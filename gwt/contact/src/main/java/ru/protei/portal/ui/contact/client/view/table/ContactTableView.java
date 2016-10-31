package ru.protei.portal.ui.contact.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.brainworm.factory.widget.table.client.helper.SelectionColumn;
import ru.brainworm.factory.widget.table.client.helper.StaticTextColumn;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.ui.contact.client.activity.table.AbstractContactTableActivity;
import ru.protei.portal.ui.contact.client.activity.table.AbstractContactTableView;
import ru.protei.portal.ui.contact.client.widget.company.buttonselector.CompanyButtonSelector;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.ModuleType;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;

import java.util.List;

/**
 * Created by turik on 28.10.16.
 */
public class ContactTableView extends Composite implements AbstractContactTableView, KeyUpHandler {
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        search.getElement().setPropertyString( "placeholder", lang.search() );
        initTable();
    }

    @Override
    public void setActivity( AbstractContactTableActivity activity ) {
        this.activity = activity;
    }

    @Override
    public HasValue<Company> company() {
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

    @Override
    public void addRecords(List< Person > result) {
        result.forEach( person -> {
            table.addRow( person );
        });
    }

    @Override
    public void clearRecords() {
        table.clearRows();
    }

    @Override
    public void onKeyUp( KeyUpEvent keyUpEvent ) {
        timer.cancel();
        timer.schedule( 300 );
    }

    @UiHandler( "company" )
    public void onCompanySelected( ValueChangeEvent< Company > event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler( "showFired" )
    public void onShowFireClicked( ClickEvent event ) {

        if (showFired.getValue())
            showFired.removeStyleName("active");
        else
            showFired.addStyleName("active");

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
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    private void initTable () {


        SelectionColumn< Person > selectionColumn = new SelectionColumn< Person >();

        StaticTextColumn< Person > displayName = new StaticTextColumn< Person >( lang.contactFullName() ) {
            @Override
            public String getColumnValue(Person person) {
                return person == null ? "" : person.getDisplayName();
            }
        };

        StaticTextColumn< Person > company = new StaticTextColumn< Person >( lang.company() ) {
            @Override
            public String getColumnValue(Person person) {
                return person == null || person.getCompany() == null ? "" : person.getCompany().getCname();
            }
        };

        StaticTextColumn< Person > position = new StaticTextColumn< Person >( lang.contactPosition() ) {
            @Override
            public String getColumnValue(Person person) {
                return person == null ? "" : person.getPosition();
            }
        };

        StaticTextColumn< Person > phone = new StaticTextColumn< Person >( lang.phone() ) {
            @Override
            public String getColumnValue(Person person) {
                return person == null ? "" : person.getMobilePhone();
            }
        };

        StaticTextColumn< Person > email = new StaticTextColumn< Person >( lang.email() ) {
            @Override
            public String getColumnValue(Person person) {
                return person == null ? "" : person.getEmail();
            }
        };

        table.addColumn( selectionColumn.header, selectionColumn.values );
        table.addColumn( displayName.header, displayName.values );
        table.addColumn( company.header, company.values );
        table.addColumn( position.header, position.values );
        table.addColumn( phone.header, phone.values );
        table.addColumn( email.header, email.values );
    }

    @Inject
    @UiField ( provided = true )
    CompanyButtonSelector company;

    @UiField
    CheckBox showFired;

    @Inject
    @UiField( provided = true )
    SortFieldSelector sortField;

    @UiField
    ToggleButton sortDir;

    @UiField
    TextBox search;

    @UiField
    TableWidget table;

    @Inject
    @UiField
    Lang lang;
    @UiField
    Button create;

    Timer timer = new Timer() {
        @Override
        public void run() {
            if ( activity != null ) {
                activity.onFilterChanged();
            }
        }
    };

    AbstractContactTableActivity activity;

    private static ContactTableViewUiBinder ourUiBinder = GWT.create( ContactTableViewUiBinder.class );
    interface ContactTableViewUiBinder extends UiBinder< HTMLPanel, ContactTableView> {}
}