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
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.util.TransliterationUtils;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.PlatformOption;
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

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;


/**
 * Активность создания обращения
 */
public abstract class IssueCreateActivity implements AbstractIssueCreateActivity, Activity {
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

        fillView(pageState != null);

        pageState = null;
    }

    @Event
    public void onFillPerson(PersonEvents.PersonCreated event) {
        if (CrmConstants.Issue.CREATE_CONTACT_IDENTITY.equals(event.origin) && event.person != null) {
            view.initiator().setValue(event.person.toFullNameShortView());
        }
    }

    @Override
    public void onSaveClicked() {
        if (!validateView()) {
            return;
        }

        CaseObject caseObject = fillIssueObject(new CaseObject());

        if (isLockedSave()) {
            return;
        }

        lockSave();
        issueService.saveIssue(caseObject, new FluentCallback<Long>()
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
        Company companyOption = Company.fromEntityOption(view.company().getValue());

        view.initiatorState().setEnabled(companyOption != null);
        view.initiatorUpdateCompany(companyOption);

        if ( companyOption == null ) {
            setSubscriptionEmails(getSubscriptionsBasedOnPrivacy(null, lang.issueCompanySubscriptionNeedSelectCompany()));
            view.initiator().setValue(null);
        } else {
            initiatorSelectorAllowAddNew(companyOption.getId());
            Long selectedCompanyId = companyOption.getId();

            view.platform().setValue(null);
            view.platformState().setEnabled(true);
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
            if (profile.getCompany() != null && Objects.equals(profile.getCompany().getId(), selectedCompanyId)) {
                view.initiator().setValue(new PersonShortView(transliteration(profile.getShortName()), profile.getId(), profile.isFired()));
            } else {
                view.initiator().setValue(null);
            }
        }

        fireEvent(new CaseStateEvents.UpdateSelectorOptions());
    }

    @Override
    public void onCreateContactClicked() {
        if (view.company().getValue() != null) {
            pageState = fillIssueObject(new CaseObject());
            fireEvent(new ContactEvents.Edit(null, Company.fromEntityOption(view.company().getValue()), CrmConstants.Issue.CREATE_CONTACT_IDENTITY));
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

    private void fillView(boolean isRestoredIssue) {
        view.productEnabled().setEnabled(policyService.hasPrivilegeFor(En_Privilege.ISSUE_PRODUCT_EDIT));
        view.managerEnabled().setEnabled(policyService.hasPrivilegeFor(En_Privilege.ISSUE_MANAGER_EDIT));

        view.attachmentsContainer().clear();
        view.privacyVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_PRIVACY_VIEW));

        view.caseSubscriptionContainer().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_MANAGER_VIEW));

        view.setTagsAddButtonEnabled(policyService.hasGrantAccessFor(En_Privilege.ISSUE_VIEW));
        view.setTagsEditButtonEnabled(policyService.hasGrantAccessFor(En_Privilege.ISSUE_VIEW));

        view.links().setValue(isRestoredIssue ? new HashSet<>(pageState.getLinks()) : null);
        view.isPrivate().setValue(isRestoredIssue ? pageState.isPrivateCase() : false);

        view.setDescriptionPreviewAllowed(makePreviewDisplaying(AbstractIssueEditView.DESCRIPTION));
        view.name().setValue(isRestoredIssue ? pageState.getName() : null);
        view.description().setValue(isRestoredIssue ? pageState.getInfo() : null);

        view.setStateWorkflow(En_CaseStateWorkflow.NO_WORKFLOW);
        view.state().setValue(isRestoredIssue ? pageState.getState() : En_CaseState.CREATED);
        view.stateEnabled().setEnabled(policyService.personBelongsToHomeCompany());
        view.importance().setValue(isRestoredIssue ? pageState.importanceLevel() : En_ImportanceLevel.BASIC);

        boolean hasPrivilegeForTimeElapsed = policyService.hasPrivilegeFor(En_Privilege.ISSUE_WORK_TIME_VIEW);
        view.timeElapsedContainerVisibility().setVisible(hasPrivilegeForTimeElapsed);

        if (hasPrivilegeForTimeElapsed) {
            boolean timeElapsedEditAllowed = policyService.personBelongsToHomeCompany();
            view.timeElapsedInput().setTime(isRestoredIssue ? pageState.getTimeElapsed() : 0L);
            view.timeElapsedEditContainerVisibility().setVisible(timeElapsedEditAllowed);
            view.timeElapsedType().setValue(isRestoredIssue ? pageState.getTimeElapsedType() : En_TimeElapsedType.NONE);
        }

        view.applyCompanyValueIfOneOption();
        view.product().setValue(isRestoredIssue && pageState.getProduct() != null ? pageState.getProduct().toProductShortView() : null);
        view.manager().setValue(isRestoredIssue && pageState.getManager() != null ? pageState.getManager().toShortNameShortView() : null);
        view.saveVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_EDIT));
        initiatorSelectorAllowAddNew(isRestoredIssue ? pageState.getInitiatorCompanyId() : null);
        view.platform().setValue(null);
        view.platformVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_PLATFORM_EDIT));

        unlockSave();
    }

    private CaseObject fillIssueObject(CaseObject issue) {
        issue.setName(view.name().getValue());
        issue.setInfo(view.description().getValue());
        issue.setPrivateCase(view.isPrivate().getValue());
        issue.setStateId(view.state().getValue().getId());
        issue.setImpLevel(view.importance().getValue().getId());

        issue.setInitiatorCompany(Company.fromEntityOption(view.company().getValue()));
        issue.setInitiator(Person.fromPersonShortView(view.initiator().getValue()));
        issue.setProduct(DevUnit.fromProductShortView(view.product().getValue()));
        issue.setManager(Person.fromPersonShortView(view.manager().getValue()));
        issue.setNotifiers(view.notifiers().getValue().stream().map(Person::fromPersonShortView).collect(Collectors.toSet()));
        issue.setLinks(view.links().getValue() == null ? new ArrayList<>() : new ArrayList<>(view.links().getValue()));
        issue.setTags(view.tags().getValue() == null ? new HashSet<>() : view.tags().getValue());
        issue.setPlatformId(view.platform().getValue() == null ? null : view.platform().getValue().getId());
        issue.setAttachmentExists(!CollectionUtils.isEmpty(view.attachmentsContainer().getAll()));
        issue.setAttachments(new ArrayList<>(view.attachmentsContainer().getAll()));

        if (policyService.hasPrivilegeFor(En_Privilege.ISSUE_WORK_TIME_VIEW) && policyService.personBelongsToHomeCompany()) {
            issue.setTimeElapsed(view.timeElapsedInput().getTime());
            En_TimeElapsedType elapsedType = view.timeElapsedType().getValue();
            issue.setTimeElapsedType(elapsedType != null ? elapsedType : En_TimeElapsedType.NONE);
        }

        return issue;
    }

    private void setSubscriptionEmails(String value) {
        view.setSubscriptionEmails(value);
        view.companyEnabled().setEnabled(true);
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
        if (view.company().getValue() == null) {
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

        boolean isFieldsValid = (view.nameValidator().isValid()) &&
                view.stateValidator().isValid() &&
                view.importanceValidator().isValid() &&
                view.companyValidator().isValid();

        if (!isFieldsValid) {
            fireEvent(new NotifyEvents.Show(lang.errSaveIssueFieldsInvalid(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        return true;
    }

    private void addAttachmentsToCollection(Attachment attachment) {
        if (attachments == null) {
            attachments = new HashSet<>();
        }

        attachments.add(attachment);
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

        view.initiatorSelectorAllowAddNew(policyService.hasPrivilegeFor( En_Privilege.CONTACT_CREATE) && !homeCompanyService.isHomeCompany(companyId));
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

    private CaseObject pageState;

    private Set<Attachment> attachments;
    private boolean saving;
    private AppEvents.InitDetails initDetails;
    private Profile authProfile;
    private List<CompanySubscription> subscriptionsList;
    private String subscriptionsListEmptyMessage;
    private static final String ISSUE_CREATE = "issue_create_is_preview_displayed";
}
