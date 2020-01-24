package ru.protei.portal.ui.contact.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.events.InputEvent;
import ru.protei.portal.ui.common.client.view.passwordgen.popup.PasswordGenPopup;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyModel;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanySelector;
import ru.protei.portal.ui.common.client.widget.selector.dict.GenderButtonSelector;
import ru.protei.portal.ui.common.client.widget.subscription.locale.LocaleButtonSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.contact.client.activity.edit.AbstractContactEditActivity;
import ru.protei.portal.ui.contact.client.activity.edit.AbstractContactEditView;

import java.util.Arrays;
import java.util.Date;

/**
 * Представление создания и редактирования контактного лица
 */
public class ContactEditView extends Composite implements AbstractContactEditView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        companyModel.setCategories( Arrays.asList(
                En_CompanyCategory.CUSTOMER,
                En_CompanyCategory.PARTNER,
                En_CompanyCategory.SUBCONTRACTOR ) );
        company.setAsyncModel( companyModel );
        workEmail.setRegexp( CrmConstants.Masks.EMAIL );
        personalEmail.setRegexp( CrmConstants.Masks.EMAIL );
        password.addDomHandler(event -> {
            changeContactLoginTimer.schedule( 300 );
            setPasswordGenPopupVisible(StringUtils.isBlank(password().getText()));
        }, InputEvent.getType());
        passwordGenPopup.addClickHandler(event -> {
            if (activity != null) {
                activity.onPasswordGenerationClicked();
            }
        });
    }

    @Override
    public void setActivity(AbstractContactEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setGeneratePasswordHandler(ClickHandler handler) {
        passwordGenPopup.addClickHandler(handler);
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
    public HasText displayName() {
        return displayName;
    }

    @Override
    public HasText shortName() {
        return shortName;
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
    public HasText homePhone() {
        return homePhone;
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
    public HasText workFax() {
        return workFax;
    }

    @Override
    public HasText homeFax() {
        return homeFax;
    }

    @Override
    public HasText workAddress() {
        return workAddress;
    }

    @Override
    public HasText homeAddress() {
        return homeAddress;
    }

    @Override
    public HasText displayPosition() {
        return displayPosition;
    }

    @Override
    public HasText displayDepartment() {
        return displayDepartment;
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
    public HasValue<String> locale() {
        return locale;
    }

    @Override
    public HasText personInfo() {
        return personInfo;
    }

    @Override
    public HasText login() {
        return login;
    }

    @Override
    public void setContactLoginStatus(NameStatus status) {
        this.status = status;
        verifiableIcon.setClassName(status.getStyle());
    }

    @Override
    public HasText password() {
        return password;
    }

    @Override
    public HasText confirmPassword() {
        return confirmPassword;
    }

    @Override
    public HasValue<Boolean> sendWelcomeEmail() {
        return sendWelcomeEmail;
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
    public void showInfo( boolean isShow ) {
        infoPanel.setVisible( isShow );
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
        return contactFired;
    }

    @Override
    public HasVisibility deletedMsgVisibility() {
        return contactDeleted;
    }

    @Override
    public HasVisibility sendWelcomeEmailVisibility() {
        return sendWelcomeEmail;
    }

    @Override
    public HasVisibility fireBtnVisibility() {
        return fireBtn;
    }

    @Override
    public HasVisibility sendEmailWarningVisibility() {
        return sendWelcomeEmailWarning;
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
    public HasVisibility shortNameErrorLabelVisibility() {
        return shortNameErrorLabel;
    }

    @Override
    public HasVisibility loginErrorLabelVisibility() {
        return loginErrorLabel;
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
    public HasText shortNameErrorLabel() {
        return shortNameErrorLabel;
    }

    @Override
    public HasText loginErrorLabel() {
        return loginErrorLabel;
    }

    @Override
    public HasEnabled saveEnabled() {
        return saveButton;
    }

    @Override
    public NameStatus getContactLoginStatus() {
        return status;
    }

    @Override
    public HasText firstNameLabel() {
        return firstNameLabel;
    }

    @Override
    public HasText secondNameLabel() {
        return secondNameLabel;
    }

    @Override
    public HasText lastNameLabel() {
        return lastNameLabel;
    }

    @Override
    public HasText shortNameLabel() {
        return shortNameLabel;
    }

    @Override
    public HasText personalEmailLabel() {
        return personalEmailLabel;
    }

    @Override
    public HasText workEmailLabel() {
        return workEmailLabel;
    }

    @Override
    public String loginLabel() {
        return loginLabel.getInnerText();
    }

    @Override
    public void setPasswordVisible(Boolean isPasswordVisible) {
        password.getElement().setAttribute("type", isPasswordVisible ? "text" : "password");
        confirmPassword.getElement().setAttribute("type", isPasswordVisible ? "text" : "password");
    }

    @Override
    public void setPasswordGenPopupVisible(boolean isVisible) {
        if (isVisible) {
            passwordGenPopup.showNear(password);
        } else {
            passwordGenPopup.hide();
        }
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

    @UiHandler("login")
    public void onChangeContactLogin( InputEvent inputEvent ) {
        verifiableIcon.setClassName( NameStatus.UNDEFINED.getStyle() );
        changeContactLoginTimer.cancel();
        changeContactLoginTimer.schedule( 300 );
    }

    @UiHandler({"workEmail", "personalEmail"})
    public void onChangeEmail( KeyUpEvent keyUpEvent ) {
        changeContactEmailTimer.cancel();
        changeContactEmailTimer.schedule( 300 );
    }

    @UiHandler("sendWelcomeEmail")
    public void onClickSendWelcomeEmail( ClickEvent event ) {
        if (activity != null) {
            activity.onChangeSendWelcomeEmail();
        }
    }

    @UiHandler({"firstName", "secondName", "lastName", "shortName"})
    public void onLimitedFieldsChanged(InputEvent event) {
        resetValidateTimer();
    }

    @UiHandler("company")
    public void onCompanySelected(ValueChangeEvent<EntityOption> event) {
        if (activity != null) {
            activity.onCompanySelected();
        }
    }

    @UiHandler("showPassword")
    public void onShowPasswordClicked(ValueChangeEvent<Boolean> event) {
        setPasswordVisible(event.getValue());
    }

    @UiHandler("password")
    public void onPasswordClicked(ClickEvent event) {
        setPasswordGenPopupVisible(StringUtils.isBlank(password().getText()));
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
    ValidableTextBox shortName;

    @UiField
    Label firstNameErrorLabel;

    @UiField
    Label lastNameErrorLabel;

    @UiField
    Label secondNameErrorLabel;

    @UiField
    Label shortNameErrorLabel;

    @UiField
    Label loginErrorLabel;

    @UiField
    Label firstNameLabel;

    @UiField
    Label lastNameLabel;

    @UiField
    Label secondNameLabel;

    @UiField
    Label shortNameLabel;

    @UiField
    TextBox displayName;

    @Inject
    @UiField(provided = true)
    SinglePicker birthDay;

    @UiField
    TextBox workPhone;

    @UiField
    TextBox homePhone;

    @UiField
    TextBox mobilePhone;

    @UiField
    ValidableTextBox workEmail;

    @UiField
    ValidableTextBox personalEmail;

    @UiField
    TextBox workFax;

    @UiField
    TextBox homeFax;

    @UiField
    TextArea workAddress;

    @UiField
    TextArea homeAddress;

    @UiField
    Label personalEmailLabel;

    @UiField
    Label workEmailLabel;

    @UiField
    LabelElement loginLabel;

    @UiField
    TextBox displayPosition;

    @UiField
    TextBox displayDepartment;

    @UiField
    TextArea personInfo;

    @Inject
    @UiField ( provided = true )
    CompanySelector company;

    @Inject
    @UiField(provided = true)
    GenderButtonSelector gender;

    @UiField
    ValidableTextBox login;

    @UiField
    PasswordTextBox password;

    @UiField
    ToggleButton showPassword;

    @UiField
    Element verifiableIcon;

    @UiField
    HTMLPanel infoPanel;

    @UiField
    PasswordTextBox confirmPassword;

    @UiField
    HTMLPanel contactFired;

    @UiField
    HTMLPanel contactDeleted;

    @Inject
    @UiField(provided = true)
    LocaleButtonSelector locale;

    @UiField
    CheckBox sendWelcomeEmail;
    @UiField
    Label sendWelcomeEmailWarning;

    @Inject
    CompanyModel companyModel;

    Timer changeContactLoginTimer = new Timer() {
        @Override
        public void run() {
            if ( activity != null ) {
                activity.onChangeContactLogin();
            }
        }
    };

    Timer changeContactEmailTimer = new Timer() {
        @Override
        public void run() {
            if ( activity != null ) {
                activity.onChangeSendWelcomeEmail();
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

    private PasswordGenPopup passwordGenPopup = new PasswordGenPopup();

    NameStatus status;

    AbstractContactEditActivity activity;

    private static ContactViewUiBinder ourUiBinder = GWT.create(ContactViewUiBinder.class);
    interface ContactViewUiBinder extends UiBinder<HTMLPanel, ContactEditView> {}
}
