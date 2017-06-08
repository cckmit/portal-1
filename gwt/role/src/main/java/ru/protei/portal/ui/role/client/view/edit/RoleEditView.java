package ru.protei.portal.ui.role.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.single.SinglePicker;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanySelector;
import ru.protei.portal.ui.common.client.widget.selector.dict.GenderButtonSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.role.client.activity.edit.AbstractRoleEditActivity;
import ru.protei.portal.ui.role.client.activity.edit.AbstractRoleEditView;

import java.util.Date;

/**
 * Представление создания и редактирования контактного лица
 */
public class RoleEditView extends Composite implements AbstractRoleEditView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setActivity(AbstractRoleEditActivity activity) {
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
    public HasText workEmail() {
        return workEmail;
    }

//    @Override
//    public HasText personalEmail() {
//        return personalEmail;
//    }

    @Override
    public HasText workFax() {
        return workFax;
    }

//    @Override
//    public HasText homeFax() {
//        return homeFax;
//    }

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
    public HasValidable companyValidator(){
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
    public boolean isValidLogin() {
        return status != null && status.equals(NameStatus.ERROR) ? false : true;
    }

    @Override
    public void showInfo( boolean isShow ) {
        infoPanel.setVisible( isShow );
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

    @UiHandler("login")
    public void onChangeContactLogin( KeyUpEvent keyUpEvent ) {
        verifiableIcon.setClassName( NameStatus.UNDEFINED.getStyle());
        timer.cancel();
        timer.schedule( 300 );
    }

    @UiField
    Button saveButton;

    @UiField
    Button cancelButton;

    @UiField
    ValidableTextBox firstName;

    @UiField
    ValidableTextBox lastName;

    @UiField
    TextBox secondName;

    @UiField
    TextBox displayName;

    @UiField
    TextBox shortName;

    @Inject
    @UiField(provided = true)
    SinglePicker birthDay;

    @UiField
    TextBox workPhone;

    @UiField
    TextBox homePhone;

    @UiField
    TextBox workEmail;

//    @UiField
//    TextBox personalEmail;

    @UiField
    TextBox workFax;

//    @UiField
//    TextBox homeFax;

    @UiField
    TextArea workAddress;

    @UiField
    TextArea homeAddress;

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
    TextBox login;

    @UiField
    PasswordTextBox password;

    @UiField
    Element verifiableIcon;

    @UiField
    HTMLPanel infoPanel;

    @UiField
    PasswordTextBox confirmPassword;

    Timer timer = new Timer() {
        @Override
        public void run() {
            if ( activity != null ) {
                activity.onChangeContactLogin();
            }
        }
    };

    NameStatus status;

    AbstractRoleEditActivity activity;

    private static ContactViewUiBinder ourUiBinder = GWT.create(ContactViewUiBinder.class);
    interface ContactViewUiBinder extends UiBinder<HTMLPanel, RoleEditView > {}
}
