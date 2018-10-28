package ru.protei.portal.ui.contact.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
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
    public void setDisplayName(String value) { this.displayName.setInnerHTML( value ); }

    @Override
    public void setBirthday(String value) { this.birthday.setInnerText( value ); }

    @Override
    public void setCompany ( String value ) { this.company.setText( value ); }

    @Override
    public void setPosition(String value) { this.position.setText( value ); }

    @Override
    public void setPhone(String value) { this.phone.setInnerText( value ); }

    @Override
    public void setEmail(String value) { this.email.setInnerText( value ); }

    @Override
    public void setAddress(String value) { this.address.setInnerHTML( value ); }

    @Override
    public void setHomeAddress(String value) { this.homeAddress.setInnerHTML( value ); }

    @Override
    public void setInfo(String value) { this.info.setText( value ); }

    @Override
    public void showFullScreen ( boolean value ) {

        fullScreen.setVisible( !value );
        if (value)
            preview.addStyleName( "col-md-12 col-lg-6" );
        else
            preview.setStyleName( "" );
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

    @Override
    public void setGenderImage(String icon) {
        genderImage.setSrc(icon);
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
    HeadingElement displayName;
    @UiField
    SpanElement birthday;
    @UiField
    InlineLabel info;
    @UiField
    SpanElement phone;
    @UiField
    SpanElement email;
    @UiField
    SpanElement address;
    @UiField
    SpanElement homeAddress;
    @UiField
    InlineLabel company;
    @UiField
    InlineLabel position;
    @UiField
    Anchor fullScreen;
    @UiField
    HTMLPanel contactFired;
    @UiField
    HTMLPanel contactDeleted;

    @Inject
    @UiField
    Lang lang;
    @UiField
    ImageElement genderImage;

    @Inject
    FixedPositioner positioner;

    AbstractContactPreviewActivity activity;

    interface ContactPreviewViewUiBinder extends UiBinder<HTMLPanel, ContactPreviewView> { }
    private static ContactPreviewViewUiBinder ourUiBinder = GWT.create(ContactPreviewViewUiBinder.class);
}