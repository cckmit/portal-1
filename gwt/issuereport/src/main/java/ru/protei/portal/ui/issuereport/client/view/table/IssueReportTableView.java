package ru.protei.portal.ui.issuereport.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.dict.En_ReportStatus;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.ui.common.client.columns.CancelClickColumn;
import ru.protei.portal.ui.common.client.columns.DownloadClickColumn;
import ru.protei.portal.ui.common.client.columns.RefreshClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;
import ru.protei.portal.ui.common.client.lang.*;
import ru.protei.portal.ui.issuereport.client.activity.table.AbstractIssueReportTableActivity;
import ru.protei.portal.ui.issuereport.client.activity.table.AbstractIssueReportTableView;
import ru.protei.portal.ui.issuereport.client.view.table.columns.FilterColumn;
import ru.protei.portal.ui.issuereport.client.view.table.columns.InfoColumn;
import ru.protei.portal.ui.issuereport.client.view.table.columns.NumberColumn;

public class IssueReportTableView extends Composite implements AbstractIssueReportTableView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        initTable();
    }

    @Override
    public void setActivity(AbstractIssueReportTableActivity activity) {
        downloadClickColumn.setDownloadHandler(activity);
        removeClickColumn.setRemoveHandler(activity);
        refreshClickColumn.setRefreshHandler(activity);
        cancelClickColumn.setCancelHandler(activity);

        table.setLoadHandler(activity);
        table.setPagerListener(activity);
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
    public void updateRow(Report item) {
        if (item != null) {
            table.updateRow(item);
        }
    }

    @Override
    public HasWidgets getPagerContainer() {
        return pagerContainer;
    }

    private void initTable() {
        numberColumn = new NumberColumn(lang, reportStatusLang);
        infoColumn = new InfoColumn(lang, reportTypeLang, scheduledTypeLang);
        filterColumn = new FilterColumn(lang, sortFieldLang, sortDirLang, caseImportanceLang, regionStateLang, intervalLang);
        refreshClickColumn.setDisplayPredicate(v -> v.getStatus() == En_ReportStatus.ERROR ||
                                                        v.getStatus() == En_ReportStatus.CANCELLED);
        removeClickColumn.setDisplayPredicate(v -> v.getStatus() != En_ReportStatus.PROCESS);
        downloadClickColumn.setDisplayPredicate(v -> v.getStatus() == En_ReportStatus.READY);
        cancelClickColumn.setDisplayPredicate(v -> v.getStatus() == En_ReportStatus.PROCESS);

        table.addColumn(numberColumn.header, numberColumn.values);
        table.addColumn(infoColumn.header, infoColumn.values);
        table.addColumn(filterColumn.header, filterColumn.values);

        table.addColumn(cancelClickColumn.header, cancelClickColumn.values);
        table.addColumn(refreshClickColumn.header, refreshClickColumn.values);
        table.addColumn(downloadClickColumn.header, downloadClickColumn.values);
        table.addColumn(removeClickColumn.header, removeClickColumn.values);
    }


    @UiField
    Lang lang;
    @UiField
    InfiniteTableWidget<Report> table;
    @UiField
    HTMLPanel pagerContainer;

    @Inject
    private RemoveClickColumn<Report> removeClickColumn;
    @Inject
    private DownloadClickColumn<Report> downloadClickColumn;
    @Inject
    private RefreshClickColumn<Report> refreshClickColumn;
    @Inject
    private CancelClickColumn<Report> cancelClickColumn;
    @Inject
    private En_ReportStatusLang reportStatusLang;
    @Inject
    private En_SortFieldLang sortFieldLang;
    @Inject
    private En_SortDirLang sortDirLang;
    @Inject
    private En_CaseImportanceLang caseImportanceLang;
    @Inject
    private En_ReportTypeLang reportTypeLang;
    @Inject
    private En_ReportScheduledTypeLang scheduledTypeLang;
    @Inject
    private En_RegionStateLang regionStateLang;
    @Inject
    private En_DateIntervalLang intervalLang;

    @Inject
    private NumberColumn numberColumn;
    @Inject
    private InfoColumn infoColumn;
    @Inject
    private FilterColumn filterColumn;

    private static IssueReportTableViewUiBinder ourUiBinder = GWT.create(IssueReportTableViewUiBinder.class);
    interface IssueReportTableViewUiBinder extends UiBinder<HTMLPanel, IssueReportTableView> {}
}
