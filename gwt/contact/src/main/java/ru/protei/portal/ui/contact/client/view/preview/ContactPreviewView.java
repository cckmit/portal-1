package ru.protei.portal.ui.contact.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
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
    public void setDisplayName(String value) { this.displayName.setInnerText( value ); }

    @Override
    public void setShortName(String value) { this.shortName.setInnerText( value ); }

    @Override
    public void setGender(String value) { this.gender.setInnerText( value ); }

    @Override
    public void setBirthday(String value) { this.birthday.setInnerText( value ); }

    @Override
    public void setCompany ( String value ) { this.company.setInnerText( value ); }

    @Override
    public void setPosition(String value) { this.position.setInnerText( value ); }

    @Override
    public void setDepartment(String value) { this.department.setInnerText( value ); }

    @Override
    public void setPhone(String value) { this.phone.setInnerText( value ); }

    @Override
    public void setEmail(String value) { this.email.setInnerText( value ); }

    @Override
    public void setAddress(String value) { this.address.setInnerText( value ); }

    @Override
    public void setHomeAddress(String value) { this.homeAddress.setInnerText( value ); }

    @Override
    public void setInfo(String value) { this.info.setInnerText( value ); }

    @Override
    public void showFullScreen ( boolean value ) {

        fullScreen.setVisible( !value );
        if (value)
            preview.addStyleName( "col-xs-12 col-md-6" );
        else
            preview.setStyleName( "preview" );
    }

    @Override
    public HTMLPanel preview () { return preview; }

    @UiHandler( "fullScreen" )
    public void onFullScreenClicked ( ClickEvent event) {
        event.preventDefault();

        if ( activity != null ) {
            activity.onFullScreenPreviewClicked();
        }
    }

    @UiField
    HTMLPanel preview;
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
    SpanElement phone;
    @UiField
    SpanElement email;
    @UiField
    SpanElement address;
    @UiField
    SpanElement homeAddress;
    @UiField
    SpanElement company;
    @UiField
    SpanElement position;
    @UiField
    SpanElement department;
    @UiField
    Anchor fullScreen;

    @Inject
    @UiField
    Lang lang;

    AbstractContactPreviewActivity activity;

    interface ContactPreviewViewUiBinder extends UiBinder<HTMLPanel, ContactPreviewView> { }
    private static ContactPreviewViewUiBinder ourUiBinder = GWT.create(ContactPreviewViewUiBinder.class);
}