package ru.protei.portal.ui.contact.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.contact.client.activity.table.AbstractContactTableActivity;
import ru.protei.portal.ui.contact.client.activity.table.AbstractContactTableView;
import ru.protei.portal.ui.contact.client.widget.company.buttonselector.CompanyButtonSelector;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.ModuleType;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;

/**
 * Created by turik on 28.10.16.
 */
public class ContactTableView extends Composite implements AbstractContactTableView {
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        sortField.fillOptions( ModuleType.CLIENT );
        search.getElement().setPropertyString( "placeholder", lang.search() );
    }

    @Override
    public void setActivity( AbstractContactTableActivity activity ) {
        this.activity = activity;
    }

    @Inject
    @UiField
    CompanyButtonSelector company;

    @Inject
    @UiField
    SortFieldSelector sortField;

    @UiField
    ToggleButton dirButton;

    @UiField
    CheckBox showFired;

    @UiField
    TextBox search;

    @Inject
    @UiField
    Lang lang;

    AbstractContactTableActivity activity;

    private static ClientTableViewUiBinder ourUiBinder = GWT.create( ClientTableViewUiBinder.class );
    interface ClientTableViewUiBinder extends UiBinder< HTMLPanel, ContactTableView> {}
}