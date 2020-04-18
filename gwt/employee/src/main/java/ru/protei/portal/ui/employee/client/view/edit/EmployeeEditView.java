package ru.protei.portal.ui.employee.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.single.SinglePicker;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.events.InputEvent;
import ru.protei.portal.ui.common.client.view.passwordgen.popup.PasswordGenPopup;
import ru.protei.portal.ui.common.client.widget.departmentselector.popup.DepartmentSelector;
import ru.protei.portal.ui.common.client.widget.homecompany.HomeCompanyButtonSelector;
import ru.protei.portal.ui.common.client.widget.passwordbox.PasswordTextBox;
import ru.protei.portal.ui.common.client.widget.positionselector.popup.PositionSelector;
import ru.protei.portal.ui.common.client.widget.selector.dict.GenderButtonSelector;
import ru.protei.portal.ui.common.client.widget.subscription.locale.LocaleButtonSelector;
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
        personalEmail.setRegexp( CrmConstants.Masks.EMAIL );
        workEmail.setMaxLength( CrmConstants.EMAIL_MAX_SIZE );
        personalEmail.setMaxLength( CrmConstants.EMAIL_MAX_SIZE );

        departmentSelector.addValueChangeHandler(event -> {
            if (event.getValue() != null) {
                companyDepartment.setText(event.getValue().getName());
                activity.onChangedCompanyDepartment(event.getValue().getId());
                setDepartmentValid(true);
            }
        });
        departmentSelector.addAddHandler(addEvent -> activity.onAddCompanyDepartmentClicked());
        departmentSelector.addEditHandler(editEvent -> activity.onEditCompanyDepartmentClicked(editEvent.companyDepartment));
        companyDepartmentEnabled().setEnabled(false);

        positionSelector.addValueChangeHandler(event -> {
            if (event.getValue() != null) {
                workerPosition.setText(event.getValue().getName());
                activity.onChangedWorkerPosition(event.getValue().getId());
                setPositionValid(true);
            }
        });
        positionSelector.addAddHandler(addEvent -> activity.onAddWorkerPositionClicked());
        positionSelector.addEditHandler(editEvent -> activity.onEditWorkerPositionClicked(editEvent.workerPosition));
        workerPositionEnabled().setEnabled(false);
    }

    @Override
    public void setActivity(AbstractEmployeeEditActivity activity) {
        this.activity = activity;
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
    public HasText workEmail() {
        return workEmail;
    }

    @Override
    public HasText personalEmail() {
        return personalEmail;
    }

    @Override
    public HasText workerPosition() {
        return workerPosition;
    }

    @Override
    public HasText companyDepartment() {
        return companyDepartment;
    }

    @Override
    public HasEnabled companyDepartmentEnabled() {
        return companyDepartment;
    }

    @Override
    public HasEnabled workerPositionEnabled() {
        return workerPosition;
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
    public HasValidable personalEmailValidator(){
        return personalEmail;
    }

    @Override
    public void setDepartmentCompanyId (Long companyId){
        departmentSelector.setCompanyId(companyId);
    }

    @Override
    public void setPositionCompanyId (Long companyId){
        positionSelector.setCompanyId(companyId);
    }

    @Override
    public void setPositionValid (boolean isValid){
        workerPosition.setStyleName("has-error", !isValid);
    }

    @Override
    public void setDepartmentValid (boolean isValid){
        companyDepartment.setStyleName("has-error", !isValid);
    }

    @Override
    public boolean isPositionValid (){
        return !workerPosition.getStyleName().contains("has-error");
    }

    @Override
    public boolean isDepartmentValid (){
        return !companyDepartment.getStyleName().contains("has-error");
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
    public HasVisibility deletedMsgVisibility() {
        return employeeDeleted;
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
    public String personalEmailLabel() {
        return personalEmailLabel.getInnerText();
    }

    @Override
    public String workEmailLabel() {
        return workEmailLabel.getInnerText();
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

    @UiHandler({"workEmail", "personalEmail"})
    public void onChangeEmail( KeyUpEvent keyUpEvent ) {
        changeEmployeeEmailTimer.cancel();
        changeEmployeeEmailTimer.schedule( 300 );
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

    @UiHandler("companyDepartment")
    public void onDisplayDepartmentClicked(ClickEvent event){
        departmentSelector.showUnderLeft( companyDepartment, companyDepartment.getOffsetWidth() );
    }

    @UiHandler("workerPosition")
    public void onDisplayPositionClicked(ClickEvent event){
        positionSelector.showUnderLeft( workerPosition, workerPosition.getOffsetWidth() );
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
    ValidableTextBox personalEmail;

    @UiField
    LabelElement personalEmailLabel;

    @UiField
    LabelElement workEmailLabel;

    @UiField
    Button workerPosition;

    @UiField
    Button companyDepartment;

    @Inject
    @UiField ( provided = true )
    HomeCompanyButtonSelector company;

    @Inject
    @UiField(provided = true)
    GenderButtonSelector gender;

    @UiField
    HTMLPanel employeeFired;

    @UiField
    HTMLPanel employeeDeleted;

    @Inject
    private DepartmentSelector departmentSelector;

    @Inject
    private PositionSelector positionSelector;


    private Timer changeEmployeeEmailTimer = new Timer() {
        @Override
        public void run() {
            if ( activity != null ) {
                //activity.onChangeSendWelcomeEmail();
            }
        }
    };

    private Timer limitedFieldsValidationTimer = new Timer() {
        @Override
        public void run() {
            if (activity != null) {
                activity.validateLimitedFields();
            }
        }
    };

    private AbstractEmployeeEditActivity activity;

    private static EmployeeEditViewUiBinder ourUiBinder = GWT.create(EmployeeEditViewUiBinder.class);
    interface EmployeeEditViewUiBinder extends UiBinder<HTMLPanel, EmployeeEditView> {}
}