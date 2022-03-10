package ru.protei.portal.ui.company.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.ent.CompanySubscription;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.autoresizetextarea.AutoResizeTextArea;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyModel;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanySelector;
import ru.protei.portal.ui.common.client.widget.tab.TabWidget;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.company.client.activity.edit.AbstractCompanyEditActivity;
import ru.protei.portal.ui.company.client.activity.edit.AbstractCompanyEditView;
import ru.protei.portal.ui.company.client.widget.category.buttonselector.CategoryButtonSelector;
import ru.protei.portal.ui.company.client.widget.companysubscription.list.CompanySubscriptionList;

import java.util.List;

/**
 * Вид создания и редактирования компании
 */
public class CompanyEditView extends Composite implements AbstractCompanyEditView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        parentCompany.setDefaultValue(lang.selectIssueCompany());
        companyModel.showOnlyParentCompanies(true);
        parentCompany.setAsyncModel(companyModel);
        companyName.setMaxLength(512);

        ensureDebugIds();
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
    public HasValue<String> legalAddress() {
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
    public HasText companyNameErrorLabel() {
        return companyNameErrorLabel;
    }

    @Override
    public HasVisibility companyNameErrorLabelVisibility() {
        return companyNameErrorLabel;
    }

    @Override
    public HasValue<EntityOption> parentCompany() {
        return parentCompany;
    }

    @Override
    public HasValue<En_CompanyCategory> companyCategory() {
        return companyCategory;
    }

    @Override
    public HasValue<List<CompanySubscription> > companySubscriptions() {
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
    public HasWidgets employeeRegistrationEmailsContainer() {
        return employeeRegistrationEmailsContainer;
    }

    @Override
    public HasVisibility employeeRegistrationEmailsContainerVisibility() {
        return employeeRegistrationEmailsGeneralContainer;
    }

    @Override
    public HasWidgets probationEmailsContainer() {
        return probationEmailsContainer;
    }

    @Override
    public HasVisibility probationEmailsContainerVisibility() {
        return probationEmailsGeneralContainer;
    }

    @Override
    public HasWidgets tableContainer() {
        return contactsContainer;
    }

    @Override
    public HasWidgets siteFolderContainer() {
        return siteFolderContainer;
    }

    @Override
    public void setParentCompanyFilter( Selector.SelectorFilter<EntityOption> companyFilter ) {
        parentCompany.setFilter( companyFilter );
    }

    @Override
    public void setParentCompanyEnabled( boolean isEnabled ) {
        parentCompany.setEnabled( isEnabled );
    }

    @Override
    public void setCompanyIdToSubscriptionsList(Long companyId) {
        subscriptions.setCompanyId(companyId);
    }

    @Override
    public HasValue<Boolean> autoOpenIssues() {
        return autoOpenIssues;
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

    @UiHandler("companyCategory")
    public void onCategoryChanged(ValueChangeEvent<En_CompanyCategory> event) {
        if (activity != null) {
            activity.onCategoryChanged();
        }
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }

        companyName.ensureDebugId(DebugIds.COMPANY.NAME);
        verifiableIcon.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.COMPANY.VERIFIABLE_ICON);
        companyCategory.setEnsureDebugId(DebugIds.COMPANY.CATEGORY);
        parentCompany.setEnsureDebugId(DebugIds.COMPANY.PARENT);
        comment.ensureDebugId(DebugIds.COMPANY.COMMENT);
        autoOpenIssues.ensureDebugId(DebugIds.COMPANY.AUTO_OPEN_ISSUES);
        subscriptions.ensureDebugId(DebugIds.COMPANY.SUBSCRIPTIONS);
        webSite.ensureDebugId(DebugIds.COMPANY.WEB_SITE);
        employeeRegistrationEmailsGeneralContainer.ensureDebugId(DebugIds.COMPANY.EMPLOYEE_REGISTRATION_GENERAL_EMAILS);
        employeeRegistrationEmailsContainer.ensureDebugId(DebugIds.COMPANY.EMPLOYEE_REGISTRATION_EMAILS);
        probationEmailsGeneralContainer.ensureDebugId(DebugIds.COMPANY.PROBATION_GENERAL_EMAILS);
        probationEmailsContainer.ensureDebugId(DebugIds.COMPANY.PROBATION_EMAILS);
        phonesContainer.ensureDebugId(DebugIds.COMPANY.PHONES);
        emailsContainer.ensureDebugId(DebugIds.COMPANY.EMAILS);
        actualAddress.ensureDebugId(DebugIds.COMPANY.ACTUAL_ADDRESS);
        legalAddress.ensureDebugId(DebugIds.COMPANY.LEGAL_ADDRESS);
        tabWidget.ensureDebugId(DebugIds.COMPANY.TABS);
        tabWidget.setTabNameDebugId(lang.contacts(), DebugIds.COMPANY.TAB_CONTACTS);
        tabWidget.setTabNameDebugId(lang.siteFolder(), DebugIds.COMPANY.TAB_SITE_FOLDERS);
        contactsContainer.ensureDebugId(DebugIds.COMPANY.CONTACTS);
        siteFolderContainer.ensureDebugId(DebugIds.COMPANY.SITE_FOLDERS);

        saveButton.ensureDebugId(DebugIds.COMPANY.SAVE_BUTTON);
        cancelButton.ensureDebugId(DebugIds.COMPANY.CANCEL_BUTTON);
    }

    @UiField
    Button saveButton;

    @UiField
    Button cancelButton;

    @UiField
    ValidableTextBox companyName;

    @UiField
    Label companyNameErrorLabel;

    @UiField
    Element verifiableIcon;

    @UiField
    AutoResizeTextArea actualAddress;

    @UiField
    AutoResizeTextArea legalAddress;

    @UiField
    TextArea comment;

    @UiField
    TextBox webSite;

    @Inject
    @UiField( provided = true )
    CompanySelector parentCompany;

    @UiField
    CheckBox autoOpenIssues;

    @UiField
    HTMLPanel employeeRegistrationEmailsGeneralContainer;

    @UiField
    HTMLPanel employeeRegistrationEmailsContainer;

    @UiField
    HTMLPanel probationEmailsGeneralContainer;

    @UiField
    HTMLPanel probationEmailsContainer;

    @UiField
    HTMLPanel phonesContainer;

    @UiField
    HTMLPanel emailsContainer;

    @Inject
    @UiField ( provided = true )
    CategoryButtonSelector companyCategory;

    @UiField
    TabWidget tabWidget;

    @UiField
    HTMLPanel contactsContainer;

    @UiField
    HTMLPanel siteFolderContainer;

    @Inject
    @UiField
    Lang lang;

    @Inject
    @UiField( provided = true )
    CompanySubscriptionList subscriptions;


    @Inject
    CompanyModel companyModel;
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
