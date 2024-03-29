package ru.protei.portal.ui.report.client.activity.table;

import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.dto.ReportDto;
import ru.protei.portal.ui.common.client.columns.*;

public interface AbstractReportTableActivity extends
        RemoveClickColumn.RemoveHandler<ReportDto>, DownloadClickColumn.DownloadHandler<ReportDto>, RefreshClickColumn.RefreshHandler<ReportDto>,
        CancelClickColumn.CancelHandler<ReportDto>, EditClickColumn.EditHandler<ReportDto>,
        InfiniteLoadHandler<ReportDto>, InfiniteTableWidget.PagerListener
{
    void onRemoveClicked(ReportDto value);
    void onDownloadClicked(ReportDto value);
    void onRefreshClicked(ReportDto value);
    void onCancelClicked(ReportDto value);
}
