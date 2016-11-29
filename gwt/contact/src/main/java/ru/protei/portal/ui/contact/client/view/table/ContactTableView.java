package ru.protei.portal.ui.contact.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.AbstractColumn;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.brainworm.factory.widget.table.client.helper.SelectionColumn;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.common.ContactColumnBuilder;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.separator.Separator;
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
        animation.setContainers( tableContainer, previewContainer, filterContainer );
    }

    @Override
    public HasWidgets getPreviewContainer () { return previewContainer; }

    @Override
    public HasWidgets getFilterContainer () { return filterContainer; }

    @Override
    public void hideElements() {
        filterContainer.setVisible( false );
        hideColumn.setVisibility( false );
        tableContainer.removeStyleName( "col-xs-9" );
        tableContainer.addStyleName( "col-xs-12" );
    }

    @Override
    public void showElements() {
        filterContainer.setVisible( true );
        hideColumn.setVisibility( true );
        tableContainer.removeStyleName( "col-xs-12" );
        tableContainer.addStyleName( "col-xs-9" );
    }

    @Override
    public void clearRecords() {
        table.clearCache();
        table.clearRows();
    }

    @Override
    public void setRecordCount( Long count ) {
        table.setTotalRecords( count.intValue() );
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
                element.setInnerText( person == null || person.getCompany() == null ? "" : person.getCompany().getCname() + "(" +person.getCompanyId()+")" );
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

        table.setSeparatorProvider( separator );
    }

    @UiField
    InfiniteTableWidget<Person> table;

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
    Separator separator;

    AbstractColumn hideColumn;
    ClickColumnProvider<Person> columnProvider = new ClickColumnProvider<>();
    SelectionColumn< Person > selectionColumn = new SelectionColumn<>();
    EditClickColumn<Person > editClickColumn;
    List<ClickColumn > columns = new ArrayList<>();

    AbstractContactTableActivity activity;

    private static ContactTableViewUiBinder ourUiBinder = GWT.create( ContactTableViewUiBinder.class );
    interface ContactTableViewUiBinder extends UiBinder< HTMLPanel, ContactTableView> {}
}