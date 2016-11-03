package ru.protei.portal.ui.contact.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
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
    public void setLastName(String value) {
        this.lastName.setInnerText( value );
    }

    @Override
    public void setFirstName(String value) {
        this.firstName.setInnerText( value );
    }

    @Override
    public void setSecondName(String value) {
        this.secondName.setInnerText( value );
    }

    @Override
    public void setSex(String value) {
        this.sex.setInnerText( value );
    }

    @Override
    public void setBirthday(String value) {
        this.birthday.setInnerText( value );
    }

    @Override
    public void setPosition(String value) {
        this.position.setInnerText( value );
    }

    @Override
    public void setDepartment(String value) {
        this.department.setInnerText( value );
    }

    @Override
    public void setPhone(String value) {
        this.phone.setInnerText( value );
    }

    @Override
    public void setFax(String value) {
        this.fax.setInnerText( value );
    }

    @Override
    public void setEmail(String value) {
        this.email.setInnerText( value );
    }

    @Override
    public void setAddress(String value) {
        this.address.setInnerText( value );
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
    LabelElement lastName;
    @UiField
    LabelElement firstName;
    @UiField
    LabelElement secondName;
    @UiField
    LabelElement sex;
    @UiField
    LabelElement birthday;
    @UiField
    LabelElement info;
    @UiField
    LabelElement phone;
    @UiField
    LabelElement fax;
    @UiField
    LabelElement email;
    @UiField
    LabelElement address;
    @UiField
    LabelElement position;
    @UiField
    LabelElement department;
    @UiField
    LabelElement link;

    @Inject
    @UiField
    Lang lang;

    AbstractContactPreviewActivity activity;

    interface ContactPreviewViewUiBinder extends UiBinder<HTMLPanel, ContactPreviewView> { }
    private static ContactPreviewViewUiBinder ourUiBinder = GWT.create(ContactPreviewViewUiBinder.class);
}