package ru.protei.portal.ui.company.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.common.FixedPositioner;
import ru.protei.portal.ui.company.client.activity.preview.AbstractCompanyPreviewActivity;
import ru.protei.portal.ui.company.client.activity.preview.AbstractCompanyPreviewView;

/**
 * Представление превью компании
 */
public class CompanyPreviewView extends Composite implements AbstractCompanyPreviewView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        watchForScroll(false);
    }

    @Override
    public void watchForScroll(boolean isWatch) {
        if(isWatch)
            positioner.watch(this, FixedPositioner.NAVBAR_TOP_OFFSET);
        else
            positioner.ignore(this);
    }

    @Override
    public void setName( String name ) {
        this.companyName.setInnerText(name);
    }

    @Override
    public void setActivity( AbstractCompanyPreviewActivity activity ) {
        this.activity = activity;
    }

    @Override
    public void setPhone( String value ) {
        this.phone.setInnerText( value );
    }

    @Override
    public void setSite( String value ) {
        String href = value == null ? "#" : value;
        site.setInnerText(value);
        if ( !href.startsWith("http://") && !href.startsWith("htts://") ) {
            href = "http://" + href;
        }
        site.setHref( href );
    }

    @Override
    public void setEmail( String value ) {
        this.email.setInnerText( value );
    }

    @Override
    public void setAddressDejure( String value ) {
        this.addressDejure.setInnerText( value );
    }

    @Override
    public void setAddressFact( String value ) {
        this.addressFact.setInnerText( value );
    }

    @Override
    public void setCategory ( String value ) {
        this.categoryImage.setSrc( value );
    }

    @Override
    public void setCompanyLinksMessage(String value ) {
        this.companyLinksMessage.setText( value );
    }

    @Override
    public void setInfo( String value ) {
        this.info.setText( value );
    }

    @Override
    public Widget asWidget(boolean isForTableView) {
        if(isForTableView){
            rootWrapper.addStyleName("preview-wrapper");
        }else {
            rootWrapper.removeStyleName("preview-wrapper");
        }

        return asWidget();
    }

    @Override
    public HasWidgets getContactsContainer() {
        return contactsContainer;
    }

    @Override
    public HasWidgets getSiteFolderContainer() {
        return siteFolderContainer;
    }

    @Override
    public HasVisibility getContactsContainerVisibility() {
        return contactsContainer;
    }

    @Override
    public HasVisibility getSiteFolderContainerVisibility() {
        return siteFolderContainer;
    }

    @Override
    public void setSubscriptionEmails(String value) {
        subscription.setText(value);
    }

    @UiField
    SpanElement phone;
    @UiField
    AnchorElement site;
    @UiField
    SpanElement email;
    @UiField
    InlineLabel companyLinksMessage;
    @UiField
    SpanElement addressDejure;
    @UiField
    SpanElement addressFact;
    @UiField
    Label info;
    @UiField
    HeadingElement companyName;
    @UiField
    HTMLPanel rootWrapper;
    @UiField
    HTMLPanel contactsContainer;
    @UiField
    InlineLabel subscription;
    @UiField
    ImageElement categoryImage;
    @UiField
    HTMLPanel siteFolderContainer;

    @Inject
    FixedPositioner positioner;

    AbstractCompanyPreviewActivity activity;

    interface CompanyPreviewViewUiBinder extends UiBinder<HTMLPanel, CompanyPreviewView > {}
    private static CompanyPreviewViewUiBinder ourUiBinder = GWT.create( CompanyPreviewViewUiBinder.class );
}