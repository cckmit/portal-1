package ru.protei.portal.ui.contact.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.single.SinglePicker;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.ui.contact.client.activity.edit.AbstractContactEditActivity;
import ru.protei.portal.ui.contact.client.activity.edit.AbstractContactEditView;
import ru.protei.portal.ui.contact.client.widget.company.buttonselector.CompanyButtonSelector;

/**
 * Created by michael on 02.11.16.
 */
public class ContactEditView extends Composite implements AbstractContactEditView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }


    @Override
    public void setActivity(AbstractContactEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasText firstName() {
        return firstName;
    }

    @Override
    public HasText lastName() {
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
    public SinglePicker birthDay() {
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
    public HasValue<Company> company() {
        return company;
    }

    @Override
    public HasText personInfo() {
        return personInfo;
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
    TextBox firstName;

    @UiField
    TextBox lastName;

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

    @UiField
    TextBox personalEmail;

    @UiField
    TextBox workFax;

    @UiField
    TextBox homeFax;

    @UiField
    TextArea workAddress;

    @UiField
    TextArea homeAddress;

    @UiField
    TextArea displayPosition;

    @UiField
    TextArea displayDepartment;

    @UiField
    TextArea personInfo;

    @Inject
    @UiField ( provided = true )
    CompanyButtonSelector company;


//
//
//    @UiField
//    TextArea personInfo;

    AbstractContactEditActivity activity;


    private static ContactViewUiBinder ourUiBinder = GWT.create(ContactViewUiBinder.class);
    interface ContactViewUiBinder extends UiBinder<HTMLPanel, ContactEditView> {}

}
