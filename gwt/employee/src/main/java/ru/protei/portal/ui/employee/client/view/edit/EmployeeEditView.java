package ru.protei.portal.ui.employee.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.single.SinglePicker;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.events.InputEvent;
import ru.protei.portal.ui.common.client.widget.homecompany.HomeCompanyButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.companydepartment.CompanyDepartmentSelector;
import ru.protei.portal.ui.common.client.widget.selector.dict.GenderButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.workerposition.WorkerPositionSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.employee.client.activity.edit.AbstractEmployeeEditActivity;
import ru.protei.portal.ui.employee.client.activity.edit.AbstractEmployeeEditView;

import java.util.Date;

public class EmployeeEditView extends Composite implements AbstractEmployeeEditView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        workEmail.setRegexp( CrmConstants.Masks.EMAIL );
        workEmail.setMaxLength( CrmConstants.EMAIL_MAX_SIZE );
        ipAddress.setRegexp( CrmConstants.Masks.IP);

        gender.setValid(false);

        companyDepartmentSelector.setEditHandler(editEvent -> activity.onEditCompanyDepartmentClicked(editEvent.id, editEvent.text));
        companyDepartmentSelector.setAddHandler(addEvent -> activity.onAddCompanyDepartmentClicked());

        workerPositionSelector.setEditHandler(editEvent -> activity.onEditWorkerPositionClicked(editEvent.id, editEvent.text));
        workerPositionSelector.setAddHandler(addEvent -> activity.onAddWorkerPositionClicked());

        birthDay.setMandatory(false);
        changeAccountVisibility().setVisible(false);

    }

    @Override
    public void setActivity(AbstractEmployeeEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasEnabled firstNameEnabled() {
        return firstName;
    }

    @Override
    public HasEnabled lastNameEnabled() {
        return lastName;
    }

    @Override
    public HasEnabled secondNameEnabled() {
        return secondName;
    }

    @Override
    public HasEnabled birthDayEnabled() {
        return birthDay;
    }

    @Override
    public HasEnabled genderEnabled() {
        return gender;
    }

    @Override
    public HasEnabled workEmailEnabled() {
        return workEmail;
    }

    @Override
    public HasEnabled mobilePhoneEnabled() {
        return mobilePhone;
    }

    @Override
    public HasEnabled workPhoneEnabled() {
        return workPhone;
    }

    @Override
    public HasEnabled ipAddressEnabled() {
        return ipAddress;
    }

    @Override
    public HasValue<String> firstName() {
        return firstName;
    }

    @Override
    public HasValue<String> lastName() {
        return lastName;
    }

    @Override
    public HasText secondName() {
        return secondName;
    }

    @Override
    public HasValue<Date> birthDay() {
        return birthDay;
    }

    @Override
    public HasText workPhone() {
        return workPhone;
    }

    @Override
    public HasText mobilePhone() {
        return mobilePhone;
    }

    @Override
    public HasValue<String> workEmail() {
        return workEmail;
    }

    @Override
    public HasValue<String> ipAddress() {
        return ipAddress;
    }

    @Override
    public HasValue<EntityOption> workerPosition() {
        return workerPositionSelector;
    }

    @Override
    public HasValue<EntityOption> companyDepartment() {
        return companyDepartmentSelector;
    }

    @Override
    public HasEnabled companyDepartmentEnabled() {
        return companyDepartmentSelector;
    }

    @Override
    public HasEnabled workerPositionEnabled() {
        return workerPositionSelector;
    }

    @Override
    public HasValue<EntityOption> company() {
        return company;
    }

    @Override
    public HasValue<En_Gender> gender() {
        return gender;
    }

    @Override
    public HasValidable companyValidator() {
        return company;
    }

    @Override
    public HasValidable firstNameValidator(){
        return firstName;
    }

    @Override
    public HasValidable lastNameValidator(){
        return lastName;
    }

    @Override
    public HasValidable workEmailValidator(){
        return workEmail;
    }

    @Override
    public HasValidable ipAddressValidator(){
        return ipAddress;
    }

    @Override
    public void companyDepartmentSelectorReload(){
        companyDepartmentSelector.reload();
    }

    @Override
    public void workerPositionSelectorReload(){
        workerPositionSelector.reload();
    }

    @Override
    public HasValidable companyDepartmentValidator(){
        return companyDepartmentSelector;
    }

    @Override
    public HasValidable workerPositionValidator(){
        return workerPositionSelector;
    }

    @Override
    public HasValidable genderValidator(){
        return gender;
    }

    @Override
    public HasVisibility saveVisibility() {
        return saveButton;
    }

    @Override
    public HasEnabled companyEnabled () {
        return company;
    }

    @Override
    public HasVisibility firedMsgVisibility() {
        return employeeFired;
    }

    @Override
    public HasVisibility fireBtnVisibility() {
        return fireBtn;
    }

    @Override
    public HasVisibility firstNameErrorLabelVisibility() {
        return firstNameErrorLabel;
    }

    @Override
    public HasVisibility secondNameErrorLabelVisibility() {
        return secondNameErrorLabel;
    }

    @Override
    public HasVisibility lastNameErrorLabelVisibility() {
        return lastNameErrorLabel;
    }

    @Override
    public HasText firstNameErrorLabel() {
        return firstNameErrorLabel;
    }

    @Override
    public HasText secondNameErrorLabel() {
        return secondNameErrorLabel;
    }

    @Override
    public HasText lastNameErrorLabel() {
        return lastNameErrorLabel;
    }

    @Override
    public HasEnabled saveEnabled() {
        return saveButton;
    }

    @Override
    public String firstNameLabel() {
        return firstNameLabel.getInnerText();
    }

    @Override
    public String secondNameLabel() {
        return secondNameLabel.getInnerText();
    }

    @Override
    public String lastNameLabel() {
        return lastNameLabel.getInnerText();
    }

    @Override
    public String ipAddressLabel() {
        return ipAddressLabel.getInnerText();
    }

    @Override
    public String workEmailLabel() {
        return workEmailLabel.getInnerText();
    }

    @Override
    public HasValue<Boolean> changeAccount() { return changeAccount; }

    @Override
    public HasVisibility changeAccountVisibility() {
        return changeAccount;
    }

    @Override
    public void updateCompanyDepartments(Long companyId) {
        companyDepartmentSelector.updateCompanyDepartments(companyId);
    }

    @Override
    public void setAddButtonCompanyDepartmentVisible(boolean isVisible) {
        companyDepartmentSelector.setAddButtonVisible(isVisible);
    }

    @Override
    public void updateWorkerPositions(Long companyId) {
        workerPositionSelector.updateWorkerPositions(companyId);
    }

    @Override
    public void setAddButtonWorkerPositionVisible(boolean isVisible) {
        workerPositionSelector.setAddButtonVisible(isVisible);
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

    @UiHandler( "fireBtn" )
    public void onFireClicked( ClickEvent event ) {
        if (activity != null) {
            activity.onFireClicked();
        }
    }

    @UiHandler({"firstName", "secondName", "lastName"})
    public void onLimitedFieldsChanged(InputEvent event) {
        resetValidateTimer();
    }

    @UiHandler("company")
    public void onCompanySelected(ValueChangeEvent<EntityOption> event) {
        if (activity != null) {
            activity.onCompanySelected();
        }
    }

    @UiHandler("gender")
    public void onGenderSelected(ValueChangeEvent<En_Gender> event) {
        if (activity != null) {
            activity.onGenderSelected();
        }
    }

    private void resetValidateTimer() {
        limitedFieldsValidationTimer.cancel();
        limitedFieldsValidationTimer.schedule(200);
    }

    @UiField
    Button saveButton;

    @UiField
    Button cancelButton;

    @UiField
    Button fireBtn;

    @UiField
    ValidableTextBox firstName;

    @UiField
    ValidableTextBox lastName;

    @UiField
    ValidableTextBox secondName;

    @UiField
    Label firstNameErrorLabel;

    @UiField
    Label lastNameErrorLabel;

    @UiField
    Label secondNameErrorLabel;

    @UiField
    LabelElement firstNameLabel;

    @UiField
    LabelElement lastNameLabel;

    @UiField
    LabelElement secondNameLabel;

    @com.google.inject.Inject
    @UiField(provided = true)
    SinglePicker birthDay;

    @UiField
    TextBox workPhone;

    @UiField
    TextBox mobilePhone;

    @UiField
    ValidableTextBox workEmail;

    @UiField
    ValidableTextBox ipAddress;

    @UiField
    LabelElement ipAddressLabel;

    @UiField
    LabelElement workEmailLabel;

    @UiField
    CheckBox changeAccount;

    @Inject
    @UiField(provided = true)
    CompanyDepartmentSelector companyDepartmentSelector;

    @Inject
    @UiField(provided = true)
    WorkerPositionSelector workerPositionSelector;

    @Inject
    @UiField ( provided = true )
    HomeCompanyButtonSelector company;

    @Inject
    @UiField(provided = true)
    GenderButtonSelector gender;

    @UiField
    HTMLPanel employeeFired;

    private Timer limitedFieldsValidationTimer = new Timer() {
        @Override
        public void run() {
            if (activity != null) {
                activity.validateLimitedFields();
                activity.checkLastNameChanged();
            }
        }
    };

    private AbstractEmployeeEditActivity activity;

    private static EmployeeEditViewUiBinder ourUiBinder = GWT.create(EmployeeEditViewUiBinder.class);
    interface EmployeeEditViewUiBinder extends UiBinder<HTMLPanel, EmployeeEditView> {}
}