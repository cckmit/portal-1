package ru.protei.portal.ui.company.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanySelector;
import ru.protei.portal.ui.common.client.widget.subscription.model.Subscription;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextArea;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.company.client.activity.edit.AbstractCompanyEditActivity;
import ru.protei.portal.ui.company.client.activity.edit.AbstractCompanyEditView;
import ru.protei.portal.ui.company.client.widget.category.buttonselector.CategoryButtonSelector;
import ru.protei.portal.ui.common.client.widget.subscription.list.SubscriptionList;

import java.util.List;

/**
 * Вид создания и редактирования компании
 */
public class CompanyEditView extends Composite implements AbstractCompanyEditView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        parentCompany.setDefaultValue(lang.selectIssueCompany());
        parentCompany.showOnlyParentCompanies(true);
    }

    @Override
    public void setActivity( AbstractCompanyEditActivity activity ) {
        this.activity = activity;
    }

    @Override
    public HasValue<String> companyName() {
        return companyName;
    }

    @Override
    public HasValidable companyNameValidator() {
        return companyName;
    }

    @Override
    public void setCompanyNameStatus(NameStatus status) {
        verifiableIcon.setClassName(status.getStyle());
    }

    @Override
    public HasValue<String> actualAddress() {
        return actualAddress;
    }

    @Override
    public HasValidable actualAddressValidator() {
        return actualAddress;
    }

    @Override
    public HasValue<String> legalAddress() {
        return legalAddress;
    }

    @Override
    public HasValidable legalAddressValidator() {
        return legalAddress;
    }

    @Override
    public HasText webSite() {
        return webSite;
    }

    @Override
    public HasText comment() {
        return comment;
    }

    @Override
    public HasValue<EntityOption> parentCompany() {
        return parentCompany;
    }

    @Override
    public HasValue<EntityOption> companyCategory() {
        return companyCategory;
    }

    @Override
    public HasValue<List<Subscription> > companySubscriptions() {
        return subscriptions;
    }

    @Override
    public HasValidable companySubscriptionsValidator() {
        return subscriptions;
    }

    @Override
    public HasWidgets phonesContainer() {
        return phonesContainer;
    }

    @Override
    public HasWidgets emailsContainer() {
        return emailsContainer;
    }

    @Override
    public HasWidgets tableContainer() {
        return tableContainer;
    }

    @Override
    public HasWidgets siteFolderContainer() {
        return siteFolderContainer;
    }

    @Override
    public HasVisibility tableContainerVisibility() {
        return tableContainerBlock;
    }

    @Override
    public HasVisibility siteFolderContainerVisibility() {
        return siteFolderContainerBlock;
    }

    @Override
    public void setParentCompanyFilter( Selector.SelectorFilter<EntityOption> companyFilter ) {
        parentCompany.setFilter( companyFilter );
    }

    @Override
    public void setParentCompanyEnabled( boolean isEnabled ) {
        parentCompany.setEnabled( isEnabled );
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

    @UiHandler("companyName")
    public void onChangeCompanyName( KeyUpEvent event ) {
        verifiableIcon.setClassName(NameStatus.UNDEFINED.getStyle());
        timer.cancel();
        timer.schedule( 300 );
    }

    @UiField
    Button saveButton;

    @UiField
    Button cancelButton;

    @UiField
    ValidableTextBox companyName;

    @UiField
    Element verifiableIcon;

    @UiField
    ValidableTextArea actualAddress;

    @UiField
    ValidableTextArea legalAddress;

    @UiField
    TextArea comment;

    @UiField
    TextBox webSite;
    
    @Inject
    @UiField( provided = true )
    CompanySelector parentCompany;

    @UiField
    HTMLPanel phonesContainer;

    @UiField
    HTMLPanel emailsContainer;

    @Inject
    @UiField ( provided = true )
    CategoryButtonSelector companyCategory;

    @UiField
    HTMLPanel tableContainerBlock;
    @UiField
    HTMLPanel tableContainer;

    @UiField
    HTMLPanel siteFolderContainerBlock;
    @UiField
    HTMLPanel siteFolderContainer;

    @Inject
    @UiField
    Lang lang;

    @Inject
    @UiField( provided = true )
    SubscriptionList subscriptions;


    Timer timer = new Timer() {
        @Override
        public void run() {
            if ( activity != null ) {
                activity.onChangeCompanyName();
            }
        }
    };

    AbstractCompanyEditActivity activity;

    private static CompanyViewUiBinder2 ourUiBinder = GWT.create(CompanyViewUiBinder2.class);
    interface CompanyViewUiBinder2 extends UiBinder<HTMLPanel, CompanyEditView> {}
}