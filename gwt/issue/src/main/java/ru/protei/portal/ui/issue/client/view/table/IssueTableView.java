package ru.protei.portal.ui.issue.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.brainworm.factory.widget.table.client.helper.SelectionColumn;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.En_CaseImportanceLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanySelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.ModuleType;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
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
        search.getElement().setPropertyString( "placeholder", lang.search() );
        sortField.setType( ModuleType.ISSUE );
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
        animation.setContainers( tableContainer, previewContainer );
    }

    @Override
    public HasWidgets getPreviewContainer () { return previewContainer; }
    
    @Override
    public HasValue<EntityOption> company() {
        return company;
    }

    @Override
    public HasValue<En_SortField> sortField() {
        return sortField;
    }

    @Override
    public HasValue<Boolean> sortDir() {
        return sortDir;
    }

    @Override
    public HasValue<String> searchPattern() {
        return search;
    }

    @Override
    public void resetFilter() {
        company.setValue( null );
        sortField.setValue( En_SortField.creation_date );
        sortDir.setValue( true );
        search.setText( "" );
    }

    @Override
    public void addRecord( CaseObject issue ) {
        table.addRow( issue );
    }

    @Override
    public void clearRecords() {
        table.clearRows();
    }

    @Override
    public void setIssuesCount( Long issuesCount ) {
        table.setTotalRecords( issuesCount.intValue() );
    }

    @UiHandler( "company" )
    public void onCompanySelected( ValueChangeEvent< EntityOption > event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler("create")
    public void onCreateClick (ClickEvent event) {
        activity.onCreateClick();
    }

    @UiHandler( "sortField" )
    public void onSortFieldSelected( ValueChangeEvent< En_SortField > event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler("sortDir")
    public void onSortDirClicked( ClickEvent event ) {

        if (sortDir.getValue())
            sortDir.removeStyleName( "active" );
        else
            sortDir.addStyleName( "active" );

        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler( "search" )
    public void onKeyUpSearch( KeyUpEvent event ) {
        timer.cancel();
        timer.schedule( 300 );
    }

    private void initTable () {
        editClickColumn = new EditClickColumn< CaseObject>( lang ) {};

        issueNumber = new ClickColumn< CaseObject >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.issueNumber() );
                element.addClassName( "number" );
            }

            @Override
            public void fillColumnValue( Element element, CaseObject caseObject ) {
                com.google.gwt.dom.client.Element div = DOM.createDiv();
                com.google.gwt.dom.client.Element i = DOM.createElement("i");
                i.setClassName( "fa fa-2x fa-flag " + En_ImportanceLevel.find( caseObject.getImpLevel() ) );
                div.appendChild( i );
                com.google.gwt.dom.client.Element divNumber = DOM.createDiv();
                divNumber.setInnerText( caseObject == null ? "" : caseObject.getCaseNumber().toString() );
                divNumber.getStyle().setFontSize( 20, Style.Unit.PX );
                div.appendChild( divNumber );
                element.appendChild( div );
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
                element.setInnerText( caseObject == null ? "" : caseObject.getProduct() == null ? "" : caseObject.getProduct().getName() );
            }
        };

        contacts = new ClickColumn< CaseObject >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.issueContacts() );
                element.addClassName( "contacts" );
            }

            @Override
            public void fillColumnValue( Element element, CaseObject caseObject ) {

                Company company = caseObject == null ? null : caseObject.getInitiatorCompany();

                com.google.gwt.dom.client.Element companyElement= DOM.createDiv();
                companyElement.setClassName( "text-semibold" );
                companyElement.setInnerText( company == null ? "" : company.getCname() + ": " );
                element.appendChild( companyElement );

                Person initiator = caseObject == null ? null : caseObject.getInitiator();

                com.google.gwt.dom.client.Element initiatorElement = DOM.createDiv();
                //initiatorElement.setClassName( "text-semimuted" );
                initiatorElement.setInnerText( initiator == null ? "" : initiator.getDisplayName() );
                element.appendChild( initiatorElement );
            }
        };

        manager = new ClickColumn< CaseObject >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.issueManager() );
                element.addClassName( "manager" );
            }

            @Override
            public void fillColumnValue( Element element, CaseObject caseObject ) {

                Company company = caseObject == null ? null : caseObject.getManager() == null ? null : caseObject.getManager().getCompany();

                com.google.gwt.dom.client.Element companyElement= DOM.createDiv();
                companyElement.setClassName( "text-semibold" );
                companyElement.setInnerText( company == null ? "" : company.getCname() + ": " );
                element.appendChild( companyElement );

                Person manager = caseObject == null ? null : caseObject.getManager();

                com.google.gwt.dom.client.Element managerElement = DOM.createDiv();
                //managerElement.setClassName( "text-semimuted" );
                managerElement.setInnerText( manager == null ? "" : manager.getDisplayName() );
                element.appendChild( managerElement );
            }
        };

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

        creationDate = new ClickColumn< CaseObject >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.getStyle().setWhiteSpace( Style.WhiteSpace.NOWRAP );
                element.setInnerText( lang.issueCreationDate() );
                element.addClassName( "creation" );
            }

            @Override
            public void fillColumnValue( Element element, CaseObject caseObject ) {
                Date created = caseObject == null ? null : caseObject.getCreated();
                if ( created == null ) {
                    element.setInnerText( "" );
                } else {
                    element.setClassName( "text-semimuted" );
                    com.google.gwt.dom.client.Element i = DOM.createElement("i");
                    i.setClassName( "fa fa-clock-o" );
                    element.appendChild( i );
                    com.google.gwt.dom.client.Element span = DOM.createSpan();
                    span.setInnerText( " " + dateFormatter.formatDateOnly( created ) );
                    element.appendChild( span );
                }
            }
        };

        table.addColumn( selectionColumn.header, selectionColumn.values );
        table.addColumn( editClickColumn.header, editClickColumn.values );
        table.addColumn( issueNumber.header, issueNumber.values );
        table.addColumn( product.header, product.values );
        table.addColumn( contacts.header, contacts.values );
        table.addColumn( manager.header, manager.values );
        table.addColumn( info.header, info.values );
        table.addColumn( creationDate.header, creationDate.values );

        table.setSeparatorProvider( ( element, i ) -> {
            element.setInnerText( "Страница "+ (i+1) );
            element.addClassName( "separator" );
        } );
    }

    @Inject
    @UiField ( provided = true )
    CompanySelector company;

    @Inject
    @UiField( provided = true )
    SortFieldSelector sortField;

    @UiField
    ToggleButton sortDir;

    @UiField
    Button create;

    @UiField
    TextBox search;

    @UiField
    InfiniteTableWidget<CaseObject> table;

    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel previewContainer;

    @Inject
    @UiField
    Lang lang;

    @Inject
    DateFormatter dateFormatter;

    Timer timer = new Timer() {
        @Override
        public void run() {
            if ( activity != null ) {
                activity.onFilterChanged();
            }
        }
    };

    ClickColumnProvider<CaseObject> columnProvider = new ClickColumnProvider<>();
    SelectionColumn< CaseObject  > selectionColumn = new SelectionColumn<>();
    EditClickColumn< CaseObject > editClickColumn;
    ClickColumn< CaseObject > issueNumber;
    ClickColumn< CaseObject > product;
    ClickColumn< CaseObject > contacts;
    ClickColumn< CaseObject > info;
    ClickColumn< CaseObject > creationDate;
    ClickColumn< CaseObject > manager;

    AbstractIssueTableActivity activity;

    private static ContactTableViewUiBinder ourUiBinder = GWT.create( ContactTableViewUiBinder.class );
    interface ContactTableViewUiBinder extends UiBinder< HTMLPanel, IssueTableView > {}
}