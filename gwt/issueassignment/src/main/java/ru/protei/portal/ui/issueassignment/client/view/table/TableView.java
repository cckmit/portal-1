package ru.protei.portal.ui.issueassignment.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.UIObject;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.ActionIconClickColumn;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.issuefilterselector.IssueFilterSelector;
import ru.protei.portal.ui.common.client.widget.loading.IndeterminateCircleLoading;
import ru.protei.portal.ui.issueassignment.client.activity.table.AbstractTableActivity;
import ru.protei.portal.ui.issueassignment.client.activity.table.AbstractTableView;
import ru.protei.portal.ui.issueassignment.client.view.table.columns.IssueColumn;

import java.util.List;

public class TableView extends Composite implements AbstractTableView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractTableActivity activity) {
        this.activity = activity;
        initTable();
    }

    @Override
    public void clearRecords() {
        table.clearRows();
    }

    @Override
    public void putRecords(List<CaseShortView> list) {
        list.forEach(table::addRow);
    }

    @Override
    public void setTotalRecords(int totalRecords) {
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
        tableOverflowText.setInnerText(lang.issueAssignmentTableOverflow(showedRecords));
    }

    @Override
    public void hideTableOverflow() {
        tableOverflow.setVisible(false);
    }

    @Override
    public void updateFilterSelector() {
        filter.updateFilterType(En_CaseFilterType.CASE_OBJECTS);
    }

    @Override
    public HasValue<CaseFilterShortView> filter() {
        return filter;
    }

    private void initTable() {

        columnProvider = new ClickColumnProvider<>();

        IssueColumn number = new IssueColumn(lang, caseStateLang);
        table.addColumn(number.header, number.values);
        number.setHandler(value -> activity.onItemClicked(value));
        number.setColumnProvider(columnProvider);

        ActionIconClickColumn<CaseShortView> assign = new ActionIconClickColumn<>("far fa-lg fa-caret-square-right", lang.issueAssignmentIssueAssignTo(), null);
        table.addColumn(assign.header, assign.values);
        assign.setHandler(value -> activity.onItemClicked(value));
        assign.setActionHandler(new ClickColumn.Handler<CaseShortView>() {
            public void onItemClicked(CaseShortView value) {}
            public void onItemClicked(CaseShortView value, Element target) {
                activity.onItemActionAssign(value, new CustomUIObject(target));
            }
        });
        assign.setColumnProvider(columnProvider);
    }

    @UiHandler("filter")
    public void onFilterChanged(ValueChangeEvent<CaseFilterShortView> event) {
        if (activity != null) {
            activity.onFilterChanged(event.getValue());
        }
    }

    private static class CustomUIObject extends UIObject {
        CustomUIObject(Element element) {
            setElement(element);
        }
    }

    @Inject
    @UiField
    Lang lang;
    @Inject
    En_CaseStateLang caseStateLang;

    @Inject
    @UiField(provided = true)
    IssueFilterSelector filter;
    @UiField
    TableWidget<CaseShortView> table;
    @UiField
    HTMLPanel tableOverflow;
    @UiField
    SpanElement tableOverflowText;
    @UiField
    IndeterminateCircleLoading loading;

    private ClickColumnProvider<CaseShortView> columnProvider;
    private AbstractTableActivity activity;

    interface TableViewBinder extends UiBinder<HTMLPanel, TableView> {}
    private static TableViewBinder ourUiBinder = GWT.create(TableViewBinder.class);
}
