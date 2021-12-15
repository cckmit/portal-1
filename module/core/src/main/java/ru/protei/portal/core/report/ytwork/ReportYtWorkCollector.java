package ru.protei.portal.core.report.ytwork;

import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.struct.reportytwork.ReportYtWorkClassificationError;
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
import static ru.protei.portal.core.model.dict.En_YoutrackWorkType.NIOKR;
import static ru.protei.portal.core.model.dict.En_YoutrackWorkType.NMA;
import static ru.protei.portal.core.model.helper.CollectionUtils.setOf;
import static ru.protei.portal.core.report.ytwork.ReportYtCollectorsUtils.WorkTypeAndValue;
import static ru.protei.portal.core.report.ytwork.ReportYtCollectorsUtils.mergeWithItem;

public class ReportYtWorkCollector implements Collector<
        ReportYtWorkInfo,
        ReportYtWorkCollector.ErrorsAndItems,
        ReportYtWorkCollector.ErrorsAndItems> {

    private final Map<String, List<String>> niokrs;
    private final Map<String, List<String>> nmas;
    private final Set<String> homeCompany;

    private final Function<String, Optional<WorkTypeAndValue>> getContractsAndGuarantee;

    public ReportYtWorkCollector(Map<String, List<String>> niokrs,
                                 Map<String, List<String>> nmas,
                                 Function< String, List<Contract>> getContractsByCustomer,
                                 Date now,
                                 Set<String> homeCompany) {
        this.niokrs = unmodifiableMap(niokrs == null? new HashMap<>() : new HashMap<>(niokrs));
        this.nmas = unmodifiableMap(nmas == null? new HashMap<>() : new HashMap<>(nmas));
        this.homeCompany = unmodifiableSet(new HashSet<>(homeCompany));

        Map<String, Optional<WorkTypeAndValue>> memo = new ConcurrentHashMap<>();
        this.getContractsAndGuarantee = customer ->
                ReportYtCollectorsUtils.getContractsAndGuarantee(now, memo, customer, getContractsByCustomer);
    }

    @Override
    public Supplier<ErrorsAndItems> supplier() {
        return ErrorsAndItems::new;
    }

    @Override
    public BiConsumer<ErrorsAndItems, ReportYtWorkInfo> accumulator() {
        return this::collectItems;
    }

    private void collectItems(ErrorsAndItems accumulator, ReportYtWorkInfo ytWorkInfo) {
        WorkTypeAndValue workTypeAndValue = makeWorkType(ytWorkInfo.getCustomer(), ytWorkInfo.getProject());
        if (workTypeAndValue != null) {
            ReportYtWorkRowItem reportYtWorkRowItem = accumulator.getItems().compute(ytWorkInfo.getEmail(),
                    (email, ytWorkItem) -> ytWorkItem != null ? ytWorkItem : new ReportYtWorkRowItem());
            mergeWithItem(ytWorkInfo.getSpentTime(), reportYtWorkRowItem, workTypeAndValue);
            reportYtWorkRowItem.addAllTimeSpent(ytWorkInfo.getSpentTime());
        } else {
            accumulator.getErrors().add(new ReportYtWorkClassificationError(ytWorkInfo.getIssue()));
        }
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
            return null;
        } else {
            return this.getContractsAndGuarantee.apply(customer).orElse(null);
        }
    }

    @Override
    public BinaryOperator<ErrorsAndItems> combiner() {
        return ReportYtWorkCollector::mergeCollectedItems;
    }

    static private ErrorsAndItems mergeCollectedItems(ErrorsAndItems collector1, ErrorsAndItems collector2) {
        ReportYtCollectorsUtils.mergeAccs(collector1.getItems(), collector2.getItems());
        collector1.getErrors().addAll(collector2.getErrors());
        return collector1;
    }

    @Override
    public Function<ErrorsAndItems, ErrorsAndItems> finisher() {
        return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return setOf(Characteristics.UNORDERED, Characteristics.IDENTITY_FINISH);
    }

    static public class ErrorsAndItems {
        Set<ReportYtWorkClassificationError> errors = new HashSet<>();
        Map<String, ReportYtWorkRowItem> items = new HashMap<>();

        public Set<ReportYtWorkClassificationError> getErrors() {
            return errors;
        }

        public Map<String, ReportYtWorkRowItem> getItems() {
            return items;
        }
    }
}
