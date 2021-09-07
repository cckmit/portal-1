package ru.protei.portal.ui.common.client.view.ytwork.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.core.model.dict.En_ReportYoutrackWorkType;
import ru.protei.portal.core.model.ent.YoutrackProject;
import ru.protei.portal.core.model.ent.YoutrackReportDictionary;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.ytwork.table.AbstractYoutrackReportDictionaryTableActivity;
import ru.protei.portal.ui.common.client.activity.ytwork.table.AbstractYoutrackReportDictionaryTableView;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;
import ru.protei.portal.ui.common.client.columns.StaticColumn;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.loading.IndeterminateCircleLoading;

import java.util.List;
import java.util.stream.Collectors;

public class YoutrackReportDictionaryTableView extends Composite implements AbstractYoutrackReportDictionaryTableView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractYoutrackReportDictionaryTableActivity activity) {
        this.activity = activity;
        initTable();
    }

    @Override
    public void clearRecords() {
        table.clearRows();
        count.setInnerText("");
    }

    @Override
    public void putRecords(List<YoutrackReportDictionary> list) {
        list.forEach(table::addRow);
    }

    @Override
    public void setName(String name) {
        this.name.setInnerText(name);
    }

    @Override
    public void setCollapsed(boolean isCollapsed) {
        if (isCollapsed){
            tableContainer.addClassName("table-container-collapsed");
            collapseIcon.replaceClassName("fa-caret-down", "fa-caret-right");
            collapse.setTitle(lang.dashboardActionExpand());
        } else {
            tableContainer.removeClassName("table-container-collapsed");
            collapseIcon.replaceClassName("fa-caret-right", "fa-caret-down");
            collapse.setTitle(lang.dashboardActionCollapse());
        }
    }

    @Override
    public void setTotalRecords(int totalRecords) {
        count.setInnerText("(" + totalRecords + ")");
    }

    @Override
    public void showLoader(boolean isShow) {
        loading.removeStyleName("d-block");
        if (isShow) {
            loading.addStyleName("d-block");
        }
    }

    @Override
    public void showTableOverflow(int showedRecords) {
        tableOverflow.setVisible(true);
        tableOverflowText.setInnerText(lang.dashboardTableOverflow(showedRecords));
    }

    @Override
    public void hideTableOverflow() {
        tableOverflow.setVisible(false);
    }

    @Override
    public void setEnsureDebugId(String debugId) {
        table.setEnsureDebugId(debugId);
    }

    @Override
    public void onShow() {
        activity.onShow();
    }

    @UiHandler("add")
    public void onOpenClicked(ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onAddClicked();
        }
    }


    @UiHandler("collapse")
    public void onCollapseClicked(ClickEvent event) {
        boolean isCollapsed = tableContainer.getClassName().contains("table-container-collapsed");

        activity.onCollapseClicked(!isCollapsed);
        setCollapsed(!isCollapsed);
    }

    private void initTable() {
        StaticColumn<YoutrackReportDictionary> name = new StaticColumn<YoutrackReportDictionary>() {
            @Override
            protected void fillColumnHeader(com.google.gwt.user.client.Element columnHeader) {
                columnHeader.setInnerText(lang.reportYoutrackWorkDictionaryName());
            }

            @Override
            public void fillColumnValue(com.google.gwt.user.client.Element cell, YoutrackReportDictionary value) {
                cell.setInnerText(value.getName());
            }
        };
        StaticColumn<YoutrackReportDictionary> type = new StaticColumn<YoutrackReportDictionary>() {
            @Override
            protected void fillColumnHeader(com.google.gwt.user.client.Element columnHeader) {
                columnHeader.setInnerText(lang.reportYoutrackWorkDictionaryType());
            }

            @Override
            public void fillColumnValue(com.google.gwt.user.client.Element cell, YoutrackReportDictionary value) {
                cell.setInnerText(value.getDictionaryType().name());
            }
        };
        StaticColumn<YoutrackReportDictionary> projects = new StaticColumn<YoutrackReportDictionary>() {
            @Override
            protected void fillColumnHeader(com.google.gwt.user.client.Element columnHeader) {
                columnHeader.setInnerText(lang.reportYoutrackWorkDictionaryProjects());
            }

            @Override
            public void fillColumnValue(com.google.gwt.user.client.Element cell, YoutrackReportDictionary value) {
                cell.setInnerText(value.getYoutrackProjects().stream().map(YoutrackProject::getShortName).collect(Collectors.joining(", ")));
            }
        };

        EditClickColumn<YoutrackReportDictionary> editClickColumn = new EditClickColumn<>(lang);
        editClickColumn.setEditHandler(activity::onEditClicked);

        RemoveClickColumn<YoutrackReportDictionary> removeClickColumn = new RemoveClickColumn<>(lang);
        removeClickColumn.setRemoveHandler(activity::onRemoveClicked);
        
        table.addColumn(name.header, name.values);
        table.addColumn(type.header, type.values);
        table.addColumn(projects.header, projects.values);
        table.addColumn(editClickColumn.header, editClickColumn.values);
        table.addColumn(removeClickColumn.header, removeClickColumn.values);
    }

    void ensureDebugIds(En_ReportYoutrackWorkType type) {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        table.setEnsureDebugId(DebugIds.YOUTRACK_WORK_REPORT.TABLE + type.getId());
    }


    @Inject
    @UiField
    Lang lang;
    @UiField
    SpanElement name;
    @UiField
    SpanElement count;
    @UiField
    Button add;
    @UiField
    Button collapse;
    @UiField
    IndeterminateCircleLoading loading;
    @UiField
    TableWidget<YoutrackReportDictionary> table;
    @UiField
    DivElement tableContainer;
    @UiField
    HTMLPanel tableOverflow;
    @UiField
    SpanElement tableOverflowText;
    @UiField
    Element collapseIcon;

    @Inject


    @UiField
    Element headerContainer;

    @Inject
    AbstractYoutrackReportDictionaryTableActivity activity;

    interface YoutrackReporDictionaryViewUiBinder extends UiBinder<HTMLPanel, YoutrackReportDictionaryTableView> {}
    private static YoutrackReporDictionaryViewUiBinder ourUiBinder = GWT.create(YoutrackReporDictionaryViewUiBinder.class);
}
