package ru.protei.portal.ui.issue.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.brainworm.factory.widget.table.client.helper.SelectionColumn;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.issue.client.activity.table.AbstractIssueTableActivity;
import ru.protei.portal.ui.issue.client.activity.table.AbstractIssueTableView;

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
        product.setHandler( activity );
        product.setColumnProvider( columnProvider );
        contacts.setHandler( activity );
        contacts.setColumnProvider( columnProvider );
        info.setHandler( activity );
        info.setColumnProvider( columnProvider );
        creationDate.setHandler( activity );
        creationDate.setColumnProvider( columnProvider );
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
        //editClickColumn.setColumnProvider( columnProvider );

        issueNumber = new ClickColumn< CaseObject >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.issueNumber() );
                element.addClassName( "number" );
            }

            @Override
            public void fillColumnValue( Element element, CaseObject caseObject ) {
                element.setInnerText( caseObject == null ? "" : caseObject.getCaseNumber().toString() );
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
                element.setInnerText( caseObject == null ? "" : En_CaseState.getById(caseObject.getStateId()).getName() );
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
                element.setInnerText( caseObject == null ? "" : En_ImportanceLevel.getById(caseObject.getImpLevel()).getCode() );
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
                DevUnit product = caseObject == null ? null : caseObject.getProduct();
                element.setInnerText( product == null ? "" : product.getName() );
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
                element.setInnerText( companyName+separator+initiatorName );
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
                element.setInnerText( info );
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
                element.setInnerText( created == null ? "" : dateFormatter.formatDateOnly( created ) );
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
                Person manager = caseObject == null || caseObject.getManager() == null ? null : caseObject.getManager();
                element.setInnerText( manager == null ? "" : manager.getDisplayName() );
            }
        };
        //manager.setColumnProvider( columnProvider );

        table.addColumn( selectionColumn.header, selectionColumn.values );
        table.addColumn( editClickColumn.header, editClickColumn.values );
        table.addColumn( issueNumber.header, issueNumber.values );
        table.addColumn( state.header, state.values );
        table.addColumn( importance.header, importance.values );
        table.addColumn( product.header, product.values );
        table.addColumn( contacts.header, contacts.values );
        table.addColumn( info.header, info.values );
        table.addColumn( creationDate.header, creationDate.values );
        table.addColumn( manager.header, manager.values );

        table.setSeparatorProvider( ( element, i ) -> {
            element.setInnerText( "Страница "+ (i+1) );
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
    DateFormatter dateFormatter;

    ClickColumnProvider<CaseObject> columnProvider = new ClickColumnProvider<>();
    SelectionColumn< CaseObject  > selectionColumn = new SelectionColumn<>();
    EditClickColumn< CaseObject > editClickColumn;
    ClickColumn< CaseObject > issueNumber;
    ClickColumn< CaseObject > importance;
    ClickColumn< CaseObject > state;
    ClickColumn< CaseObject > product;
    ClickColumn< CaseObject > contacts;
    ClickColumn< CaseObject > info;
    ClickColumn< CaseObject > creationDate;
    ClickColumn< CaseObject > manager;

    AbstractIssueTableActivity activity;

    private static IssueTableViewUiBinder ourUiBinder = GWT.create( IssueTableViewUiBinder.class );
    interface IssueTableViewUiBinder extends UiBinder< HTMLPanel, IssueTableView > {}
}