package ru.protei.portal.ui.issue.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.brainworm.factory.widget.table.client.helper.SelectionColumn;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.common.DateFormatter;
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
        issueNumber.setHandler( activity );
        product.setHandler( activity );
        contacts.setHandler( activity );
        info.setHandler( activity );
        creationDate.setHandler( activity );
        manager.setHandler( activity );
    }
    
//    @Override
    public void setAnimation ( TableAnimation animation ) {
        animation.setContainers( tableContainer, previewContainer );
    }

//    @Override
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
        editClickColumn.setColumnProvider( columnProvider );

        issueNumber = new ClickColumn< CaseObject >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.issueNumber() );
            }

            @Override
            public void fillColumnValue( Element element, CaseObject caseObject ) {
                element.setInnerText( caseObject == null ? "" : caseObject.getCaseNumber().toString() );
            }
        };
        issueNumber.setColumnProvider( columnProvider );

        product = new ClickColumn< CaseObject >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.issueProduct() );
            }

            @Override
            public void fillColumnValue( Element element, CaseObject caseObject ) {
                element.setInnerText( caseObject == null ? "" : "продукт" );
            }
        };
        product.setColumnProvider( columnProvider );

        contacts = new ClickColumn< CaseObject >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.issueContacts() );
            }

            @Override
            public void fillColumnValue( Element element, CaseObject caseObject ) {
                Person initiator = caseObject == null ? null : caseObject.getInitiator();
                element.setInnerText( initiator == null ? "" : initiator.getDisplayName() );
            }
        };
        contacts.setColumnProvider( columnProvider );

        info = new ClickColumn< CaseObject >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.issueInfo() );
            }

            @Override
            public void fillColumnValue( Element element, CaseObject caseObject ) {
                String info = caseObject == null ? "" : caseObject.getInfo();
                element.addClassName( "info" );
                element.setInnerText( info );
            }
        };
        info.setColumnProvider( columnProvider );

        creationDate = new ClickColumn< CaseObject >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.issueCreationDate() );
            }

            @Override
            public void fillColumnValue( Element element, CaseObject caseObject ) {
                Date created = caseObject == null ? null : caseObject.getCreated();
                element.setInnerText( created == null ? "" : dateFormatter.formatDateOnly( created ) );
            }
        };
        creationDate.setColumnProvider( columnProvider );

        manager = new ClickColumn< CaseObject >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.issueManager() );
            }

            @Override
            public void fillColumnValue( Element element, CaseObject caseObject ) {
                Person manager = caseObject == null ? null : caseObject.getManager();
                element.setInnerText( manager == null ? "" : manager.getDisplayName() );
            }
        };
        manager.setColumnProvider( columnProvider );

        table.addColumn( selectionColumn.header, selectionColumn.values );
        table.addColumn( editClickColumn.header, editClickColumn.values );
        table.addColumn( issueNumber.header, issueNumber.values );
        table.addColumn( product.header, product.values );
        table.addColumn( contacts.header, contacts.values );
        table.addColumn( info.header, info.values );
        table.addColumn( creationDate.header, creationDate.values );
        table.addColumn( manager.header, manager.values );
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
    TableWidget table;

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