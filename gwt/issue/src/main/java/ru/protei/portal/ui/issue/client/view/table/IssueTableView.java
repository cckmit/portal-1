package ru.protei.portal.ui.issue.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.AbstractColumn;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.AttachClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.separator.Separator;
import ru.protei.portal.ui.issue.client.activity.table.AbstractIssueTableActivity;
import ru.protei.portal.ui.issue.client.activity.table.AbstractIssueTableView;
import ru.protei.portal.ui.issue.client.view.table.columns.ContactColumn;
import ru.protei.portal.ui.issue.client.view.table.columns.InfoColumn;
import ru.protei.portal.ui.issue.client.view.table.columns.ManagerColumn;
import ru.protei.portal.ui.issue.client.view.table.columns.NumberColumn;


/**
 * Представление таблицы обращений
 */
public class IssueTableView extends Composite implements AbstractIssueTableView {
    @Inject
    public void onInit(EditClickColumn< CaseShortView> editClickColumn) {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        this.editClickColumn = editClickColumn;
        initTable();
    }

    @Override
    public void setActivity( AbstractIssueTableActivity activity ) {
        this.activity = activity;

        editClickColumn.setHandler( activity );
        editClickColumn.setEditHandler( activity );
        editClickColumn.setColumnProvider( columnProvider );

        attachClickColumn.setHandler(activity);
        attachClickColumn.setAttachHandler(activity);
        attachClickColumn.setColumnProvider(columnProvider);

        issueNumber.setHandler( activity );
        issueNumber.setColumnProvider( columnProvider );
        contact.setHandler( activity );
        contact.setColumnProvider( columnProvider );
        info.setHandler( activity );
        info.setColumnProvider( columnProvider );
        manager.setHandler( activity );
        manager.setColumnProvider( columnProvider );
        table.setLoadHandler( activity );
        table.setPagerListener( activity );
    }
    
    @Override
    public void setAnimation ( TableAnimation animation ) {
        animation.setContainers( tableContainer, previewContainer, filterContainer );
    }

    @Override
    public HasWidgets getPreviewContainer () { return previewContainer; }

    @Override
    public HasWidgets getFilterContainer () { return filterContainer; }


    @Override
    public void hideElements() {
        hideContact.setVisibility( false );
        hideManager.setVisibility( false );
    }

    @Override
    public void showElements() {
        hideContact.setVisibility( true );
        hideManager.setVisibility( true );
    }

    @Override
    public HasWidgets getPagerContainer() {
        return pagerContainer;
    }

    @Override
    public void clearRecords() {
        table.clearCache();
        table.clearRows();
        columnProvider.setSelectedValue(null);
    }

    @Override
    public void setIssuesCount( Long issuesCount ) {
        table.setTotalRecords( issuesCount.intValue() );
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
    public void scrollTo( int page ) {
        table.scrollToPage( page );
    }

    @Override
    public void updateRow(CaseShortView item) {
        if(item != null)
            table.updateRow(item);
    }

    private void initTable () {
        attachClickColumn = new AttachClickColumn<CaseShortView>(lang) {};
        editClickColumn.setPrivilege( En_Privilege.ISSUE_EDIT );
        issueNumber = new NumberColumn( lang, caseStateLang );
        contact = new ContactColumn( lang );
        manager = new ManagerColumn( lang );
        info = new InfoColumn( lang );

//        table.addColumn( selectionColumn.header, selectionColumn.values );
        table.addColumn( issueNumber.header, issueNumber.values );
        table.addColumn( info.header, info.values );
//        table.addColumn( contact.header, contact.values );
//        table.addColumn( managers.header, managers.values );
        hideContact = table.addColumn( contact.header, contact.values );
        hideManager = table.addColumn( manager.header, manager.values );
        table.addColumn( attachClickColumn.header, attachClickColumn.values );
        table.addColumn( editClickColumn.header, editClickColumn.values );
//        table.setSeparatorProvider( separator );
    }

    @UiField
    InfiniteTableWidget<CaseShortView> table;

    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    HTMLPanel filterContainer;

    @Inject
    @UiField
    Lang lang;
    @UiField
    HTMLPanel pagerContainer;

    @Inject
    En_CaseStateLang caseStateLang;

    @Inject
    Separator separator;

    ClickColumnProvider<CaseShortView> columnProvider = new ClickColumnProvider<>();
//    SelectionColumn< CaseShortView > selectionColumn = new SelectionColumn<>();
    EditClickColumn< CaseShortView > editClickColumn;
    AttachClickColumn< CaseShortView > attachClickColumn;
    NumberColumn issueNumber;
    ContactColumn contact;
    ManagerColumn manager;
    InfoColumn info;

    AbstractColumn hideContact;
    AbstractColumn hideManager;

    AbstractIssueTableActivity activity;

    private static IssueTableViewUiBinder ourUiBinder = GWT.create( IssueTableViewUiBinder.class );
    interface IssueTableViewUiBinder extends UiBinder< HTMLPanel, IssueTableView > {}
}