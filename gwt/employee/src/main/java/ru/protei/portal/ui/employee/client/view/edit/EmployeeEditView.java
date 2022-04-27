package ru.protei.portal.ui.employee.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.single.SinglePicker;
import ru.protei.portal.core.model.dict.AttachmentType;
import ru.protei.portal.core.model.dict.En_ContactDataAccess;
import ru.protei.portal.core.model.dict.En_ContactItemType;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.struct.ContactItem;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.events.InputEvent;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.contactitem.group.ContactItemGroupWithValidation;
import ru.protei.portal.ui.common.client.widget.homecompany.HomeCompanyButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.companydepartment.CompanyDepartmentSelector;
import ru.protei.portal.ui.common.client.widget.selector.dict.GenderButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.workerposition.WorkerPositionSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableContactItemBox;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.employee.client.activity.edit.AbstractEmployeeEditActivity;
import ru.protei.portal.ui.employee.client.activity.edit.AbstractEmployeeEditView;

import java.util.Date;
import java.util.List;

import static ru.protei.portal.core.model.util.CrmConstants.ContactConstants.*;
import static ru.protei.portal.core.model.util.CrmConstants.Masks.*;

public class EmployeeEditView extends Composite implements AbstractEmployeeEditView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        workEmail.setRegexp( CrmConstants.Masks.EMAIL );
        ipAddress.setRegexp( CrmConstants.Masks.IP);

        gender.setValid(false);

        companyDepartmentSelector.setEditHandler(editEvent -> activity.onEditCompanyDepartmentClicked(editEvent.id, editEvent.text));
        companyDepartmentSelector.setAddHandler(addEvent -> activity.onAddCompanyDepartmentClicked());

        workerPositionSelector.setEditHandler(editEvent -> activity.onEditWorkerPositionClicked(editEvent.id, editEvent.text));
        workerPositionSelector.setAddHandler(addEvent -> activity.onAddWorkerPositionClicked());

        birthDay.setMandatory(false);

        form.setEncoding(FormPanel.ENCODING_MULTIPART);
        form.setMethod(FormPanel.METHOD_POST);
        fileUpload.getElement().setAttribute("accept", AttachmentType.JPEG.mimeType);

        firstNameErrorLabel.setText(lang.promptFieldLengthExceed(firstNameLabel(), FIRST_NAME_SIZE));
        secondNameErrorLabel.setText(lang.promptFieldLengthExceed(secondNameLabel(), SECOND_NAME_SIZE));
        lastNameErrorLabel.setText(lang.promptFieldLengthExceed(lastNameLabel(), LAST_NAME_SIZE));
        innErrorLabel.setText(lang.promptFieldLengthNotEqual(innLabel(), INN_SIZE));

        company.setSynchronizeWith1C(false);

        workPhones.setRegexp(WORK_PHONE_NUMBER_PATTERN);
        mobilePhones.setRegexp(RUS_PHONE_NUMBER_PATTERN);

        workPhones.setNewContactItem(() -> new ContactItem(En_ContactItemType.GENERAL_PHONE, En_ContactDataAccess.PUBLIC));
        mobilePhones.setNewContactItem(() -> new ContactItem(En_ContactItemType.MOBILE_PHONE, En_ContactDataAccess.PUBLIC));

        workEmail.setSetTypeAndAccess(contactItem -> contactItem.modify(En_ContactDataAccess.PUBLIC).modify(En_ContactItemType.EMAIL));
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
    public HasEnabled innEnabled() {
        return inn;
    }

    @Override
    public HasEnabled birthDayEnabled() {
        return birthDay;
    }

    @Override
    public HasVisibility birthDayVisibility() {
        return birthDayContainer;
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
        return mobilePhones;
    }

    @Override
    public HasEnabled workPhoneEnabled() {
        return workPhones;
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
    public HasValue<Boolean> contractAgreement() {
        return contractAgreement;
    }

    @Override
    public HasText secondName() {
        return secondName;
    }

    @Override
    public HasText inn() {
        return inn;
    }

    @Override
    public HasValue<Date> birthDay() {
        return birthDay;
    }

    @Override
    public void setBirthDayTimeZone (TimeZone timeZone) {
        birthDay.setTimeZone(timeZone);
    }

    @Override
    public HasValue<List<ContactItem>> workPhones() {
        return workPhones;
    }

    @Override
    public HasValue<List<ContactItem>> mobilePhones() {
        return mobilePhones;
    }

    @Override
    public HasValue<ContactItem> workEmail() {
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
    public HasValue<EntityOption> company() {
        return company;
    }

    @Override
    public HasValue<En_Gender> gender() {
        return gender;
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
    public HasValidable workPhonesValidator() {
        return workPhones;
    }

    @Override
    public HasValidable mobilePhonesValidator() {
        return mobilePhones;
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
    public HasValidable genderValidator(){
        return gender;
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
    public HasVisibility innErrorLabelVisibility() {
        return innErrorLabel;
    }

    @Override
    public HasVisibility lastNameErrorLabelVisibility() {
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
    public String innLabel() {
        return innLabel.getInnerText();
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
        return changeAccountContainer;
    }

    @Override
    public HasWidgets getPositionsContainer() {
        return positionsContainer;
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
    public void setWorkerPositionsEditable(boolean isEditable) {
        workerPositionSelector.setEditable(isEditable);
    }

    @Override
    public void setAddButtonWorkerPositionVisible(boolean isVisible) {
        workerPositionSelector.setAddButtonVisible(isVisible);
    }

    @Override
    public HandlerRegistration addChangeHandler(ChangeHandler changeHandler) {
        return fileUpload.addChangeHandler(changeHandler);
    }

    @Override
    public void submitAvatar(String url){
        if(!fileUpload.getFilename().isEmpty()) {
            form.setAction(url);
            form.submit();
        }
    }

    @Override
    public void setFileUploadEnabled(boolean isEnabled){
        fileUpload.setEnabled(isEnabled);
        imageContainer.setStyleName("upload-enabled", isEnabled);
    }

    @Override
    public HandlerRegistration addSubmitCompleteHandler(FormPanel.SubmitCompleteHandler submitCompleteHandler) {
        return form.addSubmitCompleteHandler(submitCompleteHandler);
    }

    @Override
    public void setAvatarUrl(String url) {
        image.setUrl(url);
    }

    @Override
    public void setAvatarLabelText(String text){
        imageLabel.setText(text);
    }

    @Override
    public void refreshHomeCompanies(Boolean isSynchronize){
        company.setSynchronizeWith1C(isSynchronize);
        company.setValue(null);
        activity.onCompanySelected();
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

    @UiHandler("addPositionBtn")
    public void onAddPositionClicked( ClickEvent event ) {
        if (activity != null) {
            activity.onAddPositionBtnClicked();
        }
    }

    @UiHandler({"firstName", "secondName", "lastName", "inn"})
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

    @UiHandler("contractAgreement")
    public void onContractAgreementChanged(ValueChangeEvent<Boolean> event) {
        if (activity != null) {
            activity.onContractAgreementChanged(event.getValue());
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
    ValidableTextBox inn;

    @UiField
    Label firstNameErrorLabel;

    @UiField
    Label lastNameErrorLabel;

    @UiField
    Label secondNameErrorLabel;

    @UiField
    Label innErrorLabel;

    @UiField
    LabelElement firstNameLabel;

    @UiField
    LabelElement lastNameLabel;

    @UiField
    LabelElement secondNameLabel;

    @UiField
    LabelElement innLabel;

    @com.google.inject.Inject
    @UiField(provided = true)
    SinglePicker birthDay;

    @UiField
    HTMLPanel birthDayContainer;

    @UiField
    ContactItemGroupWithValidation workPhones;

    @UiField
    ContactItemGroupWithValidation mobilePhones;

    @UiField
    ValidableContactItemBox workEmail;

    @UiField
    ValidableTextBox ipAddress;

    @UiField
    LabelElement ipAddressLabel;

    @UiField
    LabelElement workEmailLabel;

    @UiField
    CheckBox changeAccount;

    @UiField
    HTMLPanel changeAccountContainer;

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

    @UiField
    HTMLPanel positionsContainer;

    @UiField
    Image image;

    @UiField
    Label imageLabel;

    @UiField
    HTMLPanel imageContainer;

    @UiField
    FormPanel form;

    @UiField
    FileUpload fileUpload;

    @UiField
    Button addPositionBtn;

    @UiField
    CheckBox contractAgreement;

    @UiField
    Lang lang;

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
