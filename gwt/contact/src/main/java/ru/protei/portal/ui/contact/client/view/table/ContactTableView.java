package ru.protei.portal.ui.contact.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.AbstractColumn;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.brainworm.factory.widget.table.client.helper.SelectionColumn;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;
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
    public void onInit(EditClickColumn<Person> editClickColumn, RemoveClickColumn<Person> removeClickColumn) {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        this.editClickColumn = editClickColumn;
        this.removeClickColumn = removeClickColumn;
        initTable();
    }

    @Override
    public void setActivity( AbstractContactTableActivity activity ) {
        this.activity = activity;

        editClickColumn.setHandler( activity );
        editClickColumn.setEditHandler( activity );
        editClickColumn.setColumnProvider( columnProvider );

        removeClickColumn.setHandler( activity );
        removeClickColumn.setRemoveHandler( activity );
        removeClickColumn.setColumnProvider( columnProvider );
        removeClickColumn.setPrivilege( En_Privilege.CONTACT_REMOVE );

        columns.forEach( clickColumn -> {
            clickColumn.setHandler( activity );
            clickColumn.setColumnProvider( columnProvider );
        });
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
        filterContainer.setVisible( false );
        //hideColumn.setVisibility( false );
        tableContainer.removeStyleName( "col-xs-9" );
        tableContainer.addStyleName( "col-xs-12" );
    }

    @Override
    public void showElements() {
        filterContainer.setVisible( true );
        //hideColumn.setVisibility( true );
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

        editClickColumn.setPrivilege( En_Privilege.CONTACT_EDIT );

        ClickColumn< Person > displayName = new ClickColumn< Person >() {
            @Override
            protected void fillColumnHeader( Element element ) {
                element.setInnerText( lang.contactFullName() );
            }

            @Override
            public void fillColumnValue ( Element cell, Person value ) {
                Element root = DOM.createDiv();
                cell.appendChild( root );

                Element fioElement = DOM.createDiv();
                fioElement.setInnerHTML( "<b>" + value.getDisplayName() + "<b>" );
                root.appendChild( fioElement );

                PlainContactInfoFacade infoFacade = new PlainContactInfoFacade( value.getContactInfo() );
                root.appendChild( ContactColumnBuilder.make().add( "ion-android-call", infoFacade.getWorkPhone() )
                        .add( "ion-android-call", infoFacade.getMobilePhone() )
                        .add( "ion-android-phone-portrait", infoFacade.getHomePhone() )
                        .toElement() );

                root.appendChild( ContactColumnBuilder.make().add( "ion-android-mail", infoFacade.getEmail() )
                        .add( "ion-android-mail", infoFacade.getEmail_own() )
                        .toElement() );
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
            public void fillColumnValue ( Element cell, Person value ) {
                Element root = DOM.createDiv();
                cell.appendChild( root );

                Element fioElement = DOM.createDiv();
                fioElement.setInnerHTML( "<b>" + value.getCompany().getCname() + "<b>" );
                root.appendChild( fioElement );

                Element posElement = DOM.createDiv();
                posElement.addClassName( "contact-position" );
                posElement.setInnerHTML( value.getPosition() );
                root.appendChild( posElement );
            }
        };
        columns.add( company );

        //hideColumn = table.addColumn( selectionColumn.header, selectionColumn.values );
        table.addColumn( company.header, company.values );
        table.addColumn( displayName.header, displayName.values );
        table.addColumn( editClickColumn.header, editClickColumn.values );
        table.addColumn( removeClickColumn.header, removeClickColumn.values );
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
    RemoveClickColumn<Person> removeClickColumn;
    List<ClickColumn > columns = new ArrayList<>();


    AbstractContactTableActivity activity;

    private static ContactTableViewUiBinder ourUiBinder = GWT.create( ContactTableViewUiBinder.class );
    interface ContactTableViewUiBinder extends UiBinder< HTMLPanel, ContactTableView> {}
}