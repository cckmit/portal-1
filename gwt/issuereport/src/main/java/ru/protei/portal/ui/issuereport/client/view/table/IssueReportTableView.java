package ru.protei.portal.ui.issuereport.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.AbstractColumn;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.DownloadClickColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
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
        downloadClickColumn.setHandler(activity);
        downloadClickColumn.setRemoveHandler(activity);
        downloadClickColumn.setColumnProvider(columnProvider);

        removeClickColumn.setHandler(activity);
        removeClickColumn.setRemoveHandler(activity);
        removeClickColumn.setColumnProvider(columnProvider);

        editClickColumn.setHandler(activity);
        editClickColumn.setEditHandler(activity);
        editClickColumn.setColumnProvider(columnProvider);

        numberColumn.setHandler(activity);
        numberColumn.setColumnProvider(columnProvider);

        infoColumn.setHandler(activity);
        infoColumn.setColumnProvider(columnProvider);

        filterColumn.setHandler(activity);
        filterColumn.setColumnProvider(columnProvider);

        table.setLoadHandler(activity);
        table.setPagerListener(activity);
    }

    @Override
    public void clearRecords() {
        table.clearCache();
        table.clearRows();
    }

    @Override
    public void setReportsCount(Long issuesCount) {
        table.setTotalRecords(issuesCount.intValue());
    }

    @Override
    public int getPageSize() {
        return table.getPageSize();
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
    public void hideElements() {
        hideFilterColumn.setVisibility(false);
    }

    @Override
    public void showElements() {
        hideFilterColumn.setVisibility(true);
    }

    private void initTable() {
        numberColumn = new NumberColumn(lang, reportStatusLang);
        infoColumn = new InfoColumn(lang);
        filterColumn = new FilterColumn(lang, sortFieldLang, sortDirLang, caseImportanceLang, caseStateLang);

        table.addColumn(numberColumn.header, numberColumn.values);
        table.addColumn(infoColumn.header, infoColumn.values);
        hideFilterColumn = table.addColumn(filterColumn.header, filterColumn.values);

        table.addColumn(removeClickColumn.header, removeClickColumn.values);
        table.addColumn(downloadClickColumn.header, downloadClickColumn.values);
//        table.addColumn(editClickColumn.header, editClickColumn.values);
    }

    @UiField
    InfiniteTableWidget<Report> table;
    @UiField
    HTMLPanel tableContainer;

    @Inject
    private EditClickColumn<Report> editClickColumn;
    @Inject
    private RemoveClickColumn<Report> removeClickColumn;
    @Inject
    private DownloadClickColumn<Report> downloadClickColumn;
    private ClickColumnProvider<Report> columnProvider = new ClickColumnProvider<>();
    private NumberColumn numberColumn;
    private InfoColumn infoColumn;
    private FilterColumn filterColumn;
    private AbstractColumn hideFilterColumn;

    @Inject
    @UiField
    Lang lang;
    @Inject
    private En_ReportStatusLang reportStatusLang;
    @Inject
    private En_SortFieldLang sortFieldLang;
    @Inject
    private En_SortDirLang sortDirLang;
    @Inject
    private En_CaseImportanceLang caseImportanceLang;
    @Inject
    private En_CaseStateLang caseStateLang;

    private static IssueReportTableViewUiBinder ourUiBinder = GWT.create(IssueReportTableViewUiBinder.class);
    interface IssueReportTableViewUiBinder extends UiBinder<HTMLPanel, IssueReportTableView> {}
}
