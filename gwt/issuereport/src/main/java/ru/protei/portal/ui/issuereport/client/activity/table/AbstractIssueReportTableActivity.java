package ru.protei.portal.ui.issuereport.client.activity.table;

import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.ui.common.client.columns.*;

public interface AbstractIssueReportTableActivity extends
        ClickColumn.Handler<Report>, EditClickColumn.EditHandler<Report>, RemoveClickColumn.RemoveHandler<Report>, DownloadClickColumn.DownloadHandler<Report>,
        InfiniteLoadHandler<Report>, InfiniteTableWidget.PagerListener
{
    void onEditClicked(Report value);
    void onRemoveClicked(Report value);
    void onDownloadClicked(Report value);
}
