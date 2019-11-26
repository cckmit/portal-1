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
        if (event.id == null) {
            if (issue != null) {
                initDetails.parent.add(view.asWidget());
                fillView(issue, true);
            } else {
                initDetails.parent.add(view.asWidget());
                issue = createNewIssue();
                fillView(issue, false);
            }
        } else {
            requestIssue(event.id, false);
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
    public void onFillPerson(PersonEvents.PersonCreated event) {
        if (CrmConstants.Issue.CREATE_CONTACT_IDENTITY.equals(event.origin) && event.person != null) {
            final AbstractIssueMetaView metaView = view.getMetaView();
            metaView.setInitiator(event.person);
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
    public void onSaveClicked() {

        if (!validateView(issue)) {
            return;
        }

        fillIssueObject(issue);

        if (isLockedSave()) {
            return;
        }
        lockSave();
        issueService.saveIssue( issue, new FluentCallback<Long>()
                .withError(throwable -> {
                    unlockSave();
                    defaultErrorHandler.accept(throwable);
                })
                .withSuccess(caseId -> {
                    unlockSave();
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(isNew(issue) ? new IssueEvents.Show(true) : new Back());
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

        final AbstractIssueMetaView metaView = view.getMetaView();

        Company company = metaView.getCaseMeta().getInitiatorCompany();

        metaView.initiatorEnabled().setEnabled(company != null);
        metaView.initiatorUpdateCompany(company);

        if (company == null) {
            setSubscriptionEmails(getSubscriptionsBasedOnPrivacy(null, lang.issueCompanySubscriptionNeedSelectCompany()));
            metaView.setInitiator(null);
        } else {
            initiatorSelectorAllowAddNew(company.getId());
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

    @Override
    public void onCaseMetaChanged( CaseMeta value ) {//TODO rework CaseMetaView handlers, separate links and tags

        caseLinkController.updateCaseLinks( issue.getId(), view.links().getValue(), new FluentCallback<List<CaseLink>>()
                .withError( t -> view.links().setValue( null ) )
                .withSuccess( caseLinks ->
                        view.links().setValue( caseLinks == null ? null : new HashSet<>( caseLinks ) )
                ) );

    }

    private void requestCaseLinks( Long issueId ) {
        caseLinkController.getCaseLinks( issueId, new FluentCallback<List<CaseLink>>().withSuccess( caseLinks ->
                view.links().setValue( caseLinks == null ? null : new HashSet<>( caseLinks ) )
        ) );
    }

    private void requestIssue(Long number, final boolean isRestoredIssue ){
        issueService.getIssue(number, new RequestCallback<CaseObject>() {
            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onSuccess(CaseObject issue) {
                IssueEditActivity.this.issue = issue;
                initDetails.parent.add(view.asWidget());
                fillView(issue, isRestoredIssue);
                requestCaseLinks(issue.getId());
            }
        });
    }

    private CaseObject createNewIssue() {
        CaseObject caseObject = new CaseObject();
        boolean isPrivacyVisible = policyService.hasPrivilegeFor(En_Privilege.ISSUE_PRIVACY_VIEW);
        caseObject.setPrivateCase(isPrivacyVisible ? true : false);
        return caseObject;
    }

    private void fillView(CaseObject issue, boolean isRestoredIssue) {

        view.attachmentsContainer().clear();

        if (isNew(issue)) {
            view.setCaseNumber(null);
            view.numberContainerVisibility().setVisible(false);
            view.showComments(false);
            view.getCommentsContainer().clear();
            view.privacyVisibility().setVisible( policyService.hasPrivilegeFor(En_Privilege.ISSUE_PRIVACY_VIEW));
        } else {
            view.setCaseNumber(issue.getCaseNumber());
            view.privacyVisibility().setVisible(false);
            view.setPrivacyIcon(issue.isPrivateCase());
            view.numberContainerVisibility().setVisible(true);
            view.showComments(true);
            view.attachmentsContainer().add(issue.getAttachments());
            view.setCreatedBy(lang.createBy(transliteration(issue.getCreator().getDisplayShortName()), DateFormatter.formatDateTime(issue.getCreated())));
        }

        showComments(issue);

//        view.links().setValue(CollectionUtils.toSet(issue.getLinks(), caseLink -> caseLink));

        view.setTagsAddButtonEnabled(policyService.hasGrantAccessFor( En_Privilege.ISSUE_VIEW ));
        view.setTagsEditButtonEnabled(policyService.hasGrantAccessFor( En_Privilege.ISSUE_VIEW ));

        view.tags().setValue(issue.getTags() == null ? new HashSet<>() : issue.getTags());

        view.numberVisibility().setVisible( !isNew(issue) );
        view.setNumber(isNew(issue) ? null : issue.getCaseNumber().intValue() );

        view.isPrivate().setValue(issue.isPrivateCase());

        boolean isAllowedEditNameAndDescription = isNew(issue) || isSelfIssue(issue);
        if (isAllowedEditNameAndDescription) {
            view.setDescriptionPreviewAllowed(makePreviewDisplaying(AbstractIssueEditView.DESCRIPTION));
            view.switchToRONameDescriptionView(false);
            view.name().setValue(issue.getName());
            view.description().setValue(issue.getInfo());
            view.setNameRO(null, "");
            view.setDescriptionRO(null);
        } else {
            view.switchToRONameDescriptionView(true);
            view.name().setValue(null);
            view.description().setValue(null);
            view.setNameRO(issue.getName() == null ? "" : issue.getName(), En_ExtAppType.JIRA.getCode().equals(issue.getExtAppType()) ? issue.getJiraUrl() : "");
            renderMarkupText(issue.getInfo(), converted -> view.setDescriptionRO(converted));
        }

        view.saveVisibility().setVisible( policyService.hasPrivilegeFor( En_Privilege.ISSUE_EDIT ) );
        initiatorSelectorAllowAddNew(issue.getInitiatorCompanyId());
        view.copyVisibility().setVisible(!isNew(issue));

        fillMetaView(issue, isRestoredIssue);

        unlockSave();
    }

    private void fillMetaView(CaseObject issue, boolean isRestoredIssue) {

        final AbstractIssueMetaView metaView = view.getMetaView();
        final boolean isNew = isNew(issue);
        final boolean isNewNotRestored = isNew && !isRestoredIssue;
        CaseObjectMeta caseMeta = new CaseObjectMeta(issue);
        CaseObjectMetaNotifiers caseMetaNotifiers = new CaseObjectMetaNotifiers(issue);
        CaseObjectMetaJira caseMetaJira = new CaseObjectMetaJira(issue);

        metaView.companyEnabled().setEnabled( isCompanyChangeAllowed(issue) );
        metaView.productEnabled().setEnabled( policyService.hasPrivilegeFor( En_Privilege.ISSUE_PRODUCT_EDIT ) );
        metaView.managerEnabled().setEnabled( policyService.hasPrivilegeFor( En_Privilege.ISSUE_MANAGER_EDIT) );

        if (isNew) {
            metaView.timeElapsedHeader().addClassName("hide");
        } else {
            metaView.timeElapsedHeader().removeClassName("hide");
        }

        if (policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_MANAGER_VIEW)) { //TODO change rule
            metaView.caseSubscriptionContainer().setVisible(true);
        } else {
            caseMetaNotifiers.setNotifiers(null);
            metaView.caseSubscriptionContainer().setVisible(false);
        }

        caseMeta.setImportance(isNewNotRestored ? En_ImportanceLevel.BASIC : caseMeta.getImportance());
        caseMeta.setState(isNewNotRestored ? En_CaseState.CREATED : caseMeta.getState());
        metaView.setStateWorkflow(CaseStateWorkflowUtil.recognizeWorkflow(issue));
        metaView.stateEnabled().setEnabled(!isNew || policyService.personBelongsToHomeCompany());

        boolean hasPrivilegeForTimeElapsed = policyService.hasPrivilegeFor(En_Privilege.ISSUE_WORK_TIME_VIEW);
        metaView.timeElapsedContainerVisibility().setVisible(hasPrivilegeForTimeElapsed);
        if (hasPrivilegeForTimeElapsed) {
            if (isNew) {
                boolean timeElapsedEditAllowed = policyService.personBelongsToHomeCompany();
                caseMeta.setTimeElapsed(null);
                // caseMeta.setTimeElapsedType(null);
                // view.timeElapsedType().setValue( En_TimeElapsedType.NONE );
                metaView.timeElapsedLabelVisibility().setVisible(!timeElapsedEditAllowed);
                metaView.timeElapsedEditContainerVisibility().setVisible(timeElapsedEditAllowed);
            } else {
                metaView.timeElapsedLabelVisibility().setVisible(true);
                metaView.timeElapsedEditContainerVisibility().setVisible(false);
            }
        }

        if (isNewNotRestored) {
            metaView.platformEnabled().setEnabled(false);
        } else {
            Company company = issue.getInitiatorCompany();
            if (company == null) company = policyService.getUserCompany();
            caseMeta.setInitiatorCompany(company);
        }

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

        if (isNewNotRestored) {
            metaView.applyCompanyValueIfOneOption();
        }

        onCompanyChanged();
    }

    private boolean makePreviewDisplaying( String key ) {
        return Boolean.parseBoolean( localStorageService.getOrDefault( ISSUE_EDIT + "_" + key, "false" ) );
    }

    private void fillIssueObject(CaseObject issue) {
        boolean isAllowedEditNameAndDescription = isNew(issue) || isSelfIssue(issue);
        if (isAllowedEditNameAndDescription) {
            issue.setName(view.name().getValue());
            issue.setInfo(view.description().getValue());
        }
        issue.setPrivateCase( view.isPrivate().getValue() );

//        issue.setLinks(view.links().getValue() == null ? new ArrayList<>() : new ArrayList<>(view.links().getValue()));
        issue.setTags(view.tags().getValue() == null ? new HashSet<>() : view.tags().getValue());

        final AbstractIssueMetaView metaView = view.getMetaView();
        if (metaView.getCaseMeta() != null) metaView.getCaseMeta().collectToCaseObject(issue);
        if (metaView.getCaseMetaNotifiers() != null) metaView.getCaseMetaNotifiers().collectToCaseObject(issue);
        if (metaView.getCaseMetaJira() != null) metaView.getCaseMetaJira().collectToCaseObject(issue);
    }

    private void showComments(CaseObject issue) {
        if (isNew(issue)) {
            return;
        }
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

    private boolean validateView(CaseObject issue) {

        boolean isRO = !(isNew(issue) || isSelfIssue(issue));
        boolean isFieldsValid = (isRO || view.nameValidator().isValid());

        if (!isFieldsValid) {
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
        final AbstractIssueMetaView metaView = view.getMetaView();
        metaView.setSubscriptionEmails(value);
        metaView.companyEnabled().setEnabled(isCompanyChangeAllowed(issue));
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

    private String transliteration(String input) {
        return TransliterationUtils.transliterate(input, LocaleInfo.getCurrentLocale().getLocaleName());
    }

    private void initiatorSelectorAllowAddNew(Long companyId) {
        if (companyId == null) {
            return;
        }
        final AbstractIssueMetaView metaView = view.getMetaView();
        boolean allowCreateContact = policyService.hasPrivilegeFor(En_Privilege.CONTACT_CREATE) && !homeCompanyService.isHomeCompany(companyId);
        metaView.initiatorSelectorAllowAddNew(allowCreateContact);
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
    @Inject
    DefaultErrorHandler defaultErrorHandler;
    @Inject
    HomeCompanyService homeCompanyService;

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
