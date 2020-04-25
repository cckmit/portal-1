package ru.protei.portal.ui.contact.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.util.AvatarUtils;
import ru.protei.portal.ui.contact.client.activity.table.AbstractContactTableActivity;
import ru.protei.portal.ui.contact.client.activity.table.AbstractContactTableView;

import java.util.ArrayList;
import java.util.List;

/**
 * Представление таблицы контактов
 */
public class ContactTableView extends ContactTableViewBase implements AbstractContactTableView {

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

        columns.forEach( clickColumn -> {
            clickColumn.setHandler( activity );
            clickColumn.setColumnProvider( columnProvider );
        });
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
    public HasWidgets getPagerContainer() {
        return pagerContainer;
    }

    @Override
    public void hideElements() {
        filterContainer.setVisible( false );
        tableContainer.removeStyleName( "col-md-9" );
        tableContainer.addStyleName( "col-md-12" );
    }

    @Override
    public void showElements() {
        filterContainer.setVisible( true );
        tableContainer.removeStyleName( "col-md-12" );
        tableContainer.addStyleName( "col-md-9" );
    }

    @Override
    public void addRecords( List< Person > persons ) {
        persons.forEach( person -> table.addRow( person ) );
    }

    @Override
    public void clearRecords() {
        table.clearRows();
    }

    @Override
    public void clearSelection() {
        columnProvider.setSelectedValue(null);
    }

    private void initTable () {
        editClickColumn.setEnabledPredicate(person -> policyService.hasPrivilegeFor(En_Privilege.CONTACT_EDIT) && !person.isFired() );
        removeClickColumn.setEnabledPredicate(person -> policyService.hasPrivilegeFor(En_Privilege.CONTACT_REMOVE) && !person.isDeleted() );

        ClickColumn gender = new DynamicColumn<Person>(null, "column-img", value -> "<img src='" + AvatarUtils.getAvatarUrlByGender(value.getGender()) + "'></img>");
        columns.add(gender);

        ClickColumn<Person> displayName = getDisplayNameColumn( lang );
        columns.add( displayName );

        ClickColumn<Person> company = getCompanyColumn( lang );
        columns.add( company );

        ClickColumn<Person> contact = getContactColumn( lang );
        columns.add(contact);

        table.addColumn( gender.header, gender.values );
        table.addColumn( displayName.header, displayName.values );
        table.addColumn( company.header, company.values );
        table.addColumn( contact.header, contact.values );
        table.addColumn( editClickColumn.header, editClickColumn.values );
        table.addColumn( removeClickColumn.header, removeClickColumn.values );
    }

    @UiField
    TableWidget<Person> table;

    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    HTMLPanel filterContainer;
    @UiField
    HTMLPanel pagerContainer;

    @Inject
    @UiField
    Lang lang;

    @Inject
    PolicyService policyService;

    private ClickColumnProvider<Person> columnProvider = new ClickColumnProvider<>();
    private EditClickColumn<Person > editClickColumn;
    private RemoveClickColumn<Person> removeClickColumn;
    private List<ClickColumn > columns = new ArrayList<>();

    private AbstractContactTableActivity activity;

    private static ContactTableViewUiBinder ourUiBinder = GWT.create( ContactTableViewUiBinder.class );
    interface ContactTableViewUiBinder extends UiBinder< HTMLPanel, ContactTableView> {}
}