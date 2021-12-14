package ru.protei.portal.core.report.ytwork;

import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.struct.reportytwork.ReportYtWorkPortalInfo;
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

public class ReportYtWorkPortalCollector implements Collector<
        ReportYtWorkPortalInfo,
        Map<Long, ReportYtWorkRowItem>,
        Map<Long, ReportYtWorkRowItem>> {

    private final Function<Long, Optional<WorkTypeAndValue>> getContractsAndGuarantee;

    public ReportYtWorkPortalCollector(Function<Long, List<Contract>> getContractsByPlatformId, Date now) {
        Map<Long, Optional<WorkTypeAndValue>> memo = new HashMap<>();
        this.getContractsAndGuarantee = platformId ->
                ReportYtCollectorsUtils.getContractsAndGuarantee(now, memo, platformId, getContractsByPlatformId);
    }

    @Override
    public Supplier<Map<Long, ReportYtWorkRowItem>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<Map<Long, ReportYtWorkRowItem>, ReportYtWorkPortalInfo> accumulator() {
        return this::collectItems;
    }

    private void collectItems(Map<Long, ReportYtWorkRowItem> accumulator, ReportYtWorkPortalInfo info) {
        ReportYtWorkRowItem item = accumulator.compute(info.getPersonId(),
                (personId, ytWorkItem) -> ytWorkItem != null ? ytWorkItem : new ReportYtWorkRowItem());
        if (info.getSurrogatePlatformId() == null) {
            item.addHomeCompanySpentTime(info.getSpentTime());
        } else {
            Optional<WorkTypeAndValue> workTypeAndValue = this.getContractsAndGuarantee.apply(info.getSurrogatePlatformId());
            if (workTypeAndValue.isPresent()) {
                mergeWithItem(info.getSpentTime(), item, workTypeAndValue.get());
            } else {
                item.addHomeCompanySpentTime(info.getSpentTime());
            }
        }
        item.addAllTimeSpent(info.getSpentTime());
    }

    @Override
    public BinaryOperator<Map<Long, ReportYtWorkRowItem>> combiner() {
        return ReportYtWorkPortalCollector::mergeCollectedItems;
    }

    static private Map<Long, ReportYtWorkRowItem> mergeCollectedItems(Map<Long, ReportYtWorkRowItem> acc1, Map<Long, ReportYtWorkRowItem> acc2) {
        ReportYtCollectorsUtils.mergeAccs(acc1, acc2);
        return acc1;
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
