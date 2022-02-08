package ru.protei.portal.ui.report.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.dict.En_ReportStatus;
import ru.protei.portal.core.model.dto.ReportDto;
import ru.protei.portal.ui.common.client.columns.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.report.client.activity.table.AbstractReportTableActivity;
import ru.protei.portal.ui.report.client.activity.table.AbstractReportTableView;
import ru.protei.portal.ui.report.client.view.table.columns.FilterColumn;
import ru.protei.portal.ui.report.client.view.table.columns.InfoColumn;
import ru.protei.portal.ui.report.client.view.table.columns.LoaderColumn;
import ru.protei.portal.ui.report.client.view.table.columns.NumberColumn;

public class ReportTableView extends Composite implements AbstractReportTableView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractReportTableActivity activity) {
        this.activity = activity;
        initTable();
    }

    @Override
    public void clearRecords() {
        table.clearCache();
        table.clearRows();
    }

    @Override
    public void triggerTableLoad() {
        table.setTotalRecords(table.getPageSize());
    }

    @Override
    public void setTotalRecords(int totalRecords) {
        table.setTotalRecords(totalRecords);
    }

    @Override
    public int getPageCount() {
        return table.getPageCount();
    }

    @Override
    public void scrollTo(int page) {
        table.scrollToPage(page);
    }

    @Override
    public void updateRow(ReportDto item) {
        if (item != null) {
            table.updateRow(item);
        }
    }

    @Override
    public HasWidgets getPagerContainer() {
        return pagerContainer;
    }

    private void initTable() {

        table.addColumn(numberColumn.header, numberColumn.values);

        table.addColumn(infoColumn.header, infoColumn.values);

        table.addColumn(filterColumn.header, filterColumn.values);

        table.addColumn(loaderColumn.header, loaderColumn.values);
        loaderColumn.setDisplayPredicate(value ->
                value.getReport().getStatus() == En_ReportStatus.PROCESS ||
                value.getReport().getStatus() == En_ReportStatus.CREATED);

        editClickColumn.setEditHandler( activity );
        table.addColumn(editClickColumn.header, editClickColumn.values);

        table.addColumn(cancelClickColumn.header, cancelClickColumn.values);
        cancelClickColumn.setDisplayPredicate(v -> v.getReport().getStatus() == En_ReportStatus.PROCESS);
        cancelClickColumn.setCancelHandler(activity);

        table.addColumn(refreshClickColumn.header, refreshClickColumn.values);
        refreshClickColumn.setDisplayPredicate(v ->
                v.getReport().getStatus() == En_ReportStatus.ERROR ||
                v.getReport().getStatus() == En_ReportStatus.CANCELLED);
        refreshClickColumn.setRefreshHandler(activity);

        table.addColumn(downloadClickColumn.header, downloadClickColumn.values);
        downloadClickColumn.setDisplayPredicate(v -> v.getReport().getStatus() == En_ReportStatus.READY);
        downloadClickColumn.setDownloadHandler(activity);

        table.addColumn(removeClickColumn.header, removeClickColumn.values);
        removeClickColumn.setDisplayPredicate(v -> v.getReport().getStatus() != En_ReportStatus.PROCESS);
        removeClickColumn.setRemoveHandler(activity);

        table.setLoadHandler(activity);
        table.setPagerListener(activity);
    }

    @UiField
    Lang lang;
    @UiField
    InfiniteTableWidget<ReportDto> table;
    @UiField
    HTMLPanel pagerContainer;

    @Inject
    private NumberColumn numberColumn;
    @Inject
    private InfoColumn infoColumn;
    @Inject
    private FilterColumn filterColumn;
    @Inject
    private RemoveClickColumn<ReportDto> removeClickColumn;
    @Inject
    private DownloadClickColumn<ReportDto> downloadClickColumn;
    @Inject
    private LoaderColumn<ReportDto> loaderColumn;
    @Inject
    private RefreshClickColumn<ReportDto> refreshClickColumn;
    @Inject
    private CancelClickColumn<ReportDto> cancelClickColumn;
    @Inject
    private EditClickColumn<ReportDto> editClickColumn;

    private AbstractReportTableActivity activity;

    private static ReportTableViewUiBinder ourUiBinder = GWT.create(ReportTableViewUiBinder.class);
    interface ReportTableViewUiBinder extends UiBinder<HTMLPanel, ReportTableView> {}
}
