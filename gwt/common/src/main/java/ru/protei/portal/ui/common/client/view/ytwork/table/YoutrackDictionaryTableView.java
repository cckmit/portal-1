package ru.protei.portal.ui.common.client.view.ytwork.table;

import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.core.model.ent.YoutrackProject;
import ru.protei.portal.core.model.ent.YoutrackWorkDictionary;
import ru.protei.portal.ui.common.client.activity.ytwork.table.AbstractYoutrackWorkDictionaryTableActivity;
import ru.protei.portal.ui.common.client.activity.ytwork.table.AbstractYoutrackWorkDictionaryTableView;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;
import ru.protei.portal.ui.common.client.columns.StaticColumn;
import ru.protei.portal.ui.common.client.events.InputEvent;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.cleanablesearchbox.CleanableSearchBox;
import ru.protei.portal.ui.common.client.widget.loading.IndeterminateCircleLoading;

import java.util.List;
import java.util.stream.Collectors;

public class YoutrackDictionaryTableView extends Composite implements AbstractYoutrackWorkDictionaryTableView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractYoutrackWorkDictionaryTableActivity activity) {
        this.activity = activity;
        initTable();
    }

    @Override
    public void clearRecords() {
        table.clearRows();
        count.setInnerText("");
    }

    @Override
    public void putRecords(List<YoutrackWorkDictionary> list) {
        list.forEach(table::addRow);
        tableContainer.setScrollLeft(left);
        tableContainer.setScrollTop(top);
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
    public void setRecords(int filteredRecords, int totalRecords) {
        count.setInnerText("(" + filteredRecords + " / " + totalRecords + ")");
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
    public void refreshTable() {
        activity.refreshTable();
    }

    @Override
    public void presetScroll() {
        this.left = tableContainer.getScrollLeft();
        this.top = tableContainer.getScrollTop();
    }

    @Override
    public void resetScroll() {
        left = 0;
        top = 0;
    }

    @Override
    public HasValue<String> searchPattern() {
        return search;
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

    @UiHandler("search")
    public void onSearchChanged(InputEvent event) {
        activity.onSearchChanged();
    }

    private void initTable() {
        StaticColumn<YoutrackWorkDictionary> name = new StaticColumn<YoutrackWorkDictionary>() {
            @Override
            protected void fillColumnHeader(com.google.gwt.user.client.Element columnHeader) {
                columnHeader.setInnerText(lang.reportYoutrackWorkDictionaryName());
            }

            @Override
            public void fillColumnValue(com.google.gwt.user.client.Element cell, YoutrackWorkDictionary value) {
                cell.setInnerText(value.getName());
            }
        };
        StaticColumn<YoutrackWorkDictionary> projects = new StaticColumn<YoutrackWorkDictionary>() {
            @Override
            protected void fillColumnHeader(com.google.gwt.user.client.Element columnHeader) {
                columnHeader.setInnerText(lang.reportYoutrackWorkDictionaryProjects());
            }

            @Override
            public void fillColumnValue(com.google.gwt.user.client.Element cell, YoutrackWorkDictionary value) {
                cell.setInnerText(value.getYoutrackProjects().stream().map(YoutrackProject::getShortName).collect(Collectors.joining(", ")));
            }
        };

        EditClickColumn<YoutrackWorkDictionary> editClickColumn = new EditClickColumn<>(lang);
        editClickColumn.setEditHandler(activity::onEditClicked);

        RemoveClickColumn<YoutrackWorkDictionary> removeClickColumn = new RemoveClickColumn<>(lang);
        removeClickColumn.setRemoveHandler(activity::onRemoveClicked);
        
        table.addColumn(name.header, name.values);
        table.addColumn(projects.header, projects.values);
        table.addColumn(editClickColumn.header, editClickColumn.values);
        table.addColumn(removeClickColumn.header, removeClickColumn.values);
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
    CleanableSearchBox search;
    @UiField
    TableWidget<YoutrackWorkDictionary> table;
    @UiField
    DivElement tableContainer;
    @UiField
    HTMLPanel tableOverflow;
    @UiField
    SpanElement tableOverflowText;
    @UiField
    Element collapseIcon;
    @UiField
    Element headerContainer;

    @Inject
    AbstractYoutrackWorkDictionaryTableActivity activity;

    private Integer left;
    private Integer top;

    interface YoutrackReporDictionaryViewUiBinder extends UiBinder<HTMLPanel, YoutrackDictionaryTableView> {}
    private static YoutrackReporDictionaryViewUiBinder ourUiBinder = GWT.create(YoutrackReporDictionaryViewUiBinder.class);
}
