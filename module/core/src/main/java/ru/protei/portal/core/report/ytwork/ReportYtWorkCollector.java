package ru.protei.portal.core.report.ytwork;

import ru.protei.portal.core.model.dict.En_ReportYtWorkType;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.struct.ReportYtWorkInfo;
import ru.protei.portal.core.model.struct.ReportYtWorkItem;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.Collections.*;
import static java.util.stream.Collectors.*;
import static ru.protei.portal.core.model.dict.En_ReportYtWorkType.*;
import static ru.protei.portal.core.model.helper.CollectionUtils.mergeMap;
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.core.model.struct.ReportYtWorkItem.RepresentTime;

public class ReportYtWorkCollector implements Collector<
        ReportYtWorkInfo,
        Map<String, ReportYtWorkItem>,
        List<ReportYtWorkItem>> {

    private final Map<En_ReportYtWorkType, Set<String>> processedWorkTypes;
    private final Map<String, List<WorkTypeAndValue>> memoCustomerToContract = new ConcurrentHashMap<>();

    private final Map<String, List<String>> niokrs;
    private final Map<String, List<String>> nmas;
    private final Function<String, List<Contract>> getContractsByCustomer;
    private final Function<String, Person> getPersonByEmail;
    private final Date now;
    private final Set<String> homeCompany;

    public ReportYtWorkCollector(Map<String, List<String>> niokrs,
                                 Map<String, List<String>> nmas,
                                 Function<String, List<Contract>> getContractsByCustomer,
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
        processedWorkTypes.put(NIOKR, new HashSet<>());
        processedWorkTypes.put(NMA, new HashSet<>());
        processedWorkTypes.put(CONTRACT, new HashSet<>());
        processedWorkTypes.put(GUARANTEE, new HashSet<>());
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
        List<WorkTypeAndValue> workTypeAndValueList = makeWorkType(ytWorkInfo.getCustomer(), ytWorkInfo.getProject());
        for (WorkTypeAndValue workTypeAndValue : workTypeAndValueList) {
            Map<String, Long> spentTimeMap = createSpentTimeMap(ytWorkInfo.getSpentTime(), workTypeAndValue.getValue());
            switch(workTypeAndValue.getWorkType()) {
                case NIOKR: mergeSpentTimeMap(reportYtWorkItem.getNiokrSpentTime(), spentTimeMap); break;
                case NMA: mergeSpentTimeMap(reportYtWorkItem.getNmaSpentTime(), spentTimeMap); break;
                case CONTRACT: mergeSpentTimeMap(reportYtWorkItem.getContractSpentTime(), spentTimeMap); break;
                case GUARANTEE: mergeSpentTimeMap(reportYtWorkItem.getGuaranteeSpentTime(), spentTimeMap); break;
            }
            reportYtWorkItem.getIssueSpentTime().merge(ytWorkInfo.getIssue(),new RepresentTime(ytWorkInfo.getSpentTime()), RepresentTime::sum);
            reportYtWorkItem.addAllTimeSpent(ytWorkInfo.getSpentTime());
            processedWorkTypes.compute(workTypeAndValue.getWorkType(), (workType, value) -> {
                if (value == null) {
                    value = new HashSet<>();
                }
                value.addAll(workTypeAndValue.getValue());
                return value;
            });
        }
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

    private List<WorkTypeAndValue> makeWorkType(String customer, String project) {
        if (isHomeCompany(customer)) {
            List<String> strings = niokrs.get(project);
            if (strings != null) {
                return singletonList(new WorkTypeAndValue(NIOKR, strings));
            }
            strings = nmas.get(project);
            if (strings != null) {
                return singletonList(new WorkTypeAndValue(NMA, strings));
            }
            ArrayList<String> value = new ArrayList<>();
            value.add("CLASSIFICATION ERROR - " + project);
            return singletonList(new WorkTypeAndValue(NIOKR, value));
        } else {
            return getContractsAndGuarantee(customer);
        }
    }

    private List<WorkTypeAndValue> getContractsAndGuarantee(String customer) {
        return memoCustomerToContract.compute(customer, (keyCustomer, workTypeAndValues) -> {
            if (workTypeAndValues != null) {
                return workTypeAndValues;
            }
            List<Contract> contracts = getContractsByCustomer.apply(keyCustomer);
            if (contracts.isEmpty()) {
                return singletonList(createContractListFromCustomer(keyCustomer));
            } else {
                Map<En_ReportYtWorkType, List<String>> mapContactGuaranteeToName = contracts.stream()
                        .collect(groupingBy(contract -> contractGuaranteeClassifier(contract, now), mapping(Contract::getNumber, toList())));
                return mapContactGuaranteeToName.entrySet().stream()
                        .map(entry -> new WorkTypeAndValue(entry.getKey(), entry.getValue())).collect(toList());
            }
        });
    }

    static private WorkTypeAndValue createContractListFromCustomer(String customer) {
        List<String> list = new ArrayList<>();
        list.add("CLASSIFICATION ERROR - " + customer);
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
        mergeMap(map1, map2, (dto1, dto2) -> {
            mergeSpentTimeMap(dto1.getNiokrSpentTime(), dto2.getNiokrSpentTime());
            mergeSpentTimeMap(dto1.getNmaSpentTime(), dto2.getNmaSpentTime());
            mergeSpentTimeMap(dto1.getContractSpentTime(), dto2.getContractSpentTime());
            mergeSpentTimeMap(dto1.getGuaranteeSpentTime(), dto2.getGuaranteeSpentTime());
            mergeMap(dto1.getIssueSpentTime(), dto2.getIssueSpentTime(), RepresentTime::sum);
            return dto1;
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
        return new HashSet<>();
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
