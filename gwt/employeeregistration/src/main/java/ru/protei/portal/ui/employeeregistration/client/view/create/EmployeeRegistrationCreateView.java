package ru.protei.portal.ui.employeeregistration.client.view.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
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
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.pageable.SelectorModel;
import ru.protei.portal.ui.common.client.widget.autoresizetextarea.AutoResizeTextArea;
import ru.protei.portal.ui.common.client.widget.homecompany.HomeCompanyButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.employeedepartment.EmployeeDepartmentButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.employeeregistration.client.activity.create.AbstractEmployeeRegistrationCreateActivity;
import ru.protei.portal.ui.employeeregistration.client.activity.create.AbstractEmployeeRegistrationCreateView;
import ru.protei.portal.ui.employeeregistration.client.widget.optionlist.EmployeeEquipmentOptionList;
import ru.protei.portal.ui.employeeregistration.client.widget.optionlist.InternalResourceOptionList;
import ru.protei.portal.ui.employeeregistration.client.widget.optionlist.PhoneOfficeTypeOptionList;
import ru.protei.portal.ui.employeeregistration.client.widget.selector.EmploymentTypeSelector;

import java.util.Date;
import java.util.Set;

public class EmployeeRegistrationCreateView extends Composite implements AbstractEmployeeRegistrationCreateView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        resourcesList.setMandatoryOptions(En_InternalResource.EMAIL);
        probationPeriod.getElement().setAttribute("placeholder",  lang.employeeRegistrationProbationPeriodPlaceholder());
        position.addInputHandler(event -> resetTimer());
        workplace.addInputHandler(event -> resetTimer());
        operatingSystem.addInputHandler(event -> resetTimer());
        additionalSoft.addInputHandler(event -> resetTimer());
        resourceComment.addInputHandler(event -> resetTimer());
    }
    
    @Override
    public void setActivity(AbstractEmployeeRegistrationCreateActivity activity) {
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
    public void setEquipmentOptionEnabled(En_EmployeeEquipment equipment, boolean isEnabled) {
        equipmentList.setOptionEnabled(equipment, isEnabled);
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
    public void setPhoneOptionEnabled(En_PhoneOfficeType phone, boolean isEnabled) {
        phoneTypeList.setOptionEnabled(phone, isEnabled);
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
    public HasValue<Boolean> IDE() {
        return IDE;
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

    @Override
    public HasValue<EntityOption> company() {
        return company;
    }

    @Override
    public HasValue<EntityOption> department() {
        return department;
    }

    @Override
    public HasEnabled departmentEnabled() {
        return department;
    }

    @Override
    public void setDepartmentModel(SelectorModel<EntityOption> model) {
        department.setModel(model);
    }

    @UiHandler("company")
    public void onCompanySelected(ValueChangeEvent<EntityOption> event) {
        if (activity != null) {
            activity.onCompanySelected();
        }
    }

    @UiHandler("headOfDepartment")
    public void onHeadOfDepartment(ValueChangeEvent<PersonShortView> event) {
        if (activity != null) {
            activity.onHeadOfDepartmentChanged();
        }
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

    @UiHandler("IDE")
    public void onIDEClicked(ClickEvent event) {
        if (activity != null) {
            activity.onIDEClicked();
        }
    }

    private void resetTimer() {
        limitedFieldsValidationTimer.cancel();
        limitedFieldsValidationTimer.schedule(200);
    }

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
    HomeCompanyButtonSelector company;

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
    CheckBox IDE;

    @UiField
    Lang lang;

    @Inject
    @UiField(provided = true)
    EmployeeMultiSelector curators;
    @UiField
    EmployeeDepartmentButtonSelector department;

    private AbstractEmployeeRegistrationCreateActivity activity;

    private Timer limitedFieldsValidationTimer = new Timer() {
        @Override
        public void run() {
            if (activity != null) {
                activity.validateLimitedFields();
            }
        }
    };

    private static EmployeeRegistrationViewUiBinder ourUiBinder = GWT.create(EmployeeRegistrationViewUiBinder.class);
    interface EmployeeRegistrationViewUiBinder extends UiBinder<HTMLPanel, EmployeeRegistrationCreateView> {
    }
}
