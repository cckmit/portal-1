package ru.protei.portal.core.report.ytwork;

import ru.protei.portal.core.model.dict.En_YoutrackWorkType;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.struct.reportytwork.ReportYtWorkRowItem;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.toList;
import static ru.protei.portal.core.model.dict.En_YoutrackWorkType.CONTRACT;
import static ru.protei.portal.core.model.dict.En_YoutrackWorkType.GUARANTEE;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;

class ReportYtCollectorsUtils {
    static class WorkTypeAndValue {
        final En_YoutrackWorkType workType;
        final List<String> value;

        public WorkTypeAndValue(En_YoutrackWorkType workType, List<String> value) {
            this.workType = workType;
            this.value = value;
        }

        public En_YoutrackWorkType getWorkType() {
            return workType;
        }

        public List<String> getValue() {
            return value;
        }
    }
    
    static <T> Optional<WorkTypeAndValue> getContractsAndGuarantee(
            Date now,
            Map<T, Optional<WorkTypeAndValue>> memo,
            T key,
            Function<T, List<Contract>> getContactsByKey) {
        return memo.compute(key, (keyCustomer, workTypeAndValues) -> {
            if (workTypeAndValues != null) {
                return workTypeAndValues;
            }
            List<Contract> contracts = getContactsByKey.apply(key);
            if (contracts.isEmpty()) {
                return Optional.empty();
            } else {
                Map<En_YoutrackWorkType, List<String>> mapContactGuaranteeToName = contracts.stream()
                        .collect(groupingBy(contract -> contractGuaranteeClassifier(contract, now), mapping(Contract::getNumber, toList())));
                List<String> contractNames = mapContactGuaranteeToName.get(CONTRACT);
                if (isNotEmpty(contractNames)) {
                    return Optional.of(new WorkTypeAndValue(CONTRACT, contractNames));
                }
                return Optional.of(new WorkTypeAndValue(GUARANTEE, mapContactGuaranteeToName.get(GUARANTEE)));
            }
        });
    }
    
    static En_YoutrackWorkType contractGuaranteeClassifier(Contract contract, Date now) {
        return contract.getDateValid() == null || contract.getDateValid().after(now) ? CONTRACT : GUARANTEE;
    }

    static Map<String, Long> createSpentTimeMap(Long spentTime, List<String> values) {
        long calcSpentTime = spentTime / values.size(); // todo Ошибки округления !!!
        return stream(values).collect(toMap(Function.identity(), (v) -> calcSpentTime, Long::sum));
    }

    static public void mergeSpentTimeMap(Map<String, Long> accumulatorMap, Map<String, Long> map) {
        mergeMap(accumulatorMap, map, Long::sum);
    }

    static void mergeWithItem(Long spentTime, ReportYtWorkRowItem item, WorkTypeAndValue workTypeAndValue) {
        Map<String, Long> spentTimeMap = createSpentTimeMap(spentTime, workTypeAndValue.getValue());
        Map<String, Long> itemMap = item.selectSpentTimeMap(workTypeAndValue.getWorkType());
        mergeSpentTimeMap(itemMap, spentTimeMap);
    }

    static <T> void mergeAccs(Map<T, ReportYtWorkRowItem> acc1, Map<T, ReportYtWorkRowItem> acc2) {
        mergeMap(acc1, acc2, (ytWorkItem1, ytWorkItem2) -> {
            mergeSpentTimeMap(ytWorkItem1.getNiokrSpentTime(), ytWorkItem2.getNiokrSpentTime());
            mergeSpentTimeMap(ytWorkItem1.getNmaSpentTime(), ytWorkItem2.getNmaSpentTime());
            mergeSpentTimeMap(ytWorkItem1.getContractSpentTime(), ytWorkItem2.getContractSpentTime());
            mergeSpentTimeMap(ytWorkItem1.getGuaranteeSpentTime(), ytWorkItem2.getGuaranteeSpentTime());
            return ytWorkItem1;
        });
    }
}
