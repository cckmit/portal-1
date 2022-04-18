package ru.protei.portal.ui.company.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.tab.TabWidget;
import ru.protei.portal.ui.company.client.activity.preview.AbstractCompanyPreviewActivity;
import ru.protei.portal.ui.company.client.activity.preview.AbstractCompanyPreviewView;

import java.util.List;


/**
 * Представление превью компании
 */
public class CompanyPreviewView extends Composite implements AbstractCompanyPreviewView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        ensureDebugIds();
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
    public void setEmail( List<Widget> value ) {
        this.email.clear();
        value.forEach(email::add);
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
            rootWrapper.addStyleName("preview-card");
        }else {
            rootWrapper.removeStyleName("preview-card");
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
    public void setCommonManager(String value) {
        commonManager.getElement().setInnerHTML(value);
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
        subscription.setInnerHTML(value);
    }


    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        companyName.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.COMPANY.NAME);
        categoryImage.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.COMPANY.CATEGORY_IMAGE);
        site.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.COMPANY.WEB_SITE);
        info.ensureDebugId(DebugIds.COMPANY.COMMENT);
        companyLinksMessage.ensureDebugId(DebugIds.COMPANY.LINK_MESSAGE);
        subscription.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.COMPANY.SUBSCRIPTIONS);
        phone.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.COMPANY.PHONES);
        email.ensureDebugId(DebugIds.COMPANY.EMAILS);
        addressFact.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.COMPANY.ACTUAL_ADDRESS);
        addressDejure.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.COMPANY.LEGAL_ADDRESS);
        tabWidget.ensureDebugId(DebugIds.COMPANY.TABS);
        tabWidget.setTabNameDebugId(lang.contacts(), DebugIds.COMPANY.TAB_CONTACTS);
        tabWidget.setTabNameDebugId(lang.siteFolder(), DebugIds.COMPANY.TAB_SITE_FOLDERS);
        tabWidget.setTabNameDebugId(lang.productCommonManager(), DebugIds.COMPANY.TAB_COMMON_MANAGER);
        contactsContainer.ensureDebugId(DebugIds.COMPANY.CONTACTS);
        siteFolderContainer.ensureDebugId(DebugIds.COMPANY.SITE_FOLDERS);
        commonManager.ensureDebugId(DebugIds.COMPANY.COMMON_MANAGER);
    }

    @UiField
    SpanElement phone;
    @UiField
    AnchorElement site;
    @UiField
    HTMLPanel email;
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
    SpanElement subscription;
    @UiField
    ImageElement categoryImage;
    @UiField
    TabWidget tabWidget;
    @UiField
    HTMLPanel siteFolderContainer;
    @UiField
    HTMLPanel commonManager;
    @UiField
    SpanElement subscriptionLabel;
    @UiField
    HeadingElement contactsHeader;
    @UiField
    Lang lang;

    AbstractCompanyPreviewActivity activity;

    interface CompanyPreviewViewUiBinder extends UiBinder<HTMLPanel, CompanyPreviewView > {}
    private static CompanyPreviewViewUiBinder ourUiBinder = GWT.create( CompanyPreviewViewUiBinder.class );
}