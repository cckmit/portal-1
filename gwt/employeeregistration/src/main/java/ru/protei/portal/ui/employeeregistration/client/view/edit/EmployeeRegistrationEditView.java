package ru.protei.portal.ui.employeeregistration.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.single.SinglePicker;
import ru.protei.portal.core.model.dict.En_EmployeeEquipment;
import ru.protei.portal.core.model.dict.En_EmploymentType;
import ru.protei.portal.core.model.dict.En_InternalResource;
import ru.protei.portal.core.model.dict.En_PhoneOfficeType;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.autoresizetextarea.AutoResizeTextArea;
import ru.protei.portal.ui.common.client.widget.imagepastetextarea.event.PasteEvent;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.employeeregistration.client.activity.edit.AbstractEmployeeRegistrationEditActivity;
import ru.protei.portal.ui.employeeregistration.client.activity.edit.AbstractEmployeeRegistrationEditView;
import ru.protei.portal.ui.employeeregistration.client.widget.optionlist.EmployeeEquipmentOptionList;
import ru.protei.portal.ui.employeeregistration.client.widget.optionlist.InternalResourceOptionList;
import ru.protei.portal.ui.employeeregistration.client.widget.optionlist.PhoneOfficeTypeOptionList;
import ru.protei.portal.ui.employeeregistration.client.widget.selector.EmploymentTypeSelector;

import java.util.Date;
import java.util.Set;

public class EmployeeRegistrationEditView extends Composite implements AbstractEmployeeRegistrationEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        resourcesList.setMandatoryOptions(En_InternalResource.EMAIL);
        probationPeriod.getElement().setAttribute("placeholder",  lang.employeeRegistrationProbationPeriodPlaceholder());
        setFixedValueChangeListener(position.getElement(), this);
        setFixedValueChangeListener(workplace.getElement(), this);
        setFixedValueChangeListener(operatingSystem.getElement(), this);
        setFixedValueChangeListener(additionalSoft.getElement(), this);
        setFixedValueChangeListener(resourceComment.getElement(), this);
    }
    
    @Override
    public void setActivity(AbstractEmployeeRegistrationEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<String> fullName() {
        return fullName;
    }

    @Override
    public HasValue<PersonShortView> headOfDepartment() {
        return headOfDepartment;
    }

    @Override
    public HasValue<Date> employmentDate() {
        return employmentDate;
    }

    @Override
    public HasValue<En_EmploymentType> employmentType() {
        return employmentType;
    }

    @Override
    public HasValue<Boolean> withRegistration() {
        return withRegistration;
    }

    @Override
    public HasValue<String> position() {
        return position;
    }

    @Override
    public HasValue<String> comment() {
        return comment;
    }

    @Override
    public HasValue<String> workplace() {
        return workplace;
    }

    @Override
    public HasValue<Set<En_EmployeeEquipment>> equipmentList() {
        return equipmentList;
    }

    @Override
    public HasValue<Set<En_InternalResource>> resourcesList() {
        return resourcesList;
    }

    @Override
    public HasValue<Set<En_PhoneOfficeType>> phoneOfficeTypeList() {
        return phoneTypeList;
    }

    @Override
    public HasValidable fullNameValidation() {
        return fullName;
    }

    @Override
    public HasValidable positionValidation() {
        return position;
    }

    @Override
    public HasValidable headOfDepartmentValidation() {
        return headOfDepartment;
    }

    @Override
    public void setEmploymentDateValid(boolean isValid) {
        employmentDate.markInputValid(isValid);
    }

    @Override
    public HasEnabled saveEnabled() {
        return saveButton;
    }

    @Override
    public HasValue<Integer> probationPeriod() {
        return probationPeriod;
    }
    @Override
    public HasValue<String> resourceComment() {
        return resourceComment;
    }
    @Override
    public HasValue<String> operatingSystem() {
        return operatingSystem;
    }
    @Override
    public HasValue<String> additionalSoft() {
        return additionalSoft;
    }

    @Override
    public HasValue<Set<PersonShortView>> curators() {
        return curators;
    }

    @Override
    public HasVisibility workplaceErrorLabelVisibility() {
        return workplaceErrorLabel;
    }

    @Override
    public void setWorkplaceErrorLabel(String errorMsg) {
        workplaceErrorLabel.setText(errorMsg);
    }

    @Override
    public HasVisibility positionErrorLabelVisibility() {
        return positionErrorLabel;
    }

    @Override
    public void setPositionErrorLabel(String errorMsg) {
        positionErrorLabel.setText(errorMsg);
    }

    @Override
    public HasVisibility additionalSoftErrorLabelVisibility() {
        return additionalSoftErrorLabel;
    }

    @Override
    public void setAdditionalSoftErrorLabel(String errorMsg) {
        additionalSoftErrorLabel.setText(errorMsg);
    }

    @Override
    public HasVisibility resourceCommentErrorLabelVisibility() {
        return resourceCommentErrorLabel;
    }

    @Override
    public void setResourceCommentErrorLabel(String errorMsg) {
        resourceCommentErrorLabel.setText(errorMsg);
    }

    @Override
    public HasVisibility operatingSystemErrorLabelVisibility() {
        return OSErrorLabel;
    }

    @Override
    public void setOperatingSystemErrorLabel(String errorMsg) {
        OSErrorLabel.setText(errorMsg);
    }

    @UiHandler("saveButton")
    public void onSaveClicked(ClickEvent event) {
        if (activity != null) {
            activity.onSaveClicked();
        }
    }

    @UiHandler("cancelButton")
    public void onCancelClicked(ClickEvent event) {
        if (activity != null) {
            activity.onCancelClicked();
        }
    }

    private void resetTimer() {
        limitedFieldsValidationTimer.cancel();
        limitedFieldsValidationTimer.schedule(200);
    }

    private native void setFixedValueChangeListener(Element element, EmployeeRegistrationEditView view) /*-{
        element.addEventListener("input", function (event) {
            view.@ru.protei.portal.ui.employeeregistration.client.view.edit.EmployeeRegistrationEditView::resetTimer()();
        });
    }-*/;

    @UiField
    Button saveButton;

    @UiField
    ValidableTextBox fullName;

    @Inject
    @UiField(provided = true)
    EmployeeButtonSelector headOfDepartment;

    @Inject
    @UiField(provided = true)
    SinglePicker employmentDate;

    @Inject
    @UiField(provided =  true)
    EmploymentTypeSelector employmentType;

    @UiField
    CheckBox withRegistration;

    @UiField
    ValidableTextBox position;

    @UiField
    Label positionErrorLabel;

    @UiField
    AutoResizeTextArea comment;

    @UiField
    AutoResizeTextArea workplace;

    @UiField
    Label workplaceErrorLabel;

    @Inject
    @UiField(provided = true)
    InternalResourceOptionList resourcesList;

    @Inject
    @UiField(provided = true)
    EmployeeEquipmentOptionList equipmentList;
    @UiField
    IntegerBox probationPeriod;
    @UiField
    AutoResizeTextArea resourceComment;
    @UiField
    Label resourceCommentErrorLabel;

    @Inject
    @UiField(provided = true)
    PhoneOfficeTypeOptionList phoneTypeList;
    @UiField
    ValidableTextBox operatingSystem;
    @UiField
    Label OSErrorLabel;
    @UiField
    AutoResizeTextArea additionalSoft;
    @UiField
    Label additionalSoftErrorLabel;

    @UiField
    Lang lang;

    @Inject
    @UiField(provided = true)
    EmployeeMultiSelector curators;

    private AbstractEmployeeRegistrationEditActivity activity;

    Timer limitedFieldsValidationTimer = new Timer() {
        @Override
        public void run() {
            if (activity != null) {
                activity.validateLimitedFields();
            }
        }
    };

    private static EmployeeRegistrationViewUiBinder ourUiBinder = GWT.create(EmployeeRegistrationViewUiBinder.class);
    interface EmployeeRegistrationViewUiBinder extends UiBinder<HTMLPanel, EmployeeRegistrationEditView> {
    }
}
