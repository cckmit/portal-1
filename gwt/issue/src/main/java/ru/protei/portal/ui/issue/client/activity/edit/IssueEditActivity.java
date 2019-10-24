package ru.protei.portal.ui.issue.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.annotation.ContextAware;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.struct.CaseObjectWithCaseComment;
import ru.protei.portal.core.model.util.CaseStateWorkflowUtil;
import ru.protei.portal.core.model.util.CaseTextMarkupUtil;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.PlatformOption;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.*;
import ru.protei.portal.ui.common.client.util.ClipboardUtils;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.common.shared.model.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Активность создания и редактирования обращения
 */
public abstract class IssueEditActivity implements AbstractIssueEditActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
        view.setFileUploadHandler(new AttachmentUploader.FileUploadHandler() {
            @Override
            public void onSuccess(Attachment attachment) {
                addAttachmentsToCase(Collections.singleton(attachment));
            }
            @Override
            public void onError(En_FileUploadStatus status, String details) {
                if (En_FileUploadStatus.SIZE_EXCEED_ERROR.equals(status)) {
                    fireEvent(new NotifyEvents.Show(lang.uploadFileSizeExceed() + " (" + details + "Mb)", NotifyEvents.NotifyType.ERROR));
                }
                else {
                    fireEvent(new NotifyEvents.Show(lang.uploadFileError(), NotifyEvents.NotifyType.ERROR));
                }
            }
        });
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        this.authProfile = event.profile;
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow( IssueEvents.Edit event ) {
        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());

        if (event.id == null) {
            if (issue != null) {
                initialRestoredView(issue);
            } else {
                CaseObject caseObject = new CaseObject();
                initNewIssue(caseObject);
                initialView(caseObject);
            }
        } else {
            requestIssue(event.id, this::initialView);
        }
    }

    @Event
    public void onAddingAttachments( AttachmentEvents.Add event ) {
        if(view.isAttached() && issue.getId().equals(event.issueId)) {
            addAttachmentsToCase(event.attachments);
        }
    }

    @Event
    public void onChangeTimeElapsed( IssueEvents.ChangeTimeElapsed event ) {
        view.timeElapsedLabel().setTime(event.timeElapsed);
        view.timeElapsedInput().setTime(event.timeElapsed);
    }

    @Event
    public void onRemovingAttachments( AttachmentEvents.Remove event ) {
        if(view.isAttached() && issue.getId().equals(event.issueId)) {
            event.attachments.forEach(view.attachmentsContainer()::remove);
            issue.getAttachments().removeAll(event.attachments);
            issue.setAttachmentExists(!issue.getAttachments().isEmpty());
        }
    }

    @Event
    public void onFillPerson(PersonEvents.PersonCreated event) {
        if (CrmConstants.Issue.CREATE_CONTACT_IDENTITY.equals(event.origin) && issue != null && event.person != null) {
            issue.setInitiator(event.person);
            issue.setInitiatorId(event.person.getId());
            if (issue.getInitiator() != null) {
                view.initiator().setValue(issue.getInitiator().toFullNameShortView());
            }
        }
    }

    @Event
    public void onRemoveTag(CaseTagEvents.Remove event) {
        issue.getTags().remove(event.getCaseTag());
        view.tags().setValue(issue.getTags());
    }

    @Override
    public void onSaveClicked() {

        if (!validateView()) {
            return;
        }

        fillIssueObject(issue);

        if (isLockedSave()) {
            return;
        }
        lockSave();
        fireEvent(new CaseCommentEvents.OnSavingEvent());

        fireEvent(new CaseCommentEvents.ValidateComment(isNew(issue), isValid -> {
            if (!isValid) {
                unlockSave();
                fireEvent(new CaseCommentEvents.OnDoneEvent());
                fireEvent(new NotifyEvents.Show(lang.commentEmpty(), NotifyEvents.NotifyType.ERROR));
                return;
            }
            fireEvent(new CaseCommentEvents.GetCurrentComment(comment -> issueService.saveIssueAndComment(issue, comment, new FluentCallback<CaseObjectWithCaseComment>()
                    .withResult(this::unlockSave)
                    .withError(throwable -> {
                        fireEvent(new CaseCommentEvents.OnDoneEvent());
                        defaultErrorHandler.accept(throwable);
                    })
                    .withSuccess(caseObjectWithCaseComment -> {
                        fireEvent(new CaseCommentEvents.OnDoneEvent(caseObjectWithCaseComment.getCaseComment()));
                        fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                        fireEvent(new IssueEvents.ChangeModel());
                        fireEvent(isNew(issue) ? new IssueEvents.Show(true) : new Back());
                    }))));
        }));
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    @Override
    public void removeAttachment(Attachment attachment) {
        attachmentService.removeAttachmentEverywhere(En_CaseType.CRM_SUPPORT, attachment.getId(), new RequestCallback<Boolean>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.removeFileError(), NotifyEvents.NotifyType.ERROR));
            }
            @Override
            public void onSuccess(Boolean result) {
                if(!result){
                    onError(null);
                    return;
                }

                view.attachmentsContainer().remove(attachment);
                issue.getAttachments().remove(attachment);
                issue.setAttachmentExists(!issue.getAttachments().isEmpty());
                if (!isNew(issue)) {
                    fireEvent(new CaseCommentEvents.Show.Builder(view.getCommentsContainer())
                            .withCaseType(En_CaseType.CRM_SUPPORT)
                            .withCaseId(issue.getId())
                            .withModifyEnabled(policyService.hasEveryPrivilegeOf(En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT))
                            .withElapsedTimeEnabled(policyService.hasPrivilegeFor(En_Privilege.ISSUE_WORK_TIME_VIEW))
                            .withPrivateVisible(!issue.isPrivateCase() && policyService.hasPrivilegeFor(En_Privilege.ISSUE_PRIVACY_VIEW))
                            .withPrivateCase(issue.isPrivateCase())
                            .withTextMarkup(CaseTextMarkupUtil.recognizeTextMarkup(issue))
                            .build());
                }
            }
        });
    }

    @Override
    public void onCompanyChanged() {
        Company companyOption = Company.fromEntityOption(view.company().getValue());

        view.initiatorState().setEnabled(companyOption != null);
        view.initiatorUpdateCompany(companyOption);

        if ( companyOption == null ) {
            setSubscriptionEmails(getSubscriptionsBasedOnPrivacy(null, lang.issueCompanySubscriptionNeedSelectCompany()));
            view.initiator().setValue(null);
        } else {
            Long selectedCompanyId = companyOption.getId();

            view.setPlatformFilter(platformOption -> selectedCompanyId.equals(platformOption.getCompanyId()));

            companyService.getCompanyWithParentCompanySubscriptions(selectedCompanyId, new ShortRequestCallback<List<CompanySubscription>>()
                    .setOnSuccess(subscriptions -> setSubscriptionEmails(getSubscriptionsBasedOnPrivacy(subscriptions,
                            CollectionUtils.isEmpty(subscriptions) ? lang.issueCompanySubscriptionNotDefined() : lang.issueCompanySubscriptionBasedOnPrivacyNotDefined()))));

            companyService.getCompanyCaseStates(selectedCompanyId, new ShortRequestCallback<List<CaseState>>()
                    .setOnSuccess(caseStates -> {
                        view.setStateFilter(caseStateFilter.makeFilter(caseStates));
                        fireEvent(new CaseStateEvents.UpdateSelectorOptions());
                    }));

            Profile profile = policyService.getProfile();
            PersonShortView initiator = null;
            if ( issue.getInitiator() != null && Objects.equals(issue.getInitiator().getCompanyId(), selectedCompanyId)) {
                initiator = PersonShortView.fromPerson(issue.getInitiator());
            } else if ( profile.getCompany() != null && Objects.equals(profile.getCompany().getId(), selectedCompanyId)) {
                initiator = new PersonShortView(profile.getShortName(), profile.getId(), profile.isFired());
            }
            view.initiator().setValue(initiator);
        }

        fireEvent(new CaseStateEvents.UpdateSelectorOptions());
    }

    @Override
    public void onCreateContactClicked() {
        if (view.company().getValue() != null) {
            fillIssueObject(issue);
            fireEvent(new ContactEvents.Edit(null, Company.fromEntityOption(view.company().getValue()), CrmConstants.Issue.CREATE_CONTACT_IDENTITY));
        }
    }

    @Override
    public void onLocalClicked() {
        setSubscriptionEmails(getSubscriptionsBasedOnPrivacy(subscriptionsList, subscriptionsListEmptyMessage));
    }

    @Override
    public void renderMarkupText(String text, Consumer<String> consumer) {
        En_TextMarkup textMarkup = CaseTextMarkupUtil.recognizeTextMarkup(issue);
        textRenderController.render(text, textMarkup, new FluentCallback<String>()
                .withError(throwable -> consumer.accept(null))
                .withSuccess(consumer));
    }

    @Override
    public void onDisplayPreviewChanged( String key, boolean isDisplay ) {
        localStorageService.set( ISSUE_EDIT + "_" + key, String.valueOf( isDisplay ) );
    }

    @Override
    public void onCopyClicked() {
        int status = ClipboardUtils.copyToClipboard(lang.crmPrefix() + issue.getCaseNumber() + " " + view.name().getValue());

        if (status != 0) {
            fireEvent(new NotifyEvents.Show(lang.errCopyToClipboard(), NotifyEvents.NotifyType.ERROR));
        } else {
            fireEvent(new NotifyEvents.Show(lang.issueCopiedToClipboard(), NotifyEvents.NotifyType.SUCCESS));
        }
    }

    private void initialView(CaseObject issue){
        this.issue = issue;
        fillView(this.issue, false);
    }

    private void requestCaseLinks( Long issueId ) {
        issueService.getCaseLinks( issueId, new FluentCallback<List<CaseLink>>().withSuccess( caseLinks ->
                view.links().setValue( caseLinks == null ? null : new HashSet<>( caseLinks ) )
        ) );
    }

    private void initialRestoredView(CaseObject issue){
        this.issue = issue;
        fillView(this.issue, true);
    }

    private void requestIssue(Long number, Consumer<CaseObject> successAction){
        issueService.getIssue(number, new RequestCallback<CaseObject>() {
            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onSuccess(CaseObject issue) {
                successAction.accept(issue);
                requestCaseLinks(issue.getId());
            }
        });
    }

    private void initNewIssue(CaseObject caseObject) {
        boolean isPrivacyVisible = policyService.hasPrivilegeFor(En_Privilege.ISSUE_PRIVACY_VIEW);
        caseObject.setPrivateCase(isPrivacyVisible ? true : false);
    }

    private void fillView(CaseObject issue, boolean isRestoredIssue) {
        view.companyEnabled().setEnabled( isCompanyChangeAllowed(issue) );
        view.productEnabled().setEnabled( policyService.hasPrivilegeFor( En_Privilege.ISSUE_PRODUCT_EDIT ) );
        view.managerEnabled().setEnabled( policyService.hasPrivilegeFor( En_Privilege.ISSUE_MANAGER_EDIT) );

        view.attachmentsContainer().clear();

        if (isNew(issue)) {
            view.setCaseNumber(null);
            view.numberContainerVisibility().setVisible(false);
            view.showComments(false);
            view.getCommentsContainer().clear();
            view.privacyVisibility().setVisible( policyService.hasPrivilegeFor(En_Privilege.ISSUE_PRIVACY_VIEW));
            view.timeElapsedHeader().addClassName("hide");
        } else {
            view.timeElapsedHeader().removeClassName("hide");
            view.setCaseNumber(issue.getCaseNumber());
            view.privacyVisibility().setVisible(false);
            view.setPrivacyIcon(issue.isPrivateCase());
            view.numberContainerVisibility().setVisible(true);
            view.showComments(true);
            view.attachmentsContainer().add(issue.getAttachments());
            view.setCreatedBy(lang.createBy(issue.getCreator().getDisplayShortName(), DateFormatter.formatDateTime(issue.getCreated())));
            fireEvent(new CaseCommentEvents.Show.Builder(view.getCommentsContainer())
                    .withCaseType(En_CaseType.CRM_SUPPORT)
                    .withCaseId(issue.getId())
                    .withModifyEnabled(policyService.hasEveryPrivilegeOf(En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT))
                    .withElapsedTimeEnabled(policyService.hasPrivilegeFor(En_Privilege.ISSUE_WORK_TIME_VIEW))
                    .withPrivateVisible(!issue.isPrivateCase() && policyService.hasPrivilegeFor(En_Privilege.ISSUE_PRIVACY_VIEW))
                    .withPrivateCase(issue.isPrivateCase())
                    .withTextMarkup(CaseTextMarkupUtil.recognizeTextMarkup(issue))
                    .build());
        }

        if(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_MANAGER_VIEW)) { //TODO change rule
            view.notifiers().setValue(issue.getNotifiers() == null ? new HashSet<>() :
                    issue.getNotifiers().stream().map(PersonShortView::fromPerson).collect(Collectors.toSet()));
            view.caseSubscriptionContainer().setVisible(true);
        } else {
            view.caseSubscriptionContainer().setVisible(false);
        }

        view.links().setValue(CollectionUtils.toSet(issue.getLinks(), caseLink -> caseLink));

        view.setTagsAddButtonEnabled(policyService.hasGrantAccessFor( En_Privilege.ISSUE_VIEW ));
        view.setTagsEditButtonEnabled(policyService.hasGrantAccessFor( En_Privilege.ISSUE_VIEW ));

        view.tags().setValue(issue.getTags() == null ? new HashSet<>() : issue.getTags());

        view.numberVisibility().setVisible( !isNew(issue) );
        view.setNumber(isNew(issue) ? null : issue.getCaseNumber().intValue() );

        view.isPrivate().setValue(issue.isPrivateCase());

        view.name().setValue(issue.getName());
        view.description().setValue(issue.getInfo());

        boolean isAllowedEditNameAndDescription = isNew(issue) || isSelfIssue(issue);
        if ( isAllowedEditNameAndDescription ) {
            view.setDescriptionPreviewAllowed(makePreviewDisplaying( AbstractIssueEditView.DESCRIPTION ));
            view.switchToRONameDescriptionView(false);
        } else {
            view.switchToRONameDescriptionView(true);
            renderMarkupText(issue.getInfo(), converted -> view.setDescriptionRO(converted));
            view.setNameRO(issue.getName());
        }

        view.setStateWorkflow(CaseStateWorkflowUtil.recognizeWorkflow(issue));
        view.state().setValue(isNew(issue) && !isRestoredIssue ? En_CaseState.CREATED : En_CaseState.getById(issue.getStateId()));
        view.stateEnabled().setEnabled(!isNew(issue) || policyService.personBelongsToHomeCompany());
        view.importance().setValue(isNew(issue) && !isRestoredIssue ? En_ImportanceLevel.BASIC : En_ImportanceLevel.getById(issue.getImpLevel()));

        boolean hasPrivilegeForTimeElapsed = policyService.hasPrivilegeFor(En_Privilege.ISSUE_WORK_TIME_VIEW);
        view.timeElapsedContainerVisibility().setVisible(hasPrivilegeForTimeElapsed);
        if (hasPrivilegeForTimeElapsed) {
            if (isNew(issue)) {
                boolean timeElapsedEditAllowed = policyService.personBelongsToHomeCompany();
                view.timeElapsedLabel().setTime(null);
                if ( !isRestoredIssue ) {
                    view.timeElapsedInput().setTime(0L);
                }
                view.timeElapsedLabelVisibility().setVisible(!timeElapsedEditAllowed);
                view.timeElapsedEditContainerVisibility().setVisible(timeElapsedEditAllowed);
                view.timeElapsedType().setValue( En_TimeElapsedType.NONE );
            } else {
                Long timeElapsed = issue.getTimeElapsed();
                view.timeElapsedLabel().setTime(Objects.equals(0L, timeElapsed) ? null : timeElapsed);
                view.timeElapsedInput().setTime(timeElapsed);
                view.timeElapsedLabelVisibility().setVisible(true);
                view.timeElapsedEditContainerVisibility().setVisible(false);
            }
        }

        if (isNew(issue) && !isRestoredIssue) {
            view.applyCompanyValueIfOneOption();
        } else {
            Company initiatorCompany = issue.getInitiatorCompany();
            if ( initiatorCompany == null ) {
                initiatorCompany = policyService.getUserCompany();
            }
            view.company().setValue(EntityOption.fromCompany(initiatorCompany), true);
        }

        view.product().setValue( ProductShortView.fromProduct( issue.getProduct() ) );
        view.manager().setValue( PersonShortView.fromPerson( issue.getManager() ) );
        view.saveVisibility().setVisible( policyService.hasPrivilegeFor( En_Privilege.ISSUE_EDIT ) );
        view.initiatorSelectorAllowAddNew( policyService.hasPrivilegeFor( En_Privilege.CONTACT_CREATE ) );
        view.platform().setValue(issue.getPlatformId() == null ? null : new PlatformOption(issue.getPlatformName(), issue.getPlatformId()));
        view.platformVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_PLATFORM_EDIT));
        view.copyVisibility().setVisible(!isNew(issue));

        fillViewForJira(issue);

        unlockSave();
    }

    private void fillViewForJira(CaseObject issue) {

        view.jiraSlaSelectorVisibility().setVisible(false);

        if (!En_ExtAppType.JIRA.getCode().equals(issue.getExtAppType())) {
            return;
        }

        view.jiraSlaSelectorVisibility().setVisible(true);
        view.jiraSlaSelector().setValue(issue.getJiraMetaData());
    }

    private boolean makePreviewDisplaying( String key ) {
        return Boolean.parseBoolean( localStorageService.getOrDefault( ISSUE_EDIT + "_" + key, "false" ) );
    }

    private void fillIssueObject(CaseObject issue){
        issue.setName(view.name().getValue());
        issue.setPrivateCase( view.isPrivate().getValue() );
        issue.setInfo(view.description().getValue());

        issue.setStateId(view.state().getValue().getId());
        issue.setImpLevel(view.importance().getValue().getId());

        issue.setInitiatorCompany(Company.fromEntityOption(view.company().getValue()));
        issue.setInitiator(Person.fromPersonShortView(view.initiator().getValue()));
        issue.setProduct( DevUnit.fromProductShortView( view.product().getValue() ) );
        issue.setManager( Person.fromPersonShortView( view.manager().getValue() ) );
        issue.setNotifiers(view.notifiers().getValue().stream().map(Person::fromPersonShortView).collect(Collectors.toSet()));
        issue.setLinks(view.links().getValue() == null ? new ArrayList<>() : new ArrayList<>(view.links().getValue()));
        issue.setTags(view.tags().getValue() == null ? new HashSet<>() : view.tags().getValue());
        issue.setPlatformId(view.platform().getValue() == null ? null : view.platform().getValue().getId());

        if (isNew(issue) && policyService.hasPrivilegeFor(En_Privilege.ISSUE_WORK_TIME_VIEW) && policyService.personBelongsToHomeCompany()) {
            issue.setTimeElapsed(view.timeElapsedInput().getTime());
            En_TimeElapsedType elapsedType = view.timeElapsedType().getValue();
            issue.setTimeElapsedType( elapsedType != null ? elapsedType : En_TimeElapsedType.NONE );
        }

        fillIssueObjectWithJira(issue);
    }

    private void fillIssueObjectWithJira(CaseObject issue) {

        if (!En_ExtAppType.JIRA.getCode().equals(issue.getExtAppType())) {
            return;
        }

        issue.setJiraMetaData(view.jiraSlaSelector().getValue());
    }

    private boolean validateView() {
        if(view.company().getValue() == null){
            fireEvent(new NotifyEvents.Show(lang.errSaveIssueNeedSelectCompany(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if (isStateWithRestrictions(view.state().getValue())) {
            if (view.manager().getValue() == null) {
                fireEvent(new NotifyEvents.Show(lang.errSaveIssueNeedSelectManager(), NotifyEvents.NotifyType.ERROR));
                return false;
            }
            if (view.product().getValue() == null) {
                fireEvent(new NotifyEvents.Show(lang.errProductNotSelected(), NotifyEvents.NotifyType.ERROR));
                return false;
            }
        }

        boolean isFieldsValid = view.nameValidator().isValid() &&
                view.stateValidator().isValid() &&
                view.importanceValidator().isValid() &&
                view.companyValidator().isValid();

        if(!isFieldsValid) {
            fireEvent(new NotifyEvents.Show(lang.errSaveIssueFieldsInvalid(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        return true;
    }

    private void addAttachmentsToCase(Collection<Attachment> attachments){
        if (issue.getAttachments() == null || issue.getAttachments().isEmpty())
            issue.setAttachments(new ArrayList<>());

        if (attachments != null && !attachments.isEmpty()) {
            view.attachmentsContainer().add(attachments);
            issue.getAttachments().addAll(attachments);
            issue.setAttachmentExists(true);
        }
    }

    private boolean isNew(CaseObject issue) {
        return issue.getId() == null;
    }

    private boolean isSelfIssue(CaseObject issue) {
        return issue.getCreator() != null && Objects.equals(issue.getCreator().getId(), authProfile.getId());
    }

    private String getSubscriptionsBasedOnPrivacy(List<CompanySubscription> subscriptionsList, String emptyMessage) {
        this.subscriptionsList = subscriptionsList;
        this.subscriptionsListEmptyMessage = emptyMessage;

        if (CollectionUtils.isEmpty(subscriptionsList)) return subscriptionsListEmptyMessage;

        List<String> subscriptionsBasedOnPrivacyList = subscriptionsList.stream()
                .map(CompanySubscription::getEmail)
                .filter(mail -> !view.isPrivate().getValue() || CompanySubscription.isProteiRecipient(mail)).collect( Collectors.toList());

        return CollectionUtils.isEmpty(subscriptionsBasedOnPrivacyList)
                ? subscriptionsListEmptyMessage
                : subscriptionsBasedOnPrivacyList.stream().collect(Collectors.joining(", "));
    }

    private boolean isCompanyChangeAllowed(CaseObject issue) {
        if (policyService.hasPrivilegeFor(En_Privilege.ISSUE_COMPANY_EDIT) &&
                (issue.getId() == null || subscriptionsList == null || subscriptionsList.isEmpty() || view.isPrivate().getValue())
        ) {
            return true;
        }

        if (isNew( issue )) return true;

        return subscriptionsList == null || subscriptionsList.stream()
                .map(CompanySubscription::getEmail)
                .allMatch(CompanySubscription::isProteiRecipient);
    }

    private void setSubscriptionEmails(String value) {
        view.setSubscriptionEmails(value);
        view.companyEnabled().setEnabled(isCompanyChangeAllowed(issue));
    }

    private boolean isStateWithRestrictions(En_CaseState caseState) {
        return !En_CaseState.CREATED.equals(caseState) &&
                !En_CaseState.CANCELED.equals(caseState);
    }

    private void lockSave() {
        saving = true;
        view.saveEnabled().setEnabled(false);
    }

    private void unlockSave() {
        saving = false;
        view.saveEnabled().setEnabled(true);
    }

    private boolean isLockedSave() {
        return saving;
    }

    @Inject
    AbstractIssueEditView view;
    @Inject
    IssueControllerAsync issueService;
    @Inject
    AttachmentServiceAsync attachmentService;
    @Inject
    Lang lang;
    @Inject
    PolicyService policyService;
    @Inject
    CompanyControllerAsync companyService;
    @Inject
    CaseStateFilterProvider caseStateFilter;
    @Inject
    TextRenderControllerAsync textRenderController;
    @Inject
    LocalStorageService localStorageService;
    @Inject
    DefaultErrorHandler defaultErrorHandler;

    @ContextAware
    CaseObject issue;

    private boolean saving = false;
    private List<CompanySubscription> subscriptionsList;
    private String subscriptionsListEmptyMessage;
    private Profile authProfile;

    private AppEvents.InitDetails initDetails;

    private static final Logger log = Logger.getLogger(IssueEditActivity.class.getName());
    private static final String ISSUE_EDIT = "issue_edit_is_preview_displayed";
}
