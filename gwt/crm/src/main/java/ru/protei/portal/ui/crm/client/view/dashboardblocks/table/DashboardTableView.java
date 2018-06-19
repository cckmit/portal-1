package ru.protei.portal.ui.crm.client.view.dashboardblocks.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.crm.client.activity.dashboardblocks.table.AbstractDashboardTableActivity;
import ru.protei.portal.ui.crm.client.activity.dashboardblocks.table.AbstractDashboardTableView;
import ru.protei.portal.ui.crm.client.view.dashboardblocks.table.columns.ContactColumn;
import ru.protei.portal.ui.crm.client.view.dashboardblocks.table.columns.ManagerColumn;
import ru.protei.portal.ui.crm.client.widget.importance.btngroup.CustomImportanceBtnGroupMulti;
import ru.protei.portal.ui.issue.client.view.table.columns.InfoColumn;
import ru.protei.portal.ui.issue.client.view.table.columns.NumberColumn;

import java.util.List;
import java.util.Set;

/**
 * Представление таблицы кейсов
 */
public class DashboardTableView extends Composite implements AbstractDashboardTableView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        initTable();
        importance.init(
                "importance importance-lg",
                "dashboard-importance-filter-btn",
                false,
                null
        );
    }

    @Override
    public void clearRecords() {
        table.clearRows();
    }

    @Override
    protected void onDetach() {
        activity.removeView(this);
        super.onDetach();
    }

    @Override
    public void putRecords(List<CaseShortView> cases){
//        table.addRow(cases.get(0));
        cases.forEach(table::addRow);
    }

    @Override
    public void setActivity(AbstractDashboardTableActivity activity) {
        this.activity = activity;
        issueNumber.setHandler( activity );
        issueNumber.setColumnProvider( columnProvider );
        contact.setHandler( activity );
        contact.setColumnProvider( columnProvider );
        info.setHandler( activity );
        info.setColumnProvider( columnProvider );
        manager.setHandler(activity);
        manager.setColumnProvider(columnProvider);
    }

    @Override
    public void setSectionName(String name) {
        sectionName.setInnerText(name);
    }

    @Override
    public void setRecordsCount(int number) {
        count.setInnerText(String.valueOf(number));
    }

    @Override
    public void setFastOpenEnabled(boolean enabled) {
        fastOpen.setEnabled(enabled);
        fastOpen.setVisible(enabled);
    }

    @Override
    public void showLoader(boolean isShow){
        if(isShow)
            loader.addClassName("active");
        else
            loader.removeClassName("active");
    }

    @Override
    public HasValue<Set<En_ImportanceLevel>> getImportance() {
        return importance;
    }

    @UiHandler( "importance" )
    public void onInactiveRecordsImportanceSelected( ValueChangeEvent<Set<En_ImportanceLevel>> event ) {
        activity.updateImportance(this, event.getValue());
    }

    @UiHandler( "fastOpen" )
    public void onFastOpenClicked(ClickEvent event) {
        event.preventDefault();

        if (activity != null) {
            activity.onFastOpenClicked(this);
        }
    }

    private void initTable () {
        issueNumber = new NumberColumn( lang, caseStateLang );
        contact = new ContactColumn( lang );
        manager = new ManagerColumn( lang );
        info = new InfoColumn( lang );
        table.addColumn( issueNumber.header, issueNumber.values );
        table.addColumn( info.header, info.values );
        table.addColumn( contact.header, contact.values );
        table.addColumn( manager.header, manager.values );
    }

    ClickColumnProvider<CaseShortView> columnProvider = new ClickColumnProvider<>();
    NumberColumn issueNumber;
    ContactColumn contact;
    ManagerColumn manager;
    InfoColumn info;

    AbstractDashboardTableActivity activity;
    

    @Inject
    En_CaseStateLang caseStateLang;
    @Inject
    Lang lang;

    @UiField
    SpanElement sectionName;
    @UiField
    SpanElement count;
    @UiField
    Anchor fastOpen;
    @Inject
    @UiField( provided = true )
    CustomImportanceBtnGroupMulti importance;
    @UiField
    DivElement loader;
    @UiField
    HTMLPanel tableContainer;
    @UiField
    TableWidget<CaseShortView> table;

    interface CaseTableViewUiBinder extends UiBinder<HTMLPanel, DashboardTableView> {}
    private static CaseTableViewUiBinder ourUiBinder = GWT.create(CaseTableViewUiBinder.class);
}