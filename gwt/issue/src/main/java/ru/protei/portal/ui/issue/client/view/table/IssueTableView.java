package ru.protei.portal.ui.issue.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.brainworm.factory.widget.table.client.helper.SelectionColumn;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.issue.client.activity.table.AbstractIssueTableActivity;
import ru.protei.portal.ui.issue.client.activity.table.AbstractIssueTableView;
import ru.protei.portal.ui.issue.client.view.table.columns.ContactColumn;
import ru.protei.portal.ui.issue.client.view.table.columns.InfoColumn;
import ru.protei.portal.ui.issue.client.view.table.columns.ManagerColumn;
import ru.protei.portal.ui.issue.client.view.table.columns.NumberColumn;

import java.util.Date;


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

    private void initTable () {
        editClickColumn = new EditClickColumn< CaseObject>( lang ) {};
        issueNumber = new NumberColumn( lang, caseStateLang );
        contact = new ContactColumn( lang );
        manager = new ManagerColumn( lang );
        info = new InfoColumn( lang, dateFormatter );
        
        /*issueNumber = new ClickColumn< CaseObject >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.issueNumber() );
                element.addClassName( "number" );
            }

            @Override
            public void fillColumnValue( Element element, CaseObject caseObject ) {
                element.setInnerHTML( HTMLHelper.wrapDiv(
                        caseObject == null ? "" : caseObject.getCaseNumber().toString()
                ));
            }
        };
        //issueNumber.setColumnProvider( columnProvider );


        state = new ClickColumn< CaseObject >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.issueState() );
            }

            @Override
            public void fillColumnValue( Element element, CaseObject caseObject ) {
                element.setInnerHTML(HTMLHelper.wrapDiv(
                        caseObject == null ? "" : En_CaseState.getById(caseObject.getStateId()).getName()
                ));
                element.addClassName( En_CaseState.getById(caseObject.getStateId()).toString() );
            }
        };

        importance = new ClickColumn< CaseObject >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.issueImportance() );
            }

            @Override
            public void fillColumnValue( Element element, CaseObject caseObject ) {
                element.setInnerHTML(HTMLHelper.wrapDiv(
                        caseObject == null ? "" : En_ImportanceLevel.getById(caseObject.getImpLevel()).getCode()
                ));
                element.addClassName( En_ImportanceLevel.getById(caseObject.getImpLevel()).toString() );
            }
        };

        product = new ClickColumn< CaseObject >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.issueProduct() );
                element.addClassName( "product" );
            }

            @Override
            public void fillColumnValue( Element element, CaseObject caseObject ) {

                element.setInnerHTML(HTMLHelper.wrapDiv(
                        caseObject == null || caseObject.getProduct() == null ? "" : caseObject.getProduct().getName()
                ));

            }
        };
        //product.setColumnProvider( columnProvider );

        contacts = new ClickColumn< CaseObject >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.issueContacts() );
                element.addClassName( "contacts" );
            }

            @Override
            public void fillColumnValue( Element element, CaseObject caseObject ) {
                Company company = caseObject == null ? null : caseObject.getInitiatorCompany();
                String companyName = company == null ? "" : company.getCname();

                Person initiator = caseObject == null ? null : caseObject.getInitiator();
                String initiatorName = initiator == null ? "" : initiator.getDisplayName();

                String separator = companyName.isEmpty() ? "" : ":\n";
                element.setInnerHTML( HTMLHelper.wrapDiv(companyName+separator+initiatorName));
            }
        };
        //contacts.setColumnProvider( columnProvider );

        info = new ClickColumn< CaseObject >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.issueInfo() );
                element.addClassName( "info" );
            }

            @Override
            public void fillColumnValue( Element element, CaseObject caseObject ) {
                String info = caseObject == null ? "" : caseObject.getInfo();
                element.addClassName( "info" );
                element.setInnerHTML( HTMLHelper.wrapDiv(info) );
            }
        };
        //info.setColumnProvider( columnProvider );

        creationDate = new ClickColumn< CaseObject >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.issueCreationDate() );
                element.addClassName( "creation" );
            }

            @Override
            public void fillColumnValue( Element element, CaseObject caseObject ) {
                Date created = caseObject == null ? null : caseObject.getCreated();
                element.setInnerHTML( HTMLHelper.wrapDiv(created == null ? "" : dateFormatter.formatDateOnly( created )));
            }
        };
        //creationDate.setColumnProvider( columnProvider );

        manager = new ClickColumn< CaseObject >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.issueManager() );
                element.addClassName( "manager" );
            }

            @Override
            public void fillColumnValue( Element element, CaseObject caseObject ) {
                Person manager = caseObject == null ? null : caseObject.getManager();
                element.setInnerHTML( HTMLHelper.wrapDiv(manager == null ? "" : manager.getDisplayName()) );
            }
        };
        //manager.setColumnProvider( columnProvider );*/

        table.addColumn( selectionColumn.header, selectionColumn.values );
        table.addColumn( editClickColumn.header, editClickColumn.values );
        table.addColumn( issueNumber.header, issueNumber.values );
        table.addColumn( info.header, info.values );
        table.addColumn( contact.header, contact.values );
        table.addColumn( manager.header, manager.values );

        table.setSeparatorProvider( ( element, i, tableWidget ) -> {
            element.setInnerHTML( lang.dataPageNumber(i+1) );
            element.addClassName( "separator" );
        } );
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
    DateFormatter dateFormatter;

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