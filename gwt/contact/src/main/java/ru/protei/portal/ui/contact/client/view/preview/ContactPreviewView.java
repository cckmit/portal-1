package ru.protei.portal.ui.contact.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.contact.client.activity.preview.AbstractContactPreviewActivity;
import ru.protei.portal.ui.contact.client.activity.preview.AbstractContactPreviewView;

/**
 * Вид превью контакта
 */
public class ContactPreviewView extends Composite implements AbstractContactPreviewView {

    public ContactPreviewView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractContactPreviewActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setLastName(String value) { this.lastName.setInnerText( value ); }

    @Override
    public void setFirstName(String value) { this.firstName.setInnerText( value ); }

    @Override
    public void setSecondName(String value) { this.secondName.setInnerText( value ); }

    @Override
    public void setDisplayName(String value) { this.secondName.setInnerText( value ); }

    @Override
    public void setShortName(String value) { this.secondName.setInnerText( value ); }

    @Override
    public void setGender(String value) {
        this.gender.setInnerText( value );
    }

    @Override
    public void setBirthday(String value) {
        this.birthday.setInnerText( value );
    }

    @Override
    public void setCompany ( String value ) { this.company.setInnerText( value ); }

    @Override
    public void setPosition(String value) { this.position.setInnerText( value ); }

    @Override
    public void setDepartment(String value) { this.department.setInnerText( value ); }

    @Override
    public void setWorkPhone(String value) { this.workPhone.setInnerText( value ); }

    @Override
    public void setPersonalPhone(String value) { this.personalPhone.setInnerText( value ); }

    @Override
    public void setWorkFax(String value) {
        this.workFax.setInnerText( value );
    }

    @Override
    public void setPersonalFax(String value) {
        this.personalFax.setInnerText( value );
    }

    @Override
    public void setWorkEmail(String value) {
        this.workEmail.setInnerText( value );
    }

    @Override
    public void setPersonalEmail(String value) {
        this.personalEmail.setInnerText( value );
    }

    @Override
    public void setWorkAddress(String value) {
        this.workAddress.setInnerText( value );
    }

    @Override
    public void setPersonalAddress(String value) {
        this.personalAddress.setInnerText( value );
    }

    @Override
    public void setInfo(String value) {
        this.info.setInnerText( value );
    }

    @Override
    public void setLinkToPreview(String value) {
        this.link.setInnerText( lang.linkToObject() );
    }


    @UiField
    SpanElement lastName;
    @UiField
    SpanElement firstName;
    @UiField
    SpanElement secondName;
    @UiField
    SpanElement displayName;
    @UiField
    SpanElement shortName;
    @UiField
    SpanElement gender;
    @UiField
    SpanElement birthday;
    @UiField
    SpanElement info;
    @UiField
    SpanElement workPhone;
    @UiField
    SpanElement personalPhone;
    @UiField
    SpanElement workFax;
    @UiField
    SpanElement personalFax;
    @UiField
    SpanElement workEmail;
    @UiField
    SpanElement personalEmail;
    @UiField
    SpanElement workAddress;
    @UiField
    SpanElement personalAddress;
    @UiField
    SpanElement company;
    @UiField
    SpanElement position;
    @UiField
    SpanElement department;
    @UiField
    SpanElement link;

    @Inject
    @UiField
    Lang lang;

    AbstractContactPreviewActivity activity;

    interface ContactPreviewViewUiBinder extends UiBinder<HTMLPanel, ContactPreviewView> { }
    private static ContactPreviewViewUiBinder ourUiBinder = GWT.create(ContactPreviewViewUiBinder.class);
}