package ru.protei.portal.ui.equipment.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.common.shared.model.DecimalNumber;
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

    @Override
    public HasValue< String > nameBySldWrks() {
        return nameSldWrks;
    }

    @Override
    public HasValue< String > nameBySpecification() {
        return nameSpecification;
    }

    @Override
    public HasValue< String > comment() {
        return comment;
    }

    @Override
    public HasEnabled nameBySpecificationEnabled() {
        return nameSpecification;
    }

    @Override
    public HasValue< DecimalNumber > pdraNumber() {
        return pdraNum;
    }

    @Override
    public HasValue< DecimalNumber > pamrNumber() {
        return pamrNum;
    }

    @Override
    public HasEnabled pamrNumberEnabled() {
        return pamrNum;
    }

    @Override
    public HasEnabled pdraNumberEnabled() {
        return pdraNum;
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
    @Inject
    @UiField(provided = true)
    DecimalNumberBox pdraNum;
    @Inject
    @UiField(provided = true)
    DecimalNumberBox pamrNum;
    @UiField
    TextBox comment;

    AbstractEquipmentEditActivity activity;


    private static ContactViewUiBinder ourUiBinder = GWT.create(ContactViewUiBinder.class);
    interface ContactViewUiBinder extends UiBinder<HTMLPanel, EquipmentEditView> {}

}
