package ru.protei.portal.ui.company.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.FieldSetElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
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
    public void setCategory ( String value ) { this.category.setInnerText( value ); }

    @Override
    public void setParentCompany( String value ) {
        this.parentCompany.setInnerText( value );
    }

    @Override
    public void setChildrenCompanies( String value ) {
        this.childrenCompanies.setInnerText( value );
    }

    @Override
    public void setInfo( String value ) {
        this.info.setInnerText( value );
    }

    @Override
    public void setGroupVisible( boolean value ) {
//        groupContainer.setVisible( value );
    }

    @Override
    public Widget asWidget(boolean isForTableView) {
        if(isForTableView){
            rootWrapper.addStyleName("preview-wrapper");
            contacts.setClassName("header");
            comments.setClassName("header");
        }else {
            rootWrapper.removeStyleName("preview-wrapper");
            contacts.setClassName("contacts");
            comments.setClassName("comments");
        }

        companyNameBlock.setVisible(isForTableView);
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
    public void setSubscriptionEmails(String value) {
        subscription.setInnerText(value);
    }

    @UiField
    SpanElement phone;
    @UiField
    AnchorElement site;
    @UiField
    SpanElement email;
    @UiField
    SpanElement category;
    @UiField
    SpanElement parentCompany;
    @UiField
    SpanElement childrenCompanies;
    @UiField
    SpanElement addressDejure;
    @UiField
    SpanElement addressFact;
    @UiField
    SpanElement info;
    @UiField
    HTMLPanel groupContainer;
    @UiField
    FieldSetElement contacts;
    @UiField
    HTMLPanel companyNameBlock;
    @UiField
    SpanElement companyName;
    @UiField
    HTMLPanel rootWrapper;
    @UiField
    FieldSetElement comments;
    @UiField
    HTMLPanel contactsContainer;
    @UiField
    HTMLPanel siteFolderContainer;
    @UiField
    SpanElement subscription;

    @Inject
    FixedPositioner positioner;

    AbstractCompanyPreviewActivity activity;

    interface CompanyPreviewViewUiBinder extends UiBinder<HTMLPanel, CompanyPreviewView > {}
    private static CompanyPreviewViewUiBinder ourUiBinder = GWT.create( CompanyPreviewViewUiBinder.class );
}