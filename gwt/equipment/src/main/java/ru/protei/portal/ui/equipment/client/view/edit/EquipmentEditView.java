package ru.protei.portal.ui.equipment.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.equipment.client.activity.edit.AbstractEquipmentEditActivity;
import ru.protei.portal.ui.equipment.client.activity.edit.AbstractEquipmentEditView;
import ru.protei.portal.ui.equipment.client.widget.number.DecimalNumberBox;


/**
 * Карточка редактирования единицы оборудования
 */
public class EquipmentEditView extends Composite implements AbstractEquipmentEditView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setActivity(AbstractEquipmentEditActivity activity) {
        this.activity = activity;
    }

    @UiHandler( "saveButton" )
    public void onSaveClicked( ClickEvent event ) {
        if ( activity != null ) {
            activity.onSaveClicked();
        }
    }

    @UiHandler( "cancelButton" )
    public void onCancelClicked( ClickEvent event ) {
        if ( activity != null ) {
            activity.onCancelClicked();
        }
    }

    @UiField
    Button saveButton;

    @UiField
    Button cancelButton;
    @UiField
    ValidableTextBox nameSldWrks;
    @UiField
    ValidableTextBox nameSpecification;
    @UiField
    DecimalNumberBox pdraNum;
    @UiField
    DecimalNumberBox pamrNum;

    AbstractEquipmentEditActivity activity;


    private static ContactViewUiBinder ourUiBinder = GWT.create(ContactViewUiBinder.class);
    interface ContactViewUiBinder extends UiBinder<HTMLPanel, EquipmentEditView> {}

}
