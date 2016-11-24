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
import ru.brainworm.factory.widget.table.client.AbstractColumn;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.brainworm.factory.widget.table.client.helper.SelectionColumn;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.common.ContactColumnBuilder;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanySelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.contact.client.activity.table.AbstractContactTableActivity;
import ru.protei.portal.ui.contact.client.activity.table.AbstractContactTableView;

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
        initTable();
    }

    @Override
    public void setActivity( AbstractContactTableActivity activity ) {
        this.activity = activity;
        editClickColumn.setHandler( activity );
        editClickColumn.setEditHandler( activity );
        editClickColumn.setColumnProvider( columnProvider );
        columns.forEach( clickColumn -> {
            clickColumn.setHandler( activity );
            clickColumn.setColumnProvider( columnProvider );
        });
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
    public void hideElements() {
        filter.setVisible( false );
        hideColumn.setVisibility( false );
    }

    @Override
    public void showElements() {
        filter.setVisible( true );
        hideColumn.setVisibility( true );
    }

    @Override
    public void clearRecords() {
        table.clearRows();
    }

    @Override
    public void setRecordCount( Long count ) {
        table.setTotalRecords( count.intValue() );
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

        editClickColumn = new EditClickColumn<Person>( lang ) {};

        ClickColumn< Person > displayName = new ClickColumn< Person >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.contactFullName() );
                element.addClassName( "person" );
            }

            @Override
            public void fillColumnValue ( Element element, Person person ) {
                element.setInnerText( person == null ? "" : person.getDisplayName() );
            }
        };
        columns.add( displayName );

        ClickColumn< Person > company = new ClickColumn< Person >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.company() );
                element.addClassName( "company" );
            }

            @Override
            public void fillColumnValue ( Element element, Person person ) {
                element.setInnerText( person == null || person.getCompany() == null ? "" : person.getCompany().getCname() );
            }
        };
        columns.add( company );

        ClickColumn< Person > position = new ClickColumn< Person >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.contactPosition() );
                element.addClassName( "position" );
            }

            @Override
            public void fillColumnValue ( Element element, Person person ) {
                element.setInnerText( person == null ? "" : person.getPosition() );

            }
        };
        columns.add( position );

        ClickColumn< Person > phone = new ClickColumn< Person >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.phone() );
                element.addClassName( "phone" );
            }

            @Override
            public void fillColumnValue( Element element, Person person ) {
                PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(person.getContactInfo());
                element.appendChild( ContactColumnBuilder.make().add( null, infoFacade.getWorkPhone() )
                        .add(null, infoFacade.getMobilePhone() )
                        .add( null, infoFacade.getHomePhone()).toElement() );
            }
        };
        columns.add( phone );

        ClickColumn< Person > email = new ClickColumn< Person >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.email() );
                element.addClassName( "email" );
            }

            @Override
            public void fillColumnValue( Element element, Person person ) {
                PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(person.getContactInfo() );
                element.appendChild( ContactColumnBuilder.make().add( null, infoFacade.getEmail() )
                        .add( null, infoFacade.getEmail_own() ).toElement() );
            }
        };
        columns.add( email );

        hideColumn = table.addColumn( selectionColumn.header, selectionColumn.values );
        table.addColumn( editClickColumn.header, editClickColumn.values );
        table.addColumn( displayName.header, displayName.values );
        table.addColumn( company.header, company.values );
        table.addColumn( position.header, position.values );
        table.addColumn( phone.header, phone.values );
        table.addColumn( email.header, email.values );

        table.setSeparatorProvider( new InfiniteTableWidget.SeparatorProvider() {
            @Override
            public void fillSeparatorValue( Element element, int i ) {
                element.setInnerText( "Страница "+ (i+1) );
                element.addClassName( "separator" );
            }
        } );
        table.addStyleName( "contacts" );
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
    InfiniteTableWidget<Person> table;

    @UiField
    HTMLPanel tableContainer;

    @UiField
    HTMLPanel previewContainer;

    @UiField
    HTMLPanel filter;

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

    AbstractColumn hideColumn;
    ClickColumnProvider<Person> columnProvider = new ClickColumnProvider<>();
    SelectionColumn< Person > selectionColumn = new SelectionColumn<>();
    EditClickColumn<Person > editClickColumn;
    List<ClickColumn > columns = new ArrayList<>();

    AbstractContactTableActivity activity;

    private static ContactTableViewUiBinder ourUiBinder = GWT.create( ContactTableViewUiBinder.class );
    interface ContactTableViewUiBinder extends UiBinder< HTMLPanel, ContactTableView> {}
}