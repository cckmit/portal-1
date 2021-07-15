package ru.protei.portal.app.portal.server.portal1794;

import ru.protei.portal.core.model.ent.Contract;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.stream.Collectors.*;
import static ru.protei.portal.app.portal.server.portal1794.PORTAL1794.WorkType.*;
import static ru.protei.portal.core.model.helper.CollectionUtils.mergeMap;

public class Collector1794 implements Collector<
        PORTAL1794.YtReportItem,
        Map<String, PORTAL1794.ReportDTO>,
        Map<String, PORTAL1794.ReportDTO>> {

    private final Map<PORTAL1794.WorkType, Set<String>> processedWorkTypes = new HashMap<>();
    private final Map<String, List<WorkTypeAndValue>> memoCustomerToContract = new HashMap<>();
    private final Map<String, List<String>> invertResearchesMap;
    private final Map<String, List<String>> invertSoftMap;
    private final Function<String, List<Contract>> getContractsByCustomer;

    public Collector1794(Map<String, List<String>> invertResearchesMap, Map<String, List<String>> invertSoftMap, Function<String, List<Contract>> getContractsByCustomer) {
        this.invertResearchesMap = invertResearchesMap;
        this.invertSoftMap = invertSoftMap;
        this.getContractsByCustomer = getContractsByCustomer;
    }

    @Override
    public Supplier<Map<String, PORTAL1794.ReportDTO>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<Map<String, PORTAL1794.ReportDTO>, PORTAL1794.YtReportItem> accumulator() {
        return (accumulator, ytReportItem) -> collectItems(processedWorkTypes, invertResearchesMap, invertSoftMap, accumulator, ytReportItem);
    }

    private void collectItems(Map<PORTAL1794.WorkType, Set<String>> processedWorkTypes,
                             Map<String, List<String>> invertResearchesMap,
                             Map<String, List<String>> invertSoftMap,
                             Map<String, PORTAL1794.ReportDTO> accumulator,
                             PORTAL1794.YtReportItem ytReportItem) {
        PORTAL1794.ReportDTO reportDTO = accumulator.compute(ytReportItem.getEmail(), (email, dto) -> dto != null ? dto : new PORTAL1794.ReportDTO());
        List<WorkTypeAndValue> workTypeAndValueList = makeWorkType(invertResearchesMap, invertSoftMap, ytReportItem.getCustomer(), ytReportItem.getProject());
        workTypeAndValueList.forEach(workTypeAndValue -> {
            Map<String, Long> spentTimeMap = createSpentTimeMap(ytReportItem.getSpentTime(), workTypeAndValue.getValue());
            switch(workTypeAndValue.getWorkType()) {
                case NIOKR: mergeSpentTimeMap(reportDTO.getNiokrSpentTime(), spentTimeMap); break;
                case NMA: mergeSpentTimeMap(reportDTO.getNmaSpentTime(), spentTimeMap); break;
                case CONTRACT: mergeSpentTimeMap(reportDTO.getContractSpentTime(), spentTimeMap); break;
                case GUARANTEE: mergeSpentTimeMap(reportDTO.getGuaranteeSpentTime(), spentTimeMap); break;
            }
            reportDTO.getIssueSpentTime().merge(ytReportItem.getIssue(), new PORTAL1794.RepresentTime(ytReportItem.getSpentTime()), PORTAL1794.RepresentTime::sum);
            processedWorkTypes.compute(workTypeAndValue.workType, (workType, value) -> {
                if (value == null) {
                    value = new HashSet<>();
                }
                value.addAll(workTypeAndValue.getValue());
                return value;
            });
        });
    }

    static private Map<String, Long> createSpentTimeMap(Long spentTime, List<String> values) {
        Map<String, Long> spentTimeMap = new HashMap<>();
        Long calcSpentTime = spentTime / values.size(); // Ошибки округления
        values.forEach(v -> spentTimeMap.put(v, calcSpentTime));
        return spentTimeMap;
    }

    static private Map<String, Long> mergeSpentTimeMap(Map<String, Long> map1, Map<String, Long> map2) {
        return mergeMap(map1, map2, Long::sum);
    }

    static private final Set<String> homeCompany = new HashSet<>(Arrays.asList(null, "НТЦ Протей", "Нет заказчика", "Протей СТ"));
    static private boolean isHomeCompany(String company) {
        return homeCompany.contains(company);
    }

    private List<WorkTypeAndValue> makeWorkType(Map<String, List<String>> invertResearchesMap, Map<String, List<String>> invertSoftMap,
                                                          String customer, String project) {
        if (isHomeCompany(customer)) {
            List<String> strings = invertResearchesMap.get(project);
            if (strings != null) {
                return Collections.singletonList(new WorkTypeAndValue(NIOKR, strings));
            }
            strings = invertSoftMap.get(project);
            if (strings != null) {
                return Collections.singletonList(new WorkTypeAndValue(NMA, strings));
            }
            ArrayList<String> value = new ArrayList<>();
            value.add("CLASSIFICATION ERROR - " + project);
            return Collections.singletonList(new WorkTypeAndValue(NIOKR, value));
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
                return Collections.singletonList(createContractListFromCustomer(keyCustomer));
            } else {
                Date now = new Date();
                Map<PORTAL1794.WorkType, List<String>> mapContactGuaranteeToName = contracts.stream()
                        .collect(groupingBy(contract -> contractGuaranteeClassifier(contract, now), mapping(Contract::getNumber, toList())));
                return mapContactGuaranteeToName.entrySet().stream()
                        .map(entry -> new WorkTypeAndValue(entry.getKey(), entry.getValue())).collect(toList());
            }
        });
    }

    static WorkTypeAndValue createContractListFromCustomer(String customer) {
        List<String> list = new ArrayList<>();
        list.add("CLASSIFICATION ERROR - " + customer);
        return new WorkTypeAndValue(CONTRACT, list);
    }

    static PORTAL1794.WorkType contractGuaranteeClassifier(Contract contract, Date now) {
        return contract.getDateValid() == null || contract.getDateValid().after(now) ? CONTRACT : GUARANTEE;
    }

    @Override
    public BinaryOperator<Map<String, PORTAL1794.ReportDTO>> combiner() {
        return Collector1794::mergeCollectedItems;
    }

    static Map<String, PORTAL1794.ReportDTO> mergeCollectedItems(Map<String, PORTAL1794.ReportDTO> map1, Map<String, PORTAL1794.ReportDTO> map2) {
        mergeMap(map1, map2, (dto1, dto2) -> {
            mergeSpentTimeMap(dto1.getNiokrSpentTime(), dto2.getNiokrSpentTime());
            mergeSpentTimeMap(dto1.getNmaSpentTime(), dto2.getNmaSpentTime());
            mergeSpentTimeMap(dto1.getContractSpentTime(), dto2.getContractSpentTime());
            mergeSpentTimeMap(dto1.getGuaranteeSpentTime(), dto2.getGuaranteeSpentTime());
            mergeMap(dto1.getIssueSpentTime(), dto2.getIssueSpentTime(), PORTAL1794.RepresentTime::sum);
            return dto1;
        });
        return map1;
    }

    @Override
    public Function<Map<String, PORTAL1794.ReportDTO>, Map<String, PORTAL1794.ReportDTO>> finisher() {
        return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.singleton(Characteristics.IDENTITY_FINISH);
    }

    static class WorkTypeAndValue {
        final PORTAL1794.WorkType workType;
        final List<String> value;

        public WorkTypeAndValue(PORTAL1794.WorkType workType, List<String> value) {
            this.workType = workType;
            this.value = value;
        }

        public PORTAL1794.WorkType getWorkType() {
            return workType;
        }

        public List<String> getValue() {
            return value;
        }
    }
}
