package ru.protei.portal.ui.issue.client.activity.create;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.struct.CaseObjectMetaJira;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.util.TransliterationUtils;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.*;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.common.shared.model.ShortRequestCallback;
import ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueEditView;
import ru.protei.portal.ui.issue.client.activity.edit.CaseStateFilterProvider;
import ru.protei.portal.ui.issue.client.activity.meta.AbstractIssueMetaActivity;
import ru.protei.portal.ui.issue.client.activity.meta.AbstractIssueMetaView;
import ru.protei.portal.ui.issue.client.view.meta.IssueMetaView;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;


/**
 * Активность создания обращения
 */
public abstract class IssueCreateActivity implements AbstractIssueCreateActivity, AbstractIssueMetaActivity, Activity {
    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        view.setFileUploadHandler(new AttachmentUploader.FileUploadHandler() {
            @Override
            public void onSuccess(Attachment attachment) {
                view.attachmentsContainer().add(attachment);
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
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow(IssueEvents.Create event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.ISSUE_EDIT)) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());

        issueMetaView.setMetaActivity(this);
        view.getIssueMetaViewContainer().add(issueMetaView);

        fillView();
    }

    @Event
    public void onFillPerson(PersonEvents.PersonCreated event) {
        if (CrmConstants.Issue.CREATE_CONTACT_IDENTITY.equals(event.origin) && event.person != null) {
            issueMetaView.setInitiator(event.person);
        }
    }

    @Override
    public void onSaveClicked() {
        if (!validateView()) {
            return;
        }

        CaseObject caseObject = fillIssueObject(new CaseObject());
        IssueCreateRequest issueCreateRequest = fillIssueCreateRequest(caseObject);

        if (isLockedSave()) {
            return;
        }

        lockSave();
        issueService.saveIssue(issueCreateRequest, new FluentCallback<Long>()
                .withError(throwable -> unlockSave())
                .withSuccess(caseId -> {
                    unlockSave();
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new IssueEvents.Show(true));
                })
        );
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
                if (!result) {
                    onError(null);
                    return;
                }

                view.attachmentsContainer().remove(attachment);
            }
        });
    }

    @Override
    public void onCompanyChanged() {
        CaseObjectMeta caseObjectMeta = issueMetaView.getCaseMeta();
        Company companyOption = caseObjectMeta.getInitiatorCompany();

        issueMetaView.initiatorEnabled().setEnabled(companyOption != null);
        issueMetaView.initiatorUpdateCompany(companyOption);

        if ( companyOption == null ) {
            setSubscriptionEmails(getSubscriptionsBasedOnPrivacy(null, lang.issueCompanySubscriptionNeedSelectCompany()));
            issueMetaView.setInitiator(null);
        } else {
            initiatorSelectorAllowAddNew(companyOption.getId());
            Long selectedCompanyId = companyOption.getId();

            issueMetaView.setPlatform(null);
            issueMetaView.platformEnabled().setEnabled(true);
            issueMetaView.setPlatformFilter(platformOption -> selectedCompanyId.equals(platformOption.getCompanyId()));

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

            companyService.getCompanyCaseStates(selectedCompanyId, new ShortRequestCallback<List<CaseState>>()
                    .setOnSuccess(caseStates -> {
                        issueMetaView.setStateFilter(caseStateFilter.makeFilter(caseStates));
                        fireEvent(new CaseStateEvents.UpdateSelectorOptions());
                    }));

            Profile profile = policyService.getProfile();
            if (profile.getCompany() != null && Objects.equals(profile.getCompany().getId(), selectedCompanyId)) {
                String transliteration = transliteration(profile.getFullName());
                Person initiator = Person.fromPersonFullNameShortView(new PersonShortView(transliteration, profile.getId(), profile.isFired()));
                issueMetaView.setInitiator(initiator);
            } else {
                issueMetaView.setInitiator(null);
            }
        }

        fireEvent(new CaseStateEvents.UpdateSelectorOptions());
    }

    @Override
    public void onCreateContactClicked() {
        CaseObjectMeta caseObjectMeta = issueMetaView.getCaseMeta();

        if (caseObjectMeta.getInitiatorCompany() != null) {
            fireEvent(new ContactEvents.Edit(null, caseObjectMeta.getInitiatorCompany(), CrmConstants.Issue.CREATE_CONTACT_IDENTITY));
        }
    }

    @Override
    public void onLocalClicked() {
        setSubscriptionEmails(getSubscriptionsBasedOnPrivacy(subscriptionsList, subscriptionsListEmptyMessage));
    }

    @Override
    public void renderMarkupText(String text, Consumer<String> consumer) {
        textRenderController.render(text, En_TextMarkup.MARKDOWN, new FluentCallback<String>()
                .withError(throwable -> consumer.accept(null))
                .withSuccess(consumer));
    }
    @Override
    public void onDisplayPreviewChanged( String key, boolean isDisplay ) {
        localStorageService.set( ISSUE_CREATE + "_" + key, String.valueOf( isDisplay ) );
    }

    private void fillView() {
        view.attachmentsContainer().clear();
        view.privacyVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_PRIVACY_VIEW));

        view.setTagsAddButtonEnabled(policyService.hasGrantAccessFor(En_Privilege.ISSUE_VIEW));
        view.setTagsEditButtonEnabled(policyService.hasGrantAccessFor(En_Privilege.ISSUE_VIEW));

        view.links().setValue(null);
        view.tags().setValue(null);
        view.isPrivate().setValue(false);

        view.setDescriptionPreviewAllowed(makePreviewDisplaying(AbstractIssueEditView.DESCRIPTION));
        view.name().setValue(null);
        view.description().setValue(null);

        view.saveVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_EDIT));
        initiatorSelectorAllowAddNew(null);

        fillMetaView(new CaseObject());
        unlockSave();
    }

    private void fillMetaView(CaseObject issue) {
        CaseObjectMeta caseObjectMeta = new CaseObjectMeta(issue);

        caseObjectMeta.setState(En_CaseState.CREATED);
        caseObjectMeta.setImportance(En_ImportanceLevel.BASIC);
        caseObjectMeta.setInitiatorCompany(policyService.getUserCompany());

        issueMetaView.companyEnabled().setEnabled(true);
        issueMetaView.productEnabled().setEnabled(policyService.hasPrivilegeFor(En_Privilege.ISSUE_PRODUCT_EDIT));
        issueMetaView.managerEnabled().setEnabled(policyService.hasPrivilegeFor(En_Privilege.ISSUE_MANAGER_EDIT));
        issueMetaView.caseSubscriptionContainer().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_MANAGER_VIEW));
        issueMetaView.stateEnabled().setEnabled(true);
        issueMetaView.timeElapsedContainerVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_WORK_TIME_VIEW));
        issueMetaView.timeElapsedLabelVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_WORK_TIME_VIEW));
        issueMetaView.timeElapsedEditContainerVisibility().setVisible(!policyService.hasPrivilegeFor(En_Privilege.ISSUE_WORK_TIME_VIEW));
        issueMetaView.platformVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_PLATFORM_EDIT));
        issueMetaView.setStateWorkflow(En_CaseStateWorkflow.NO_WORKFLOW);
        issueMetaView.setCaseMetaNotifiers(new CaseObjectMetaNotifiers(issue));
        issueMetaView.setCaseMeta(caseObjectMeta);

        onCompanyChanged();
    }

    private CaseObject fillIssueObject(CaseObject issue) {
        CaseObjectMeta caseObjectMeta = issueMetaView.getCaseMeta();
        CaseObjectMetaNotifiers notifiers = issueMetaView.getCaseMetaNotifiers();

        issue.setName(view.name().getValue());
        issue.setInfo(view.description().getValue());
        issue.setPrivateCase(view.isPrivate().getValue());
        issue.setStateId(caseObjectMeta.getStateId());
        issue.setImpLevel(caseObjectMeta.getImpLevel());

        issue.setInitiatorCompany(caseObjectMeta.getInitiatorCompany());
        issue.setInitiator(caseObjectMeta.getInitiator());
        issue.setProduct(caseObjectMeta.getProduct());
        issue.setManager(caseObjectMeta.getManager());
        issue.setNotifiers(notifiers.getNotifiers());
        issue.setTags(view.tags().getValue() == null ? new HashSet<>() : view.tags().getValue());
        issue.setPlatformId(caseObjectMeta.getPlatformId());
        issue.setAttachmentExists(!CollectionUtils.isEmpty(view.attachmentsContainer().getAll()));
        issue.setAttachments(new ArrayList<>(view.attachmentsContainer().getAll()));

        if (policyService.hasPrivilegeFor(En_Privilege.ISSUE_WORK_TIME_VIEW) && policyService.personBelongsToHomeCompany()) {
            issue.setTimeElapsed(caseObjectMeta.getTimeElapsed());
            issue.setTimeElapsedType(issueMetaView.timeElapsedType().getValue() == null ? En_TimeElapsedType.NONE : issueMetaView.timeElapsedType().getValue());
        }

        return issue;
    }

    private IssueCreateRequest fillIssueCreateRequest(CaseObject issue) {
        CaseObjectMeta caseObjectMeta = issueMetaView.getCaseMeta();
        IssueCreateRequest issueCreateRequest = new IssueCreateRequest();

        issueCreateRequest.setCaseObject(issue);
        issueCreateRequest.setLinks(view.links().getValue() == null ? new ArrayList<>() : new ArrayList<>(view.links().getValue()));
        issueCreateRequest.setTimeElapsed(caseObjectMeta.getTimeElapsed());
        issueCreateRequest.setTimeElapsedType(issueMetaView.timeElapsedType().getValue());

        return issueCreateRequest;
    }

    private void setSubscriptionEmails(String value) {
        issueMetaView.setSubscriptionEmails(value);
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

    private boolean validateView() {
        CaseObjectMeta caseObjectMeta = issueMetaView.getCaseMeta();

        if (caseObjectMeta.getInitiatorCompany() == null) {
            fireEvent(new NotifyEvents.Show(lang.errSaveIssueNeedSelectCompany(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if (caseObjectMeta.getManager() == null && isStateWithRestrictions(caseObjectMeta.getState())) {
            fireEvent(new NotifyEvents.Show(lang.errSaveIssueNeedSelectManager(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if (caseObjectMeta.getProduct() == null && isStateWithRestrictions(caseObjectMeta.getState())) {
            fireEvent(new NotifyEvents.Show(lang.errProductNotSelected(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        boolean isFieldsValid = view.nameValidator().isValid() &&
                issueMetaView.stateValidator().isValid() &&
                        issueMetaView.importanceValidator().isValid() &&
                        issueMetaView.companyValidator().isValid();

        if (!isFieldsValid) {
            fireEvent(new NotifyEvents.Show(lang.errSaveIssueFieldsInvalid(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        return true;
    }

    private String getSubscriptionsBasedOnPrivacy(List<CompanySubscription> subscriptionsList, String emptyMessage) {
        this.subscriptionsList = subscriptionsList;
        this.subscriptionsListEmptyMessage = emptyMessage;

        if (CollectionUtils.isEmpty(subscriptionsList)) {
            return emptyMessage;
        }

        List<String> subscriptionsBasedOnPrivacyList = subscriptionsList.stream()
                .map(CompanySubscription::getEmail)
                .filter(mail -> !view.isPrivate().getValue() || CompanySubscription.isProteiRecipient(mail)).collect( Collectors.toList());

        return CollectionUtils.isEmpty(subscriptionsBasedOnPrivacyList)
                ? emptyMessage
                : String.join(", ", subscriptionsBasedOnPrivacyList);
    }

    private String transliteration(String input) {
        return TransliterationUtils.transliterate(input, LocaleInfo.getCurrentLocale().getLocaleName());
    }

    private void initiatorSelectorAllowAddNew(Long companyId) {
        if (companyId == null) {
            return;
        }

        issueMetaView.initiatorSelectorAllowAddNew(policyService.hasPrivilegeFor( En_Privilege.CONTACT_CREATE) && !homeCompanyService.isHomeCompany(companyId));
    }

    private boolean isStateWithRestrictions(En_CaseState caseState) {
        return !En_CaseState.CREATED.equals(caseState) &&
                !En_CaseState.CANCELED.equals(caseState);
    }

    private boolean makePreviewDisplaying( String key ) {
        return Boolean.parseBoolean( localStorageService.getOrDefault( ISSUE_CREATE + "_" + key, "false" ) );
    }

    @Inject
    PolicyService policyService;
    @Inject
    HomeCompanyService homeCompanyService;
    @Inject
    IssueControllerAsync issueService;
    @Inject
    AttachmentServiceAsync attachmentService;
    @Inject
    TextRenderControllerAsync textRenderController;
    @Inject
    Lang lang;
    @Inject
    LocalStorageService localStorageService;
    @Inject
    CompanyControllerAsync companyService;
    @Inject
    CaseStateFilterProvider caseStateFilter;

    @Inject
    AbstractIssueCreateView view;
    @Inject
    IssueMetaView issueMetaView;

    private boolean saving;
    private AppEvents.InitDetails initDetails;
    private Profile authProfile;
    private List<CompanySubscription> subscriptionsList;
    private String subscriptionsListEmptyMessage;
    private static final String ISSUE_CREATE = "issue_create_is_preview_displayed";
}
