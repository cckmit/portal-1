package ru.protei.portal.ui.project.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanySelector;
import ru.protei.portal.ui.common.client.widget.selector.customertype.CustomerTypeSelector;
import ru.protei.portal.ui.common.client.widget.selector.product.devunit.DevUnitMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.productdirection.ProductDirectionButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.region.RegionButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.state.RegionStateIconSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.project.client.activity.edit.AbstractProjectEditActivity;
import ru.protei.portal.ui.project.client.activity.edit.AbstractProjectEditView;
import ru.protei.portal.ui.project.client.view.widget.team.TeamSelector;

import java.util.Set;

/**
 * Вид создания и редактирования проекта
 */
public class ProjectEditView extends Composite implements AbstractProjectEditView{

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
        company.setDefaultValue(lang.selectIssueCompany());
        projectRegion.setDefaultValue(lang.selectOfficialRegion());
        projectDirection.setDefaultValue(lang.contractSelectDirection());
    }

    @Override
    protected void onAttach() {
        super.onAttach();
    }

    @Override
    public void setActivity(AbstractProjectEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<String> name() { return projectName; }

    @Override
    public HasText description() { return description; }

    @Override
    public HasValue<En_RegionState> state() { return projectState; }

    @Override
    public HasValue<ProductDirectionInfo> direction() { return projectDirection; }

    @Override
    public HasValue<Set<PersonProjectMemberView>> team() { return team; }

    @Override
    public HasValue< EntityOption > region() { return projectRegion; }

    @Override
    public HasValue<Set<ProductShortView>> products() { return products; }

    @Override
    public HasValue<EntityOption> company() { return company; }

    @Override
    public HasValue<En_CustomerType> customerType() { return customerType; }

    @Override
    public HasWidgets getCommentsContainer() {
        return commentsContainer;
    }

    @Override
    public HasWidgets getDocumentsContainer() { return documentsContainer; }

    @Override
    public HasValidable nameValidator() { return projectName; }

/*
    @Override
    public HasValidable companyValidator() { return company; }

    @Override
    public HasValidable regionValidator() { return projectRegion; }

    @Override
    public HasValidable directionValidator() { return projectDirection; }

    @Override
    public HasValidable customerTypeValidator() { return customerType; }
*/


    @Override
    public HasVisibility saveVisibility() { return saveButton; }

    @Override
    public HasEnabled saveEnabled() { return saveButton; }

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

    @Override
    public void showComments(boolean isShow) {
        if(isShow)
            comments.removeClassName(UiConstants.Styles.HIDE);
        else
            comments.addClassName(UiConstants.Styles.HIDE);
    }

    @Override
    public void showDocuments(boolean isShow) {
        if(isShow)
            comments.removeClassName(UiConstants.Styles.HIDE);
        else
            comments.addClassName(UiConstants.Styles.HIDE);
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
/*        local.ensureDebugId(DebugIds.ISSUE.PRIVACY_BUTTON);
        number.ensureDebugId(DebugIds.ISSUE.NUMBER_INPUT);
        name.ensureDebugId(DebugIds.ISSUE.NAME_INPUT);
        caseMetaView.setEnsureDebugId(DebugIds.ISSUE.LINKS_BUTTON);
        caseMetaView.setEnsureDebugIdContainer(DebugIds.ISSUE.LINKS_CONTAINER);
        caseMetaView.setEnsureDebugIdSelector(DebugIds.ISSUE.LINKS_TYPE_SELECTOR);
        caseMetaView.setEnsureDebugIdTextBox(DebugIds.ISSUE.LINKS_INPUT);
        caseMetaView.setEnsureDebugIdApply(DebugIds.ISSUE.LINKS_APPLY_BUTTON);
        state.setEnsureDebugId(DebugIds.ISSUE.STATE_SELECTOR);
        importance.setEnsureDebugId(DebugIds.ISSUE.IMPORTANCE_SELECTOR);
        company.setEnsureDebugId(DebugIds.ISSUE.COMPANY_SELECTOR);
        initiator.setEnsureDebugId(DebugIds.ISSUE.INITIATOR_SELECTOR);
        product.setEnsureDebugId(DebugIds.ISSUE.PRODUCT_SELECTOR);
        manager.setEnsureDebugId(DebugIds.ISSUE.MANAGER_SELECTOR);
        timeElapsed.ensureDebugId(DebugIds.ISSUE.TIME_ELAPSED_LABEL);
        timeElapsedInput.ensureDebugId(DebugIds.ISSUE.TIME_ELAPSED_INPUT);
        description.ensureDebugId(DebugIds.ISSUE.DESCRIPTION_INPUT);
        notifiers.setAddEnsureDebugId(DebugIds.ISSUE.NOTIFIERS_SELECTOR_ADD_BUTTON);
        notifiers.setClearEnsureDebugId(DebugIds.ISSUE.NOTIFIERS_SELECTOR_CLEAR_BUTTON);
        fileUploader.setEnsureDebugId(DebugIds.ISSUE.ATTACHMENT_UPLOAD_BUTTON);
        attachmentContainer.setEnsureDebugId(DebugIds.ISSUE.ATTACHMENT_LIST_CONTAINER);
        saveButton.ensureDebugId(DebugIds.ISSUE.SAVE_BUTTON);
        cancelButton.ensureDebugId(DebugIds.ISSUE.CANCEL_BUTTON);

        nameLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.NAME);
        caseMetaView.setEnsureDebugIdLabel(DebugIds.ISSUE.LABEL.LINKS);
        stateLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.STATE);
        importanceLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.IMPORTANCE);
        companyLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.COMPANY);
        initiatorLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.CONTACT);
        productLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.PRODUCT);
        managerLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.MANAGER);
        timeElapsedLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.TIME_ELAPSED);
        descriptionLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.INFO);
        subscriptionsLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.SUBSCRIPTIONS);
        notifiersLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.NOTIFIERS);
        attachmentsLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.LABEL.ATTACHMENTS);*/
    }

    @UiField
    HTMLPanel root;

    @UiField
    ValidableTextBox projectName;

    @UiField
    TextArea description;

    @Inject
    @UiField(provided = true)
    TeamSelector team;

    @Inject
    @UiField( provided = true )
    ProductDirectionButtonSelector projectDirection;

    @Inject
    @UiField( provided = true )
    RegionStateIconSelector projectState;

    @Inject
    @UiField( provided = true )
    RegionButtonSelector projectRegion;

    @Inject
    @UiField(provided = true)
    CompanySelector company;

    @Inject
    @UiField(provided = true)
    DevUnitMultiSelector products;

    @Inject
    @UiField(provided = true)
    CustomerTypeSelector customerType;

    @UiField
    DivElement comments;
    @UiField
    HTMLPanel commentsContainer;
    @UiField
    DivElement documents;
    @UiField
    HTMLPanel documentsContainer;

    @UiField
    Button saveButton;
    @UiField
    Button cancelButton;
    @Inject
    @UiField
    Lang lang;

    private AbstractProjectEditActivity activity;

    interface ProjectEditViewUiBinder extends UiBinder<HTMLPanel, ProjectEditView> {}
    private static ProjectEditViewUiBinder ourUiBinder = GWT.create(ProjectEditViewUiBinder.class);
}