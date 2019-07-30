package ru.protei.portal.ui.contact.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
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
    protected void onAttach() {
        super.onAttach();
        positioner.watch(this, FixedPositioner.NAVBAR_TOP_OFFSET);
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        positioner.ignore(this);
    }

    @Override
    public void setActivity(AbstractContactPreviewActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setLastName(String value) { this.lastName.setInnerHTML( value ); }

    @Override
    public void setFirstName(String value) { this.firstName.setInnerHTML( value ); }

    @Override
    public void setSecondName(String value) { this.secondName.setInnerHTML( value ); }

    @Override
    public void setDisplayName(String value) { this.displayName.setInnerHTML( value ); }

    @Override
    public void setShortName(String value) { this.shortName.setInnerHTML( value ); }

    @Override
    public void setGender(String value) { this.gender.setInnerText( value ); }

    @Override
    public void setBirthday(String value) { this.birthday.setInnerText( value ); }

    @Override
    public void setCompany ( String value ) { this.company.setInnerHTML( value ); }

    @Override
    public void setPosition(String value) { this.position.setInnerHTML( value ); }

    @Override
    public void setDepartment(String value) { this.department.setInnerHTML( value ); }

    @Override
    public void setPhone(String value) { this.phone.setInnerText( value ); }

    @Override
    public void setEmail(String value) { this.emailAnchor.setText( value ); }

    @Override
    public void setMailto(String value) { this.emailAnchor.setHref("mailto:" + value); }

    @Override
    public void setAddress(String value) { this.address.setInnerHTML( value ); }

    @Override
    public void setHomeAddress(String value) { this.homeAddress.setInnerHTML( value ); }

    @Override
    public void setInfo(String value) { this.info.setInnerHTML( value ); }

    @Override
    public void showFullScreen ( boolean value ) {

        fullScreen.setVisible( !value );
        if (value)
            preview.addStyleName( "col-xs-12 col-lg-6" );
        else
            preview.setStyleName( "preview" );
    }

    @Override
    public HTMLPanel preview () { return preview; }

    @Override
    public HasVisibility firedMsgVisibility() {
        return contactFired;
    }

    @Override
    public HasVisibility deletedMsgVisibility() {
        return contactDeleted;
    }

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
    @UiField
    Anchor emailAnchor;
    @UiField
    HTMLPanel contactFired;
    @UiField
    HTMLPanel contactDeleted;

    @Inject
    @UiField
    Lang lang;

    @Inject
    FixedPositioner positioner;

    AbstractContactPreviewActivity activity;

    interface ContactPreviewViewUiBinder extends UiBinder<HTMLPanel, ContactPreviewView> { }
    private static ContactPreviewViewUiBinder ourUiBinder = GWT.create(ContactPreviewViewUiBinder.class);
}