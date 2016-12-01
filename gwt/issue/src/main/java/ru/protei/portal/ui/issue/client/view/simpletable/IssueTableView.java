package ru.protei.portal.ui.issue.client.view.simpletable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.brainworm.factory.widget.table.client.helper.SelectionColumn;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.issue.client.activity.simpletable.AbstractIssueTableActivity;
import ru.protei.portal.ui.issue.client.activity.simpletable.AbstractIssueTableView;
import ru.protei.portal.ui.issue.client.view.table.columns.ContactColumn;
import ru.protei.portal.ui.issue.client.view.table.columns.InfoColumn;
import ru.protei.portal.ui.issue.client.view.table.columns.ManagerColumn;
import ru.protei.portal.ui.issue.client.view.table.columns.NumberColumn;

import java.util.List;

/**
 * Представление таблицы обращений
 */
public class IssueTableView extends Composite implements AbstractIssueTableView {
    public IssueTableView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        initTable();
    }

    @Override
    public void clearRecords() {
        table.clearRows();
    }

    @Override
    public void putRecords(List<CaseObject> cases){
        cases.forEach(table::addRow);
    }

    @Override
    public void setActivity(AbstractIssueTableActivity activity) {
        this.activity = activity;
        editClickColumn.setHandler( activity );
        editClickColumn.setEditHandler( activity );
        editClickColumn.setColumnProvider( columnProvider );
        issueNumber.setHandler( activity );
        issueNumber.setColumnProvider( columnProvider );
        contact.setHandler( activity );
        contact.setColumnProvider( columnProvider );
        info.setHandler( activity );
        info.setColumnProvider( columnProvider );
        manager.setHandler( activity );
        manager.setColumnProvider( columnProvider );
    }

    private void initTable () {
        editClickColumn = new EditClickColumn< CaseObject>( lang ) {};
        issueNumber = new NumberColumn( lang, caseStateLang );
        contact = new ContactColumn( lang );
        manager = new ManagerColumn( lang );
        info = new InfoColumn( lang, dateFormatter );

        table.addColumn( selectionColumn.header, selectionColumn.values );
        table.addColumn( editClickColumn.header, editClickColumn.values );
        table.addColumn( issueNumber.header, issueNumber.values );
        table.addColumn( info.header, info.values );
        table.addColumn( contact.header, contact.values );
        table.addColumn( manager.header, manager.values );
    }


    ClickColumnProvider<CaseObject> columnProvider = new ClickColumnProvider<>();
    SelectionColumn< CaseObject  > selectionColumn = new SelectionColumn<>();
    EditClickColumn< CaseObject > editClickColumn;
    NumberColumn issueNumber;
    ContactColumn contact;
    ManagerColumn manager;
    InfoColumn info;

    AbstractIssueTableActivity activity;

    @UiField
    HTMLPanel tableContainer;
    @UiField
    TableWidget<CaseObject> table;

    @Inject
    DateFormatter dateFormatter;
    @Inject
    En_CaseStateLang caseStateLang;
    @Inject
    @UiField
    Lang lang;

    interface IssueTableViewUiBinder extends UiBinder<HTMLPanel, IssueTableView> {}
    private static IssueTableViewUiBinder ourUiBinder = GWT.create(IssueTableViewUiBinder.class);
}