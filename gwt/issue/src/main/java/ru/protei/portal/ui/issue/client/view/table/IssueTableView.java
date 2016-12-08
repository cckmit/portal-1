package ru.protei.portal.ui.issue.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.brainworm.factory.widget.table.client.helper.SelectionColumn;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.common.DateFormatter;
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
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        initTable();
    }

    @Override
    public void setActivity( AbstractIssueTableActivity activity ) {
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
    public void clearRecords() {
        table.clearCache();
        table.clearRows();
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

    private void initTable () {
        editClickColumn = new EditClickColumn< CaseObject>( lang ) {};
        issueNumber = new NumberColumn( lang, caseStateLang );
        contact = new ContactColumn( lang );
        manager = new ManagerColumn( lang );
        info = new InfoColumn( lang );

        table.addColumn( selectionColumn.header, selectionColumn.values );
        table.addColumn( issueNumber.header, issueNumber.values );
        table.addColumn( info.header, info.values );
        table.addColumn( contact.header, contact.values );
        table.addColumn( manager.header, manager.values );
        table.addColumn( editClickColumn.header, editClickColumn.values );
//        table.setSeparatorProvider( separator );
    }

    @UiField
    InfiniteTableWidget<CaseObject> table;

    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    HTMLPanel filterContainer;

    @Inject
    @UiField
    Lang lang;

    @Inject
    En_CaseStateLang caseStateLang;

    @Inject
    Separator separator;

    ClickColumnProvider<CaseObject> columnProvider = new ClickColumnProvider<>();
    SelectionColumn< CaseObject  > selectionColumn = new SelectionColumn<>();
    EditClickColumn< CaseObject > editClickColumn;
    NumberColumn issueNumber;
    ContactColumn contact;
    ManagerColumn manager;
    InfoColumn info;

    AbstractIssueTableActivity activity;

    private static IssueTableViewUiBinder ourUiBinder = GWT.create( IssueTableViewUiBinder.class );
    interface IssueTableViewUiBinder extends UiBinder< HTMLPanel, IssueTableView > {}
}