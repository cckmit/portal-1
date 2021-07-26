package ru.protei.portal.core.report.ytwork;

import ru.protei.portal.core.model.dict.En_ReportYtWorkType;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.struct.ReportYtWorkInfo;
import ru.protei.portal.core.model.struct.ReportYtWorkItem;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.*;
import java.util.stream.Collector;

import static java.util.Collections.*;
import static java.util.stream.Collectors.*;
import static ru.protei.portal.core.model.dict.En_ReportYtWorkType.*;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;

public class ReportYtWorkCollector implements Collector<
        ReportYtWorkInfo,
        Map<String, ReportYtWorkItem>,      // email -> processed items
        List<ReportYtWorkItem>> {           // collected items

    private final Map<En_ReportYtWorkType, Set<String>> processedWorkTypes;
    private final Map<String, WorkTypeAndValue> memoCustomerToContract = new ConcurrentHashMap<>();

    private final Map<String, List<String>> niokrs;
    private final Map<String, List<String>> nmas;
    private final Function<String, List<Contract>> getContractsByCustomer;
    private final Function<String, Person> getPersonByEmail;
    private final Date now;
    private final Set<String> homeCompany;

    public ReportYtWorkCollector(Map<String, List<String>> niokrs,
                                 Map<String, List<String>> nmas,
                                 Function< String, List<Contract>> getContractsByCustomer,
                                 Function<String, Person> getPersonByEmail,
                                 Date now,
                                 Set<String> homeCompany) {
        this.niokrs = unmodifiableMap(new HashMap<>(niokrs));
        this.nmas = unmodifiableMap(new HashMap<>(nmas));
        this.getContractsByCustomer = getContractsByCustomer;
        this.getPersonByEmail = getPersonByEmail;
        this.now = now;
        this.homeCompany = unmodifiableSet(new HashSet<>(homeCompany));
        processedWorkTypes = new LinkedHashMap<>();
        for (En_ReportYtWorkType value : values()) {
            processedWorkTypes.put(value, new HashSet<>());
        }
    }

    @Override
    public Supplier<Map<String, ReportYtWorkItem>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<Map<String, ReportYtWorkItem>, ReportYtWorkInfo> accumulator() {
        return this::collectItems;
    }

    private void collectItems(Map<String, ReportYtWorkItem> accumulator, ReportYtWorkInfo ytWorkInfo) {
        ReportYtWorkItem reportYtWorkItem = accumulator.compute(ytWorkInfo.getEmail(),
                (email, ytWorkItem) -> ytWorkItem != null ? ytWorkItem : new ReportYtWorkItem());
        WorkTypeAndValue workTypeAndValue = makeWorkType(ytWorkInfo.getCustomer(), ytWorkInfo.getProject());
        Map<String, Long> spentTimeMap = createSpentTimeMap(ytWorkInfo.getSpentTime(), workTypeAndValue.getValue());
        Map<String, Long> itemMap = reportYtWorkItem.selectSpentTimeMap(workTypeAndValue.getWorkType());
        mergeSpentTimeMap(itemMap, spentTimeMap);
        processedWorkTypes.compute(workTypeAndValue.getWorkType(), (workType, value) -> {
            value.addAll(workTypeAndValue.getValue());
            return value;
        });
        reportYtWorkItem.addAllTimeSpent(ytWorkInfo.getSpentTime());
    }

    static private Map<String, Long> createSpentTimeMap(Long spentTime, List<String> values) {
        long calcSpentTime = spentTime / values.size(); // todo Ошибки округления
        return stream(values).collect(toMap(Function.identity(), (v) -> calcSpentTime, Long::sum));
    }

    static private Map<String, Long> mergeSpentTimeMap(Map<String, Long> map1, Map<String, Long> map2) {
        return mergeMap(map1, map2, Long::sum);
    }

    private boolean isHomeCompany(String company) {
        return homeCompany.contains(company);
    }

    private WorkTypeAndValue makeWorkType(String customer, String project) {
        if (isHomeCompany(customer)) {
            List<String> strings = niokrs.get(project);
            if (strings != null) {
                return new WorkTypeAndValue(NIOKR, strings);
            }
            strings = nmas.get(project);
            if (strings != null) {
                return new WorkTypeAndValue(NMA, strings);
            }
            ArrayList<String> value = new ArrayList<>();
            value.add("CLASSIFICATION ERROR - " + project);
            return new WorkTypeAndValue(NIOKR, value);
        } else {
            return getContractsAndGuarantee(customer);
        }
    }

    private WorkTypeAndValue getContractsAndGuarantee(String customer) {
        return memoCustomerToContract.compute(customer, (keyCustomer, workTypeAndValues) -> {
            if (workTypeAndValues != null) {
                return workTypeAndValues;
            }
            List<Contract> contracts = getContractsByCustomer.apply(customer);
            if (contracts.isEmpty()) {
                return createContractListFromCustomer(keyCustomer);
            } else {
                Map<En_ReportYtWorkType, List<String>> mapContactGuaranteeToName = contracts.stream()
                        .collect(groupingBy(contract -> contractGuaranteeClassifier(contract, now), mapping(Contract::getNumber, toList())));
                List<String> contractNames = mapContactGuaranteeToName.get(CONTRACT);
                if (isNotEmpty(contractNames)) {
                    return new WorkTypeAndValue(CONTRACT, contractNames);
                }
                return new WorkTypeAndValue(GUARANTEE, mapContactGuaranteeToName.get(GUARANTEE));
            }
        });
    }

    static private WorkTypeAndValue createContractListFromCustomer(String name) {
        List<String> list = new ArrayList<>();
        list.add("CLASSIFICATION ERROR - " + name);
        return new WorkTypeAndValue(CONTRACT, list);
    }

    static private En_ReportYtWorkType contractGuaranteeClassifier(Contract contract, Date now) {
        return contract.getDateValid() == null || contract.getDateValid().after(now) ? CONTRACT : GUARANTEE;
    }

    @Override
    public BinaryOperator<Map<String, ReportYtWorkItem>> combiner() {
        return ReportYtWorkCollector::mergeCollectedItems;
    }

    static private Map<String, ReportYtWorkItem> mergeCollectedItems(Map<String, ReportYtWorkItem> map1, Map<String, ReportYtWorkItem> map2) {
        mergeMap(map1, map2, (ytWorkItem1, ytWorkItem2) -> {
            mergeSpentTimeMap(ytWorkItem1.getNiokrSpentTime(), ytWorkItem2.getNiokrSpentTime());
            mergeSpentTimeMap(ytWorkItem1.getNmaSpentTime(), ytWorkItem2.getNmaSpentTime());
            mergeSpentTimeMap(ytWorkItem1.getContractSpentTime(), ytWorkItem2.getContractSpentTime());
            mergeSpentTimeMap(ytWorkItem1.getGuaranteeSpentTime(), ytWorkItem2.getGuaranteeSpentTime());
            return ytWorkItem1;
        });
        return map1;
    }

    @Override
    public Function<Map<String, ReportYtWorkItem>, List<ReportYtWorkItem>> finisher() {
        return map -> {
            map.forEach((email, ytWorkItem) -> ytWorkItem.setPerson(getPersonByEmail.apply(email)));
            return new ArrayList<>(map.values());
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }

    public Map<En_ReportYtWorkType, Set<String>> getProcessedWorkTypes() {
        return processedWorkTypes;
    }

    static private class WorkTypeAndValue {
        final En_ReportYtWorkType workType;
        final List<String> value;

        public WorkTypeAndValue(En_ReportYtWorkType workType, List<String> value) {
            this.workType = workType;
            this.value = value;
        }

        public En_ReportYtWorkType getWorkType() {
            return workType;
        }

        public List<String> getValue() {
            return value;
        }
    }
}
