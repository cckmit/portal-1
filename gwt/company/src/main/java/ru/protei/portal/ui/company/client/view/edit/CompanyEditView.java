package ru.protei.portal.ui.company.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.common.NameStatus;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.casemeta.CaseMetaView;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanySelector;
import ru.protei.portal.ui.common.client.widget.subscription.list.SubscriptionList;
import ru.protei.portal.ui.common.client.widget.subscription.model.Subscription;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextArea;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.company.client.activity.edit.AbstractCompanyEditActivity;
import ru.protei.portal.ui.company.client.activity.edit.AbstractCompanyEditView;
import ru.protei.portal.ui.company.client.widget.category.buttonselector.CategoryButtonSelector;

import java.util.List;
import java.util.Set;

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
    public HasValue<Set<CaseTag>> tags() {
        return new HasValue<Set<CaseTag>>() {
            @Override public Set<CaseTag> getValue() { return caseMetaView.getTags(); }
            @Override public void setValue(Set<CaseTag> value) { caseMetaView.setTags(value); }
            @Override public void setValue(Set<CaseTag> value, boolean fireEvents) { caseMetaView.setTags(value); }
            @Override public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Set<CaseTag>> handler) { return null; }
            @Override public void fireEvent(GwtEvent<?> event) {}
        };
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
    public HasVisibility stateButtonVisibility() {
        return stateButton;
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
    public void hideTags( boolean isHide ) {
        if ( isHide ) {
            tagsPanel.addClassName( "hide" );
        } else {
            tagsPanel.removeClassName( "hide" );
        }
    }

    @Override
    public void setStateButtonText(String caption) {
        stateButton.setText(caption);
    }

    @UiHandler("stateButton")
    public void onStateClicked(ClickEvent event) {
        if (activity != null) {
            activity.onStateChanged();
            activity.onSaveClicked();
        }
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

    @UiHandler("addTagButton")
    public void addButtonClick(ClickEvent event) {
        if ( activity != null ) {
            activity.onAddTagClicked();
        }
    }

    @UiField
    Button saveButton;

    @UiField
    Button cancelButton;

    @UiField
    Button stateButton;

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

    @Inject
    @UiField(provided = true)
    CaseMetaView caseMetaView;
    @UiField
    DivElement tagsPanel;
    @UiField
    Button addTagButton;

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