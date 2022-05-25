package ru.protei.portal.ui.contact.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
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
    public void setDisplayName(String value) {
        this.displayName.setHTML( value );
    }

    @Override
    public void setDisplayNameHref(String link) {
        this.displayName.setHref(link);
    }

    @Override
    public void setBirthday(String value) { this.birthday.setInnerText( value ); }

    @Override
    public void setLogins(String value) { this.logins.setInnerText( value ); }

    @Override
    public void setCompany ( String value ) { this.company.setText( value ); }

    @Override
    public void setPosition(String value) { this.position.setText( value ); }

    @Override
    public void setPhone(String value) { this.phone.setInnerText( value ); }

    @Override
    public void setEmail(String value) { this.emailAnchor.setInnerHTML(value); }

    @Override
    public void setAddress(String value) { this.address.setInnerText( value ); }

    @Override
    public void setHomeAddress(String value) { this.homeAddress.setInnerText( value ); }

    @Override
    public void setInfo(String value) { this.info.setInnerText(value); }

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

    @UiHandler( "displayName" )
    public void onFullScreenClicked ( ClickEvent event) {
        event.preventDefault();

        if ( activity != null ) {
            activity.onFullScreenPreviewClicked();
        }
    }

    @Override
    public void showFullScreen(boolean isFullScreen) {
        backButtonPanel.setVisible(isFullScreen);
        previewWrapperContainer.setStyleName("card card-transparent no-margin preview-wrapper card-with-fixable-footer", isFullScreen);
    }

    @UiHandler("backButton")
    public void onBackButtonClicked(ClickEvent event) {
        if (activity != null) {
            activity.onBackButtonClicked();
        }
    }


    @UiField
    Anchor displayName;
    @UiField
    SpanElement birthday;
    @UiField
    SpanElement logins;
    @UiField
    SpanElement info;
    @UiField
    SpanElement phone;
    @UiField
    SpanElement address;
    @UiField
    SpanElement homeAddress;
    @UiField
    InlineLabel company;
    @UiField
    InlineLabel position;
    @UiField
    SpanElement emailAnchor;
    @UiField
    HTMLPanel contactFired;
    @UiField
    HTMLPanel contactDeleted;
    @UiField
    HTMLPanel previewWrapperContainer;
    @UiField
    HTMLPanel backButtonPanel;
    @UiField
    Button backButton;

    @Inject
    @UiField
    Lang lang;
    @UiField
    ImageElement genderImage;

    private AbstractContactPreviewActivity activity;

    interface ContactPreviewViewUiBinder extends UiBinder<HTMLPanel, ContactPreviewView> { }
    private static ContactPreviewViewUiBinder ourUiBinder = GWT.create(ContactPreviewViewUiBinder.class);
}