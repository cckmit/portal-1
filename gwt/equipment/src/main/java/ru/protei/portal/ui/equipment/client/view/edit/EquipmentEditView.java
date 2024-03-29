package ru.protei.portal.ui.equipment.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_EquipmentType;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.EquipmentShortView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.model.marker.HasProcessable;
import ru.protei.portal.ui.common.client.widget.button.ButtonProcessable;
import ru.protei.portal.ui.common.client.widget.decimalnumber.multiple.MultipleDecimalNumberInput;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.selector.equipment.EquipmentButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.project.ProjectButtonSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.equipment.client.activity.edit.AbstractEquipmentEditActivity;
import ru.protei.portal.ui.equipment.client.activity.edit.AbstractEquipmentEditView;
import ru.protei.portal.ui.equipment.client.widget.type.EquipmentTypeBtnGroup;

import java.util.List;
import java.util.function.Function;


/**
 * Карточка редактирования единицы оборудования
 */
public class EquipmentEditView extends Composite implements AbstractEquipmentEditView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        nameErrorLabel().setText(lang.promptFieldLengthExceed(lang.equipmentNameBySpecification(), CrmConstants.EquipmentConstants.NAME_SIZE));
        manager.setItemRenderer( value -> value == null ? lang.equipmentManagerNotDefined() : value.getDisplayShortName() );
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
    public HasValue< EquipmentShortView > linkedEquipment() {
        return linkedEquipment;
    }

    @Override
    public void setNumbers(List<DecimalNumber> decimalNumbers, boolean isEnabled) {
        if (isEnabled) {
            numbers.setValue(decimalNumbers);
        } else {
            numbers.setNotEditableValue(decimalNumbers);
        }
    }

    @Override
    public List<DecimalNumber> getNumbers() {
        return numbers.getValue();
    }

    @Override
    public HasEnabled createDocumentButtonEnabled() {
        return createDocumentButton;
    }

    @Override
    public HasVisibility documentsVisibility() {
        return documents;
    }

    @Override
    public boolean isDecimalNumbersCorrect() {
        return numbers.checkIfCorrect();
    }

    @Override
    public HasValue< PersonShortView > manager() {
        return manager;
    }

    @Override
    public HasValue<EntityOption> project() {
        return project;
    }

    @Override
    public HasValue<String> date() {
        return dateTextBox;
    }

    @Override
    public HasWidgets documents() {
        return documents;
    }

    @Override
    public void setLinkedEquipmentFilter(Selector.SelectorFilter<EquipmentShortView> filter) {
        linkedEquipment.setFilter(filter);
    }

    @Override
    public void setVisibilitySettingsForCreated(boolean isVisible) {
        if (!isVisible) {
            projectBox.removeStyleName("col-md-4");
            projectBox.addStyleName("col-md-8");
        }
        else {
            projectBox.removeStyleName("col-md-8");
            projectBox.addStyleName("col-md-4");
        }
        date.setVisible(isVisible);
        dateTextBox.setEnabled(false);
    }

    @Override
    public HasVisibility nameErrorLabelVisibility() {
        return nameSpecificationErrorLabel;
    }

    @Override
    public HasText nameErrorLabel() {
        return nameSpecificationErrorLabel;
    }

    @Override
    public void setNameSizeValidationFunction(Function<String, Boolean> validationFunction) {
        nameSpecification.setValidationFunction(validationFunction);
    }

    @Override
    public HasProcessable saveProcessable() {
        return saveButton;
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

    @UiHandler( "createDocumentButton" )
    public void onCreateDocumentClicked( ClickEvent event ) {
        if ( activity != null ) {
            activity.onCreateDocumentClicked();
        }
    }

    @UiHandler("project")
    public void onProjectChanged(ValueChangeEvent<EntityOption> event) {
        activity.onProjectChanged();
    }

    @Inject
    @UiField
    Lang lang;

    @UiField
    ButtonProcessable saveButton;

    @UiField
    Button cancelButton;
    @UiField
    ValidableTextBox nameSldWrks;
    @UiField
    ValidableTextBox nameSpecification;
    @UiField
    Label nameSpecificationErrorLabel;
    @UiField
    TextArea comment;
    @Inject
    @UiField(provided = true)
    EquipmentTypeBtnGroup type;
    @Inject
    @UiField(provided = true)
    EquipmentButtonSelector linkedEquipment;
    @Inject
    @UiField(provided = true)
    MultipleDecimalNumberInput numbers;
    @Inject
    @UiField(provided = true)
    EmployeeButtonSelector manager;
    @Inject
    @UiField(provided = true)
    ProjectButtonSelector project;
    @UiField
    HTMLPanel date;
    @UiField
    TextBox dateTextBox;
    @UiField
    HTMLPanel projectBox;
    @UiField
    HTMLPanel managerBox;
    @UiField
    Button createDocumentButton;
    @UiField
    HTMLPanel documents;

    AbstractEquipmentEditActivity activity;

    private static ContactViewUiBinder ourUiBinder = GWT.create(ContactViewUiBinder.class);
    interface ContactViewUiBinder extends UiBinder<HTMLPanel, EquipmentEditView> {}
}
