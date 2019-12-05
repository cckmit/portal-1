package ru.protei.portal.ui.issue.client.activity.edit;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.annotation.ContextAware;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.struct.CaseNameAndDescriptionChangeRequest;
import ru.protei.portal.core.model.struct.CaseObjectMetaJira;
import ru.protei.portal.core.model.util.CaseStateWorkflowUtil;
import ru.protei.portal.core.model.util.CaseTextMarkupUtil;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.util.TransliterationUtils;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.*;
import ru.protei.portal.ui.common.client.util.ClipboardUtils;
import ru.protei.portal.ui.common.client.widget.casemeta.model.CaseMeta;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.common.shared.model.*;
import ru.protei.portal.ui.issue.client.activity.meta.AbstractIssueMetaActivity;
import ru.protei.portal.ui.issue.client.activity.meta.AbstractIssueMetaView;

import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Активность создания и редактирования обращения
 */
public abstract class IssueEditActivity implements AbstractIssueEditActivity, AbstractIssueMetaActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
        view.setMetaActivity( this );
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
        if (!policyService.hasPrivilegeFor(En_Privilege.ISSUE_EDIT)) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }

        initDetails.parent.clear();
        requestIssue(event.id);
    }

    @Event
    public void onAddingAttachments( AttachmentEvents.Add event ) {
        if(view.isAttached() && issue.getId().equals(event.issueId)) {
            addAttachmentsToCase(event.attachments);
        }
    }

    @Event
    public void onChangeTimeElapsed( IssueEvents.ChangeTimeElapsed event ) {
        final AbstractIssueMetaView metaView = view.getMetaView();
        metaView.setTimeElapsed(event.timeElapsed);
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
    public void onRemoveTag(CaseTagEvents.Remove event) {
        issue.getTags().remove(event.getCaseTag());
        view.tags().setValue(issue.getTags());
    }

    @Override
    public void onCaseMetaChanged(CaseObjectMeta caseMeta) {

        if (!validateCaseMeta(caseMeta)) {
            return;
        }

        issueService.updateIssueMeta(caseMeta, new FluentCallback<CaseObjectMeta>()
                .withSuccess(caseMetaUpdated -> {
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    issue = caseMetaUpdated.collectToCaseObject(issue);
                    view.getMetaView().setCaseMeta(caseMetaUpdated);
                    showComments(issue);
                    onCompanyChanged();
                }));
    }

    @Override
    public void onCaseMetaNotifiersChanged(CaseObjectMetaNotifiers caseMetaNotifiers) {

        if (!validateCaseMetaNotifiers(caseMetaNotifiers)) {
            return;
        }

        issueService.updateIssueMetaNotifiers(caseMetaNotifiers, new FluentCallback<CaseObjectMetaNotifiers>()
                .withSuccess(caseMetaNotifiersUpdated -> {
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    issue = caseMetaNotifiersUpdated.collectToCaseObject(issue);
                    view.getMetaView().setCaseMetaNotifiers(caseMetaNotifiersUpdated);
                    showComments(issue);
                }));
    }

    @Override
    public void onCaseMetaJiraChanged(CaseObjectMetaJira caseMetaJira) {

        if (!validateCaseMetaJira(caseMetaJira)) {
            return;
        }

        issueService.updateIssueMetaJira(caseMetaJira, new FluentCallback<CaseObjectMetaJira>()
                .withSuccess(caseMetaJiraUpdated -> {
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    issue = caseMetaJiraUpdated.collectToCaseObject(issue);
                    view.getMetaView().setCaseMetaJira(caseMetaJiraUpdated);
                    showComments(issue);
                }));
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
        });
    }

    @Override
    public void onCompanyChanged() {

        final AbstractIssueMetaView metaView = view.getMetaView();

        Company company = metaView.getCaseMeta().getInitiatorCompany();

        metaView.initiatorEnabled().setEnabled(company != null);
        metaView.initiatorUpdateCompany(company);

        if (company == null) {
            setSubscriptionEmails(getSubscriptionsBasedOnPrivacy(null, lang.issueCompanySubscriptionNeedSelectCompany()));
            metaView.setInitiator(null);
        } else {
            Long selectedCompanyId = company.getId();

            metaView.setPlatform(null);
            metaView.platformEnabled().setEnabled(true);
            metaView.setPlatformFilter(platformOption -> selectedCompanyId.equals(platformOption.getCompanyId()));

            companyService.getCompanyWithParentCompanySubscriptions(
                    selectedCompanyId,
                    new ShortRequestCallback<List<CompanySubscription>>()
                            .setOnSuccess(subscriptions -> setSubscriptionEmails(getSubscriptionsBasedOnPrivacy(
                                    subscriptions,
                                    CollectionUtils.isEmpty(subscriptions) ?
                                            lang.issueCompanySubscriptionNotDefined() :
                                            lang.issueCompanySubscriptionBasedOnPrivacyNotDefined()
                                    )
                            ))
            );

            companyService.getCompanyCaseStates(
                    selectedCompanyId,
                    new ShortRequestCallback<List<CaseState>>()
                            .setOnSuccess(caseStates -> {
                                metaView.setStateFilter(caseStateFilter.makeFilter(caseStates));
                                fireEvent(new CaseStateEvents.UpdateSelectorOptions());
                            })
            );

            Person initiator = null;
            Profile profile = policyService.getProfile();
            if (issue.getInitiator() != null && Objects.equals(issue.getInitiator().getCompanyId(), selectedCompanyId)) {
                initiator = issue.getInitiator();
            } else if (profile.getCompany() != null && Objects.equals(profile.getCompany().getId(), selectedCompanyId)) {
                initiator = Person.fromPersonShortView(new PersonShortView(transliteration(profile.getFullName()), profile.getId(), profile.isFired()));
            }
            metaView.setInitiator(initiator);
        }

        fireEvent(new CaseStateEvents.UpdateSelectorOptions());
    }

    @Override
    public void onCreateContactClicked() {
        final AbstractIssueMetaView metaView = view.getMetaView();
        if (metaView.getCaseMeta().getInitiatorCompany() != null) {
            fillIssueObject(issue);
            fireEvent(new ContactEvents.Edit(null, metaView.getCaseMeta().getInitiatorCompany(), CrmConstants.Issue.CREATE_CONTACT_IDENTITY));
        }
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

    @Override
    public void onCaseMetaChanged( CaseMeta value ) {//TODO rework CaseMetaView handlers, separate links and tags

        caseLinkController.updateCaseLinks( issue.getId(), view.links().getValue(), new FluentCallback<List<CaseLink>>()
                .withError( t -> view.links().setValue( null ) )
                .withSuccess( caseLinks ->
                        view.links().setValue( caseLinks == null ? null : new HashSet<>( caseLinks ) )
                ) );

    }

    @Override
    public void onEditNameAndDescriptionClicked() {
        if (isEditingNameAndDescriptionView) {
            switchToRONameAndDescriptionView(issue);
            view.setNameAndDescriptionButtonsPanelVisibility(false);
        } else {
            boolean isAllowedEditNameAndDescription = isSelfIssue(issue);
            if (isAllowedEditNameAndDescription) {
                switchToEditingNameAndDescriptionView(issue);
                view.setNameAndDescriptionButtonsPanelVisibility(true);
            }
        }
    }

    @Override
    public void onSaveNameAndDescriptionClicked() {
        if (!view.nameValidator().isValid()) {
            fireEvent(new NotifyEvents.Show(lang.errEmptyName(), NotifyEvents.NotifyType.ERROR));
            return;
        }
        CaseNameAndDescriptionChangeRequest changeRequest = fillIssueNameAndDescription();
        issueService.saveIssueNameAndDescription(changeRequest, new FluentCallback<Void>()
                .withSuccess(result -> {
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    switchToRONameAndDescriptionView(issue);
                    view.setNameAndDescriptionButtonsPanelVisibility(false);
                }));
    }

    private CaseNameAndDescriptionChangeRequest fillIssueNameAndDescription() {
        if (isSelfIssue(issue)) {
            issue.setName(view.name().getValue());
            issue.setInfo(view.description().getValue());
        }
        return new CaseNameAndDescriptionChangeRequest(
                issue.getId(),
                issue.getName(),
                issue.getInfo());
    }

    private void requestCaseLinks( Long issueId ) {
        caseLinkController.getCaseLinks( issueId, new FluentCallback<List<CaseLink>>().withSuccess( caseLinks ->
                view.links().setValue( caseLinks == null ? null : new HashSet<>( caseLinks ) )
        ) );
    }

    private void requestIssue(Long number) {
        issueService.getIssue(number, new RequestCallback<CaseObject>() {
            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onSuccess(CaseObject issue) {
                IssueEditActivity.this.issue = issue;
                initDetails.parent.add(view.asWidget());
                fillView(issue);
                requestCaseLinks(issue.getId());
            }
        });
    }

    private void fillView(CaseObject issue) {

        view.attachmentsContainer().clear();

        view.setCaseNumber(issue.getCaseNumber());
        view.setPrivacyIcon(issue.isPrivateCase());
        view.attachmentsContainer().add(issue.getAttachments());
        view.setCreatedBy(lang.createBy(transliteration(issue.getCreator().getDisplayShortName()), DateFormatter.formatDateTime(issue.getCreated())));

        switchToRONameAndDescriptionView(issue);
        view.editNameAndDescriptionButtonVisibility().setVisible(isSelfIssue(issue));
        view.setNameAndDescriptionButtonsPanelVisibility(false);

        showComments(issue);

        view.setTagsAddButtonEnabled(policyService.hasGrantAccessFor( En_Privilege.ISSUE_VIEW ));
        view.setTagsEditButtonEnabled(policyService.hasGrantAccessFor( En_Privilege.ISSUE_VIEW ));

        view.tags().setValue(issue.getTags() == null ? new HashSet<>() : issue.getTags());

        view.setNumber(issue.getCaseNumber().intValue());

        fillMetaView(issue);
    }

    private void fillMetaView(CaseObject issue) {

        final AbstractIssueMetaView metaView = view.getMetaView();
        CaseObjectMeta caseMeta = new CaseObjectMeta(issue);
        CaseObjectMetaNotifiers caseMetaNotifiers = new CaseObjectMetaNotifiers(issue);
        CaseObjectMetaJira caseMetaJira = new CaseObjectMetaJira(issue);

        metaView.companyEnabled().setEnabled( isCompanyChangeAllowed(issue) );
        metaView.productEnabled().setEnabled( policyService.hasPrivilegeFor( En_Privilege.ISSUE_PRODUCT_EDIT ) );
        metaView.managerEnabled().setEnabled( policyService.hasPrivilegeFor( En_Privilege.ISSUE_MANAGER_EDIT) );

        metaView.timeElapsedHeader().removeClassName("hide");

        if (policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_MANAGER_VIEW)) { //TODO change rule
            metaView.caseSubscriptionContainer().setVisible(true);
        } else {
            caseMetaNotifiers.setNotifiers(null);
            metaView.caseSubscriptionContainer().setVisible(false);
        }

        caseMeta.setImportance(caseMeta.getImportance());
        caseMeta.setState(caseMeta.getState());
        metaView.setStateWorkflow(CaseStateWorkflowUtil.recognizeWorkflow(issue));
        metaView.stateEnabled().setEnabled(true);

        metaView.timeElapsedContainerVisibility().setVisible(false);

        Company company = issue.getInitiatorCompany();
        if (company == null) company = policyService.getUserCompany();
        caseMeta.setInitiatorCompany(company);

        metaView.platformVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_PLATFORM_EDIT));

        if (En_ExtAppType.JIRA.getCode().equals(issue.getExtAppType())) {
            metaView.jiraSlaSelectorVisibility().setVisible(true);
        } else {
            metaView.jiraSlaSelectorVisibility().setVisible(false);
            caseMetaJira = null;
        }

        metaView.setCaseMeta(caseMeta);
        metaView.setCaseMetaNotifiers(caseMetaNotifiers);
        metaView.setCaseMetaJira(caseMetaJira);

        onCompanyChanged();
    }

    private boolean makePreviewDisplaying( String key ) {
        return Boolean.parseBoolean( localStorageService.getOrDefault( ISSUE_EDIT + "_" + key, "false" ) );
    }

    private void fillIssueObject(CaseObject issue) {
        boolean isAllowedEditNameAndDescription = isSelfIssue(issue);
        if (isAllowedEditNameAndDescription) {
            issue.setName(view.name().getValue());
            issue.setInfo(view.description().getValue());
        }

        issue.setTags(view.tags().getValue() == null ? new HashSet<>() : view.tags().getValue());

        final AbstractIssueMetaView metaView = view.getMetaView();
        if (metaView.getCaseMeta() != null) metaView.getCaseMeta().collectToCaseObject(issue);
        if (metaView.getCaseMetaNotifiers() != null) metaView.getCaseMetaNotifiers().collectToCaseObject(issue);
        if (metaView.getCaseMetaJira() != null) metaView.getCaseMetaJira().collectToCaseObject(issue);
    }

    private void showComments(CaseObject issue) {
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

    private boolean validateCaseMeta(CaseObjectMeta caseMeta) {

        if (caseMeta.getInitiatorCompany() == null) {
            fireEvent(new NotifyEvents.Show(lang.errSaveIssueNeedSelectCompany(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if (caseMeta.getManager() == null && isStateWithRestrictions(caseMeta.getState())) {
            fireEvent(new NotifyEvents.Show(lang.errSaveIssueNeedSelectManager(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if (caseMeta.getProduct() == null && isStateWithRestrictions(caseMeta.getState())) {
            fireEvent(new NotifyEvents.Show(lang.errProductNotSelected(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        boolean isFieldsValid =
                view.getMetaView().stateValidator().isValid() &&
                view.getMetaView().importanceValidator().isValid() &&
                view.getMetaView().companyValidator().isValid();

        if (!isFieldsValid) {
            fireEvent(new NotifyEvents.Show(lang.errSaveIssueFieldsInvalid(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        return true;
    }

    private boolean validateCaseMetaNotifiers(CaseObjectMetaNotifiers caseMetaNotifiers) {
        return true;
    }

    private boolean validateCaseMetaJira(CaseObjectMetaJira caseMetaJira) {
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

    private boolean isSelfIssue(CaseObject issue) {
        return issue.getCreator() != null && Objects.equals(issue.getCreator().getId(), authProfile.getId());
    }

    private String getSubscriptionsBasedOnPrivacy(List<CompanySubscription> subscriptionsList, String emptyMessage) {
        this.subscriptionsList = subscriptionsList;
        this.subscriptionsListEmptyMessage = emptyMessage;

        if (CollectionUtils.isEmpty(subscriptionsList)) return subscriptionsListEmptyMessage;

        List<String> subscriptionsBasedOnPrivacyList = subscriptionsList.stream()
                .map(CompanySubscription::getEmail)
                .filter(mail -> !issue.isPrivateCase() || CompanySubscription.isProteiRecipient(mail)).collect( Collectors.toList());

        return CollectionUtils.isEmpty(subscriptionsBasedOnPrivacyList)
                ? subscriptionsListEmptyMessage
                : String.join(", ", subscriptionsBasedOnPrivacyList);
    }

    private boolean isCompanyChangeAllowed(CaseObject issue) {
        if (policyService.hasPrivilegeFor(En_Privilege.ISSUE_COMPANY_EDIT) &&
                (subscriptionsList == null || subscriptionsList.isEmpty() || issue.isPrivateCase())
        ) {
            return true;
        }

        return subscriptionsList == null || subscriptionsList.stream()
                .map(CompanySubscription::getEmail)
                .allMatch(CompanySubscription::isProteiRecipient);
    }

    private void setSubscriptionEmails(String value) {
        final AbstractIssueMetaView metaView = view.getMetaView();
        metaView.setSubscriptionEmails(value);
        metaView.companyEnabled().setEnabled(isCompanyChangeAllowed(issue));
    }

    private boolean isStateWithRestrictions(En_CaseState caseState) {
        return !En_CaseState.CREATED.equals(caseState) &&
                !En_CaseState.CANCELED.equals(caseState);
    }

    private String transliteration(String input) {
        return TransliterationUtils.transliterate(input, LocaleInfo.getCurrentLocale().getLocaleName());
    }

    private void switchToRONameAndDescriptionView(CaseObject issue) {
        isEditingNameAndDescriptionView = false;
        view.switchToRONameAndDescriptionView(true);
        view.name().setValue(null);
        view.description().setValue(null);
        view.setNameRO(issue.getName() == null ? "" : issue.getName(), En_ExtAppType.JIRA.getCode().equals(issue.getExtAppType()) ? issue.getJiraUrl() : "");
        renderMarkupText(issue.getInfo(), converted -> view.setDescriptionRO(converted));
    }

    private void switchToEditingNameAndDescriptionView(CaseObject issue) {
        isEditingNameAndDescriptionView = true;
        view.setDescriptionPreviewAllowed(makePreviewDisplaying(AbstractIssueEditView.DESCRIPTION));
        view.switchToRONameAndDescriptionView(false);
        view.name().setValue(issue.getName());
        view.description().setValue(issue.getInfo());
        view.setNameRO(null, "");
        view.setDescriptionRO(null);
    }

    @Inject
    AbstractIssueEditView view;
    @Inject
    IssueControllerAsync issueService;
    @Inject
    CaseLinkControllerAsync caseLinkController;
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

    @ContextAware
    CaseObject issue;

    private List<CompanySubscription> subscriptionsList;
    private String subscriptionsListEmptyMessage;
    private Profile authProfile;
    private boolean isEditingNameAndDescriptionView = false;

    private AppEvents.InitDetails initDetails;

    private static final Logger log = Logger.getLogger(IssueEditActivity.class.getName());
    private static final String ISSUE_EDIT = "issue_edit_is_preview_displayed";
}
