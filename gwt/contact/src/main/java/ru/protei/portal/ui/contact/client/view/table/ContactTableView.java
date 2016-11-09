package ru.protei.portal.ui.contact.client.view.table;

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
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanySelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.contact.client.activity.table.AbstractContactTableActivity;
import ru.protei.portal.ui.contact.client.activity.table.AbstractContactTableView;
import ru.protei.portal.ui.contact.client.view.table.columns.ContactColumnBuilder;
import ru.protei.portal.ui.contact.client.view.table.columns.EditClickColumn;

import java.util.ArrayList;
import java.util.List;

/**
 * Представление таблицы контактов
 */
public class ContactTableView extends Composite implements AbstractContactTableView {
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        search.getElement().setPropertyString( "placeholder", lang.search() );
    }

    @Override
    public void setActivity( AbstractContactTableActivity activity ) {
        this.activity = activity;
        initTable();
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
    public HasValue<Boolean> showFired() {
        return showFired;
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
        showFired.setValue( false );
        sortField.setValue( En_SortField.person_full_name );
        sortDir.setValue( true );
        search.setText( "" );
    }

    public void addRecord( Person person ) {
        table.addRow( person );
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

    @UiHandler( "showFired" )
    public void onShowFireClicked( ClickEvent event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
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

        table.addColumn( selectionColumn.header, selectionColumn.values );

        EditClickColumn<Person > editClickColumn = new EditClickColumn<Person>( lang ) {
        };
        table.addColumn( editClickColumn.header, editClickColumn.values );
        editClickColumn.setHandler( activity );
        editClickColumn.setEditHandler( activity );
        editClickColumn.setColumnProvider( columnProvider );

        ClickColumn< Person > displayName = new ClickColumn< Person >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.contactFullName() );
            }

            @Override
            public void fillColumnValue ( Element element, Person person ) {
                element.setInnerText( person == null ? "" : person.getDisplayName() );
            }
        };
        table.addColumn( displayName.header, displayName.values );
        displayName.setHandler( activity );
        displayName.setColumnProvider( columnProvider );

        ClickColumn< Person > company = new ClickColumn< Person >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.company() );
            }

            @Override
            public void fillColumnValue ( Element element, Person person ) {
                element.setInnerText( person == null || person.getCompany() == null ? "" : person.getCompany().getCname() );
            }
        };
        table.addColumn( company.header, company.values );
        company.setHandler( activity );
        company.setColumnProvider( columnProvider );

        ClickColumn< Person > position = new ClickColumn< Person >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.contactPosition() );
            }

            @Override
            public void fillColumnValue ( Element element, Person person ) {
                element.setInnerText( person == null ? "" : person.getPosition() );

            }
        };
        table.addColumn( position.header, position.values );
        position.setHandler( activity );
        position.setColumnProvider( columnProvider );

        ClickColumn< Person > phone = new ClickColumn< Person >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.phone() );
            }

            @Override
            public void fillColumnValue( Element element, Person person ) {
                PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(person.getContactInfo());
                element.appendChild( ContactColumnBuilder.make().add( null, infoFacade.getWorkPhone() )
                        .add(null, infoFacade.getMobilePhone() )
                        .add( null, infoFacade.getHomePhone()).toElement() );
            }
        };
        table.addColumn( phone.header, phone.values );
        phone.setHandler( activity );
        phone.setColumnProvider( columnProvider );

        ClickColumn< Person > email = new ClickColumn< Person >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.email() );
            }

            @Override
            public void fillColumnValue( Element element, Person person ) {
                PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(person.getContactInfo() );
                element.appendChild( ContactColumnBuilder.make().add( null, infoFacade.getEmail() )
                        .add( null, infoFacade.getEmail_own() ).toElement() );
            }
        };
        table.addColumn( email.header, email.values );
        email.setHandler( activity );
        email.setColumnProvider( columnProvider );
    }

    @Inject
    @UiField ( provided = true )
    CompanySelector company;

    @UiField
    CheckBox showFired;

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

    Timer timer = new Timer() {
        @Override
        public void run() {
            if ( activity != null ) {
                activity.onFilterChanged();
            }
        }
    };

    SelectionColumn< Person > selectionColumn = new SelectionColumn<>();
    ClickColumnProvider<Person> columnProvider = new ClickColumnProvider< Person >();
    List<ClickColumn > columns = new ArrayList< ClickColumn >();

    AbstractContactTableActivity activity;

    private static ContactTableViewUiBinder ourUiBinder = GWT.create( ContactTableViewUiBinder.class );
    interface ContactTableViewUiBinder extends UiBinder< HTMLPanel, ContactTableView> {}
}