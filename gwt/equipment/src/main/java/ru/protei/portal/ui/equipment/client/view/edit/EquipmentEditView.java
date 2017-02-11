package ru.protei.portal.ui.equipment.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_EquipmentStage;
import ru.protei.portal.core.model.dict.En_EquipmentType;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.ui.equipment.client.activity.edit.AbstractEquipmentEditActivity;
import ru.protei.portal.ui.equipment.client.activity.edit.AbstractEquipmentEditView;
import ru.protei.portal.ui.equipment.client.widget.number.list.DecimalNumberList;
import ru.protei.portal.ui.equipment.client.widget.selector.EquipmentSelector;
import ru.protei.portal.ui.equipment.client.widget.stage.EquipmentStageSelector;
import ru.protei.portal.ui.equipment.client.widget.type.EquipmentTypeSelector;

import java.util.List;


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
    public HasValue< String > nameSldWrks() {
        return nameSldWrks;
    }

    @Override
    public HasValue< String > name() {
        return nameSpecification;
    }

    @Override
    public HasValue< String > comment() {
        return comment;
    }

    @Override
    public HasEnabled nameEnabled() {
        return nameSpecification;
    }

    @Override
    public HasEnabled typeEnabled() {
        return type;
    }

    @Override
    public HasValue<En_EquipmentType> type() {
        return type;
    }

    @Override
    public HasValue<En_EquipmentStage> stage() {
        return stage;
    }

    @Override
    public HasValue< Equipment > linkedEquipment() {
        return linkedEquipment;
    }

    @Override
    public HasValue< List<DecimalNumber> > numbers() {
        return numbers;
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
    TextBox comment;
    @Inject
    @UiField(provided = true)
    EquipmentTypeSelector type;
    @Inject
    @UiField(provided = true)
    EquipmentStageSelector stage;
    @Inject
    @UiField(provided = true)
    EquipmentSelector linkedEquipment;
    @Inject
    @UiField(provided = true)
    DecimalNumberList numbers;

    AbstractEquipmentEditActivity activity;

    private static ContactViewUiBinder ourUiBinder = GWT.create(ContactViewUiBinder.class);
    interface ContactViewUiBinder extends UiBinder<HTMLPanel, EquipmentEditView> {}
}
