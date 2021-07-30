package ru.protei.portal.core.report.ytwork;

import ru.protei.portal.core.model.dict.En_ReportYtWorkType;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.struct.reportytwork.ReportYtWorkInfo;
import ru.protei.portal.core.model.struct.reportytwork.ReportYtWorkRowItem;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.*;
import static ru.protei.portal.core.model.dict.En_ReportYtWorkType.*;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;

public class ReportYtWorkCollector implements Collector<
        ReportYtWorkInfo,
        Map<String, ReportYtWorkRowItem>,      // email -> processed items
        Map<String, ReportYtWorkRowItem>> {

    private final Map<En_ReportYtWorkType, Set<String>> processedWorkTypes;
    private final Map<String, WorkTypeAndValue> memoCustomerToContract = new ConcurrentHashMap<>();

    private final Map<String, List<String>> niokrs;
    private final Map<String, List<String>> nmas;
    private final Function<String, List<Contract>> getContractsByCustomer;
    private final Date now;
    private final Set<String> homeCompany;
    private final String classificationErrorPrefix;

    public ReportYtWorkCollector(Map<String, List<String>> niokrs,
                                 Map<String, List<String>> nmas,
                                 Function< String, List<Contract>> getContractsByCustomer,
                                 Date now,
                                 Set<String> homeCompany,
                                 String classificationErrorPrefix) {
        this.niokrs = unmodifiableMap(new HashMap<>(niokrs));
        this.nmas = unmodifiableMap(new HashMap<>(nmas));
        this.getContractsByCustomer = getContractsByCustomer;
        this.now = now;
        this.homeCompany = unmodifiableSet(new HashSet<>(homeCompany));
        this.classificationErrorPrefix = classificationErrorPrefix;
        processedWorkTypes = new LinkedHashMap<>();
        for (En_ReportYtWorkType value : En_ReportYtWorkType.values()) {
            processedWorkTypes.put(value, new HashSet<>());
        }
    }

    @Override
    public Supplier<Map<String, ReportYtWorkRowItem>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<Map<String, ReportYtWorkRowItem>, ReportYtWorkInfo> accumulator() {
        return this::collectItems;
    }

    private void collectItems(Map<String, ReportYtWorkRowItem> accumulator, ReportYtWorkInfo ytWorkInfo) {
        ReportYtWorkRowItem reportYtWorkRowItem = accumulator.compute(ytWorkInfo.getEmail(),
                (email, ytWorkItem) -> ytWorkItem != null ? ytWorkItem : new ReportYtWorkRowItem());
        WorkTypeAndValue workTypeAndValue = makeWorkType(ytWorkInfo.getCustomer(), ytWorkInfo.getProject());
        Map<String, Long> spentTimeMap = createSpentTimeMap(ytWorkInfo.getSpentTime(), workTypeAndValue.getValue());
        Map<String, Long> itemMap = reportYtWorkRowItem.selectSpentTimeMap(workTypeAndValue.getWorkType());
        mergeSpentTimeMap(itemMap, spentTimeMap);
        processedWorkTypes.compute(workTypeAndValue.getWorkType(), (workType, value) -> {
            value.addAll(workTypeAndValue.getValue());
            return value;
        });
        reportYtWorkRowItem.addAllTimeSpent(ytWorkInfo.getSpentTime());
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
            value.add(classificationErrorPrefix + project);
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

    private WorkTypeAndValue createContractListFromCustomer(String name) {
        List<String> list = new ArrayList<>();
        list.add(classificationErrorPrefix + name);
        return new WorkTypeAndValue(CONTRACT, list);
    }

    static private En_ReportYtWorkType contractGuaranteeClassifier(Contract contract, Date now) {
        return contract.getDateValid() == null || contract.getDateValid().after(now) ? CONTRACT : GUARANTEE;
    }

    @Override
    public BinaryOperator<Map<String, ReportYtWorkRowItem>> combiner() {
        return ReportYtWorkCollector::mergeCollectedItems;
    }

    static private Map<String, ReportYtWorkRowItem> mergeCollectedItems(Map<String, ReportYtWorkRowItem> map1, Map<String, ReportYtWorkRowItem> map2) {
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
    public Function<Map<String, ReportYtWorkRowItem>, Map<String, ReportYtWorkRowItem>> finisher() {
        return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return setOf(Characteristics.UNORDERED, Characteristics.IDENTITY_FINISH);
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
