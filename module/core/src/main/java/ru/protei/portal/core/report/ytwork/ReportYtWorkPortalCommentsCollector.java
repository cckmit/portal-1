package ru.protei.portal.core.report.ytwork;

import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.struct.reportytwork.ReportYtWorkCaseCommentTimeElapsedSum;
import ru.protei.portal.core.model.struct.reportytwork.ReportYtWorkRowItem;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static ru.protei.portal.core.model.helper.CollectionUtils.setOf;
import static ru.protei.portal.core.report.ytwork.ReportYtCollectorsUtils.WorkTypeAndValue;
import static ru.protei.portal.core.report.ytwork.ReportYtCollectorsUtils.mergeWithItem;

public class ReportYtWorkPortalCommentsCollector implements Collector<
        ReportYtWorkCaseCommentTimeElapsedSum,
        Map<Long, ReportYtWorkRowItem>,
        Map<Long, ReportYtWorkRowItem>> {

    private final Function<Long, Optional<WorkTypeAndValue>> getContractsAndGuarantee;

    public ReportYtWorkPortalCommentsCollector(Function<Long, List<Contract>> getContractsByPlatformId, Date now) {
        Map<Long, Optional<WorkTypeAndValue>> memo = new HashMap<>();
        this.getContractsAndGuarantee = platformId ->
                ReportYtCollectorsUtils.getContractsAndGuarantee(now, memo, platformId, getContractsByPlatformId);
    }

    @Override
    public Supplier<Map<Long, ReportYtWorkRowItem>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<Map<Long, ReportYtWorkRowItem>, ReportYtWorkCaseCommentTimeElapsedSum> accumulator() {
        return this::collectItems;
    }

    private void collectItems(Map<Long, ReportYtWorkRowItem> accumulator, ReportYtWorkCaseCommentTimeElapsedSum sum) {
        ReportYtWorkRowItem item = accumulator.compute(sum.getPersonId(),
                (personId, ytWorkItem) -> ytWorkItem != null ? ytWorkItem : new ReportYtWorkRowItem());
        if (sum.getSurrogatePlatformId() == null) {
            item.addHomeCompanySpentTime(sum.getSpentTime());
        } else {
            // todo в коллектор должны попадать элементы с контрактами
            Optional<WorkTypeAndValue> workTypeAndValue = this.getContractsAndGuarantee.apply(sum.getSurrogatePlatformId());
            if (workTypeAndValue.isPresent()) {
                mergeWithItem(sum.getSpentTime(), item, workTypeAndValue.get());
            } else {
                System.out.println(sum);
            }
        }
        item.addAllTimeSpent(sum.getSpentTime());
    }

    @Override
    public BinaryOperator<Map<Long, ReportYtWorkRowItem>> combiner() {
        return ReportYtWorkPortalCommentsCollector::mergeCollectedItems;
    }

    static private Map<Long, ReportYtWorkRowItem> mergeCollectedItems(Map<Long, ReportYtWorkRowItem> collector1, Map<Long, ReportYtWorkRowItem> collector2) {
        ReportYtCollectorsUtils.mergeAccs(collector1, collector2);
        return collector1;
    }

    @Override
    public Function<Map<Long, ReportYtWorkRowItem>, Map<Long, ReportYtWorkRowItem>> finisher() {
        return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return setOf(Characteristics.UNORDERED, Characteristics.IDENTITY_FINISH);
    }
}
