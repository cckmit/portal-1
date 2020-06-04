package ru.protei.portal.ui.issue.client.activity.create;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.PlatformQuery;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.util.TransliterationUtils;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.PlatformOption;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.activity.casetag.taglist.AbstractCaseTagListActivity;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DefaultSlaValues;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.core.model.util.UiResult;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.*;
import ru.protei.portal.ui.common.client.widget.selector.product.ProductWithChildrenModel;
import ru.protei.portal.ui.common.client.widget.selector.product.ProductModel;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.common.client.widget.uploader.PasteInfo;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.common.shared.model.ShortRequestCallback;
import ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueEditView;
import ru.protei.portal.ui.issue.client.activity.meta.AbstractIssueMetaActivity;
import ru.protei.portal.ui.issue.client.activity.meta.AbstractIssueMetaView;
import ru.protei.portal.ui.issue.client.common.CaseStateFilterProvider;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.util.CrmConstants.SOME_LINKS_NOT_SAVED;
import static ru.protei.portal.ui.common.client.common.UiConstants.ISSUE_CREATE_PREVIEW_DISPLAYED;
import static ru.protei.portal.core.model.helper.CaseCommentUtils.addImageInMessage;


/**
 * Активность создания обращения
 */
public abstract class IssueCreateActivity implements AbstractIssueCreateActivity, AbstractIssueMetaActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        issueMetaView.setActivity(this);
        view.getIssueMetaViewContainer().add(issueMetaView.asWidget());

        view.setFileUploadHandler(new AttachmentUploader.FileUploadHandler() {
            @Override
            public void onSuccess(Attachment attachment, PasteInfo pasteInfo) {
                if (pasteInfo != null && attachment.getMimeType().startsWith("image/")) {
                    addImageToMessage(pasteInfo.strPosition, attachment);
                }
                view.attachmentsContainer().add(attachment);
            }

            @Override
            public void onError(En_FileUploadStatus status, String details) {
                fireEvent(new NotifyEvents.Show(En_FileUploadStatus.SIZE_EXCEED_ERROR.equals(status) ? lang.uploadFileSizeExceed() + " (" + details + "Mb)" : lang.uploadFileError(), NotifyEvents.NotifyType.ERROR));
            }
        });

        productModel.setUnitState(En_DevUnitState.ACTIVE);
        productWithChildrenModel.setUnitState(En_DevUnitState.ACTIVE);

        productModel.setUnitTypes(En_DevUnitType.PRODUCT);
        productWithChildrenModel.setUnitTypes(En_DevUnitType.COMPLEX, En_DevUnitType.PRODUCT);
    }

    @Event
    public void authEvent(AuthEvents.Success event) {
        caseStateController.getCaseState(CrmConstants.State.CREATED, new FluentCallback<CaseState>()
                .withSuccess(this::setCreatedCaseState));
    }

    @Event
    public void onInit(AppEvents.InitDetails init) {
        this.init = init;
    }

    @Event
    public void onShow(IssueEvents.Create event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.ISSUE_EDIT)) {
            fireEvent(new ForbiddenEvents.Show(init.parent));
            return;
        }

        init.parent.clear();
        init.parent.add(view.asWidget());

        createRequest = new CaseObjectCreateRequest();
        subscriptionsList = null;
        subscriptionsListEmptyMessage = null;

        fillView();
    }

    @Event
    public void onFillPerson(PersonEvents.PersonCreated event) {
        if (CrmConstants.Issue.CREATE_CONTACT_IDENTITY.equals(event.origin) && event.person != null) {
            issueMetaView.setInitiator(event.person);
        }
    }

    @Event
    public void onAttachTag(CaseTagEvents.Attach event) {
        createRequest.addTag(event.tag);
    }

    @Event
    public void onDetachTag(CaseTagEvents.Detach event) {
        createRequest.getTags().stream()
                .filter(tag -> Objects.equals(tag.getId(), event.id))
                .findFirst()
                .ifPresent(caseTag -> createRequest.getTags().remove(caseTag));
    }

    @Event
    public void onAddLink(CaseLinkEvents.Added event) {
        if (ISSUE_CASE_TYPE.equals(event.caseType)) {
            createRequest.addLink(event.caseLink);
        }
    }

    @Event
    public void onRemoveLink(CaseLinkEvents.Removed event) {
        if (ISSUE_CASE_TYPE.equals(event.caseType)) {
            createRequest.getLinks().remove(event.caseLink);
        }
    }

    @Override
    public void onSaveClicked() {
        if (!validateView()) {
            return;
        }

        createRequest.setCaseObject( fillCaseCreateRequest( createRequest.getCaseObject() ) );

        if (isLockedSave()) {
            return;
        }

        lockSave();
        issueService.createIssue(createRequest, new FluentCallback<UiResult<Long>>()
                .withError(throwable -> unlockSave())
                .withSuccess(createIssueResult -> {
                    unlockSave();
                    if (SOME_LINKS_NOT_SAVED.equals(createIssueResult.getMessage())) {
                        fireEvent(new NotifyEvents.Show(lang.caseLinkSomeNotAdded(), NotifyEvents.NotifyType.INFO));
                    }
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new IssueEvents.Show(false));
                })
        );
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    @Override
    public void onAddTagClicked(IsWidget target) {
        boolean isCanEditTags = policyService.hasPrivilegeFor(En_Privilege.ISSUE_EDIT);
        fireEvent(new CaseTagEvents.ShowSelector(target.asWidget(), ISSUE_CASE_TYPE, isCanEditTags, tagListActivity));
    }

    @Override
    public void onAddLinkClicked(IsWidget target) {
        fireEvent(new CaseLinkEvents.ShowLinkSelector(target, ISSUE_CASE_TYPE));
    }

    @Override
    public void removeAttachment(Attachment attachment) {
        attachmentService.removeAttachmentEverywhere(En_CaseType.CRM_SUPPORT, attachment.getId(), new FluentCallback<Boolean>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.removeFileError(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess(result -> view.attachmentsContainer().remove(attachment)));
    }

    @Override
    public void onProductChanged() {
        setSubscriptionEmails(getSubscriptionsBasedOnPrivacy(filterByPlatformAndProduct(subscriptionsList), subscriptionsListEmptyMessage));
    }

    @Override
    public void onPlatformChanged() {
        requestSla(
                issueMetaView.platform().getValue() == null ? null : issueMetaView.platform().getValue().getId(),
                slaList -> fillSla(getSlaByImportanceLevel(slaList, issueMetaView.importance().getValue().getId()))
        );

        if (isCompanyWithAutoOpenIssues(currentCompany)) {
            resetProduct(issueMetaView);
            updateProductsFilter(issueMetaView, issueMetaView.getCompany().getId(), issueMetaView.platform().getValue() == null ? null : issueMetaView.platform().getValue().getId());
        }

        setSubscriptionEmails(getSubscriptionsBasedOnPrivacy(filterByPlatformAndProduct(subscriptionsList), subscriptionsListEmptyMessage));
    }

    @Override
    public void onCompanyChanged() {
        Company companyOption = issueMetaView.getCompany();

        fillImportanceSelector(companyOption.getId());

        issueMetaView.initiatorUpdateCompany(companyOption);

        initiatorSelectorAllowAddNew(companyOption.getId());

        companyService.getCompanyUnsafe(companyOption.getId(), new FluentCallback<Company>()
                .withSuccess(company -> {
                    setCurrentCompany(company);
                    fillPlatformValueAndUpdateProductsFilter(company);
                })
        );

        issueMetaView.setPlatformFilter(platformOption -> companyOption.getId().equals(platformOption.getCompanyId()));

        companyService.getCompanyWithParentCompanySubscriptions(
                companyOption.getId(),
                new ShortRequestCallback<List<CompanySubscription>>()
                        .setOnSuccess(subscriptions -> {
                            subscriptions = filterByPlatformAndProduct(subscriptions);
                            setSubscriptionEmails(getSubscriptionsBasedOnPrivacy(
                                    subscriptions,
                                    isEmpty(subscriptions) ?
                                            lang.issueCompanySubscriptionNotDefined() :
                                            lang.issueCompanySubscriptionBasedOnPrivacyNotDefined()
                                    )
                            );
                        })
        );

        companyService.getCompanyCaseStates(companyOption.getId(), new ShortRequestCallback<List<CaseState>>()
                .setOnSuccess(caseStates -> {
                    issueMetaView.setStateFilter(caseStateFilter.makeFilter(caseStates));
                    fireEvent(new CaseStateEvents.UpdateSelectorOptions());
                }));

        Profile profile = policyService.getProfile();
        if (!Objects.equals(profile.getCompany().getId(), companyOption.getId())) {
            issueMetaView.setInitiator(null);
        } else {
            Person initiator = Person.fromPersonFullNameShortView(new PersonShortView(transliteration(profile.getFullName()), profile.getId(), profile.isFired()));
            issueMetaView.setInitiator(initiator);
        }
        fireEvent(new CaseStateEvents.UpdateSelectorOptions());
    }

    @Override
    public void onCreateContactClicked() {
        if (issueMetaView.getCompany() == null) {
            return;
        }

        fireEvent(new ContactEvents.Edit(null, issueMetaView.getCompany(), CrmConstants.Issue.CREATE_CONTACT_IDENTITY));
    }

    @Override
    public void onPrivacyChanged() {
        setSubscriptionEmails(getSubscriptionsBasedOnPrivacy(filterByPlatformAndProduct(subscriptionsList), subscriptionsListEmptyMessage));
    }

    @Override
    public void renderMarkupText(String text, Consumer<String> consumer) {
        textRenderController.render(text, En_TextMarkup.MARKDOWN, new FluentCallback<String>()
                .withError(throwable -> consumer.accept(null))
                .withSuccess(consumer));
    }

    @Override
    public void onDisplayPreviewChanged( String key, boolean isDisplay ) {
        localStorageService.set( ISSUE_CREATE_PREVIEW_DISPLAYED + "_" + key, String.valueOf( isDisplay ) );
    }

    @Override
    public void onImportanceChanged() {
        fillSla(getSlaByImportanceLevel(slaList, issueMetaView.importance().getValue().getId()));
    }

    @Override
    public void onPauseDateChanged() {
        issueMetaView.setPauseDateValid(isPauseDateValid(issueMetaView.state().getValue().getId(),
                issueMetaView.pauseDate().getValue() == null ? null
                        : issueMetaView.pauseDate().getValue().getTime()));
    }

    @Override
    public void onStateChange() {
        issueMetaView.pauseDate().setValue(null);
        issueMetaView.pauseDateContainerVisibility().setVisible(CrmConstants.State.PAUSED == issueMetaView.state().getValue().getId());
        setManagerCompanyEnabled(issueMetaView, issueMetaView.state().getValue().getId());

        boolean stateValid = isPauseDateValid(issueMetaView.state().getValue().getId(), issueMetaView.pauseDate().getValue() == null ? null : issueMetaView.pauseDate().getValue().getTime());
        issueMetaView.setPauseDateValid(stateValid);
    }

    @Override
    public void onManagerCompanyChanged() {
        issueMetaView.setManager(null);
        issueMetaView.updateManagersCompanyFilter(issueMetaView.getManagerCompany().getId());
    }

    private void fillPlatformValueAndUpdateProductsFilter(final Company company) {
        requestPlatforms(company.getId(), platformOptions -> {
            if (platformOptions != null && platformOptions.size() == 1) {
                issueMetaView.platform().setValue(platformOptions.get(0));
            } else {
                issueMetaView.platform().setValue(null);
            }

            if (!isCompanyWithAutoOpenIssues(company)) {
                issueMetaView.setProductModel(productModel);
            } else {
                resetProduct(issueMetaView);
                issueMetaView.setProductModel(productWithChildrenModel);
                updateProductsFilter(
                        issueMetaView,
                        issueMetaView.platform().getValue() == null ?
                                toSet(emptyIfNull(platformOptions), PlatformOption::getId) :
                                new HashSet<>(Collections.singleton(issueMetaView.platform().getValue().getId()))
                );
            }

            requestSla(
                    issueMetaView.platform().getValue() == null ? null : issueMetaView.platform().getValue().getId(),
                    slaList -> fillSla(getSlaByImportanceLevel(slaList, issueMetaView.importance().getValue().getId()))
            );

            setSubscriptionEmails(getSubscriptionsBasedOnPrivacy(filterByPlatformAndProduct(subscriptionsList), subscriptionsListEmptyMessage));
        });
    }

    private void requestPlatforms(Long companyId, Consumer<List<PlatformOption>> resultConsumer) {
        PlatformQuery query = new PlatformQuery();
        query.setCompanyId(companyId);

        siteFolderController.getPlatformsOptionList(query, new FluentCallback<List<PlatformOption>>()
                .withError(throwable -> resultConsumer.accept(null))
                .withSuccess(resultConsumer)
        );
    }

    private void addImageToMessage(Integer strPosition, Attachment attach) {
        view.description().setValue(
                addImageInMessage(En_TextMarkup.MARKDOWN, view.description().getValue(), strPosition, attach));
    }

    private void fillView() {
        view.privacyVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_PRIVACY_VIEW));
        view.isPrivate().setValue(true);

        view.name().setValue(null);
        view.description().setValue(null);
        view.setDescriptionPreviewAllowed(makePreviewDisplaying(AbstractIssueEditView.DESCRIPTION));

        fillMetaView( initCaseMeta() );
        onCompanyChanged();

        view.attachmentsContainer().clear();

        fireEvent(new CaseLinkEvents.Show(view.getLinksContainer())
                .withPageId(lang.issues())
                .withCaseType(En_CaseType.CRM_SUPPORT));

        fireEvent(new CaseTagEvents.ShowList(view.getTagsContainer(), (Long) null, false, a -> tagListActivity = a));

        view.saveVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_EDIT));
        unlockSave();
    }

    private void fillMetaView( CaseObjectMeta caseObjectMeta ) {
        issueMetaView.companyEnabled().setEnabled(true);
        issueMetaView.productEnabled().setEnabled(isProductEnabled(caseObjectMeta.getInitiatorCompany()));
        issueMetaView.caseSubscriptionContainer().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_MANAGER_VIEW));
        issueMetaView.stateEnabled().setEnabled(true);
        issueMetaView.timeElapsedContainerVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_WORK_TIME_VIEW));
        issueMetaView.timeElapsedEditContainerVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_EDIT));
        issueMetaView.timeElapsedHeaderVisibility().setVisible(false);
        setPlatformVisibility(issueMetaView, policyService.hasPrivilegeFor(En_Privilege.ISSUE_PLATFORM_EDIT));
        issueMetaView.setStateWorkflow(En_CaseStateWorkflow.NO_WORKFLOW);//Обязательно сетить до установки значения
        issueMetaView.setTimeElapsedType(En_TimeElapsedType.NONE);

        issueMetaView.setCaseMetaNotifiers(null);

        issueMetaView.importance().setValue(caseObjectMeta.getImportance());
        fillImportanceSelector(caseObjectMeta.getInitiatorCompanyId());
        issueMetaView.state().setValue(createdCaseState);
        issueMetaView.pauseDate().setValue(caseObjectMeta.getPauseDate() == null ? null : new Date(caseObjectMeta.getPauseDate()));
        issueMetaView.pauseDateContainerVisibility().setVisible(CrmConstants.State.PAUSED == caseObjectMeta.getStateId());
        issueMetaView.setPauseDateValid(isPauseDateValid(caseObjectMeta.getStateId(), caseObjectMeta.getPauseDate()));
        issueMetaView.setCompany(caseObjectMeta.getInitiatorCompany());

        this.currentCompany = caseObjectMeta.getInitiatorCompany();

        issueMetaView.setInitiator(caseObjectMeta.getInitiator());
        issueMetaView.setPlatformFilter(platformOption -> caseObjectMeta.getInitiatorCompanyId().equals(platformOption.getCompanyId()));

        issueMetaView.setProductMandatory(isCustomerWithAutoOpenIssues(caseObjectMeta.getInitiatorCompany()));
        issueMetaView.setProductModel(isCompanyWithAutoOpenIssues(caseObjectMeta.getInitiatorCompany()) ? productWithChildrenModel : productModel);
        issueMetaView.product().setValue(ProductShortView.fromProduct(caseObjectMeta.getProduct()));

        issueMetaView.setTimeElapsed(caseObjectMeta.getTimeElapsed());
        fillManagerInfoContainer(issueMetaView, caseObjectMeta);

        issueMetaView.slaContainerVisibility().setVisible(isSystemScope());
        requestSla(caseObjectMeta.getPlatformId(), slaList -> fillSla(getSlaByImportanceLevel(slaList, caseObjectMeta.getImpLevel())));
    }

    private void fillManagerInfoContainer(final AbstractIssueMetaView issueMetaView, CaseObjectMeta caseObjectMeta) {
        issueMetaView.managerEnabled().setEnabled(policyService.hasPrivilegeFor(En_Privilege.ISSUE_MANAGER_EDIT));
        setManagerCompanyEnabled(issueMetaView, caseObjectMeta.getStateId());

        if (caseObjectMeta.getManagerCompanyId() != null) {
            issueMetaView.setManagerCompany(new EntityOption(caseObjectMeta.getManagerCompanyName(), caseObjectMeta.getManagerCompanyId()));
            issueMetaView.updateManagersCompanyFilter(caseObjectMeta.getManagerCompanyId());
        } else {
            homeCompanyService.getHomeCompany(CrmConstants.Company.HOME_COMPANY_ID, company -> {
                issueMetaView.setManagerCompany(company);
                issueMetaView.updateManagersCompanyFilter(company.getId());
            });
        }

        issueMetaView.setManager(caseObjectMeta.getManager());
    }

    private void requestSla(Long platformId, Consumer<List<ProjectSla>> slaConsumer) {
        if (!isSystemScope()) {
            return;
        }

        if (platformId == null) {
            slaList = DefaultSlaValues.getList();
            issueMetaView.setValuesContainerWarning(true);
            issueMetaView.setSlaTimesContainerTitle(lang.projectSlaDefaultValues());
            slaConsumer.accept(slaList);
            return;
        }

        slaService.getSlaByPlatformId(platformId, new FluentCallback<List<ProjectSla>>()
                .withSuccess(result -> {
                    slaList = result.isEmpty() ? DefaultSlaValues.getList() : result;
                    issueMetaView.setValuesContainerWarning(result.isEmpty());
                    issueMetaView.setSlaTimesContainerTitle(result.isEmpty() ? lang.projectSlaDefaultValues() : lang.projectSlaSetValuesByManager());
                    slaConsumer.accept(slaList);
                })
        );
    }

    private ProjectSla getSlaByImportanceLevel(List<ProjectSla> slaList, final int importanceLevelId) {
        return slaList
                .stream()
                .filter(sla -> Objects.equals(importanceLevelId, sla.getImportanceLevelId()))
                .findAny()
                .orElse(new ProjectSla());
    }

    private void fillSla(ProjectSla sla) {
        issueMetaView.slaReactionTime().setTime(sla.getReactionTime());
        issueMetaView.slaTemporarySolutionTime().setTime(sla.getTemporarySolutionTime());
        issueMetaView.slaFullSolutionTime().setTime(sla.getFullSolutionTime());
    }


    private CaseObjectMeta initCaseMeta() {
        CaseObjectMeta caseObjectMeta = new CaseObjectMeta(new CaseObject());
        caseObjectMeta.setStateId(CrmConstants.State.CREATED);
        caseObjectMeta.setImportance(En_ImportanceLevel.BASIC);
        caseObjectMeta.setInitiatorCompany(policyService.getUserCompany());

        return caseObjectMeta;
    }
    private void fillImportanceSelector(Long id) {
        issueMetaView.fillImportanceOptions(new ArrayList<>());
        companyService.getImportanceLevels(id, new FluentCallback<List<En_ImportanceLevel>>()
                .withSuccess(importanceLevelList -> {
                    issueMetaView.fillImportanceOptions(importanceLevelList);
                    checkImportanceSelectedValue(importanceLevelList);
                }));
    }

    private void checkImportanceSelectedValue(List<En_ImportanceLevel> importanceLevels) {
        if (!importanceLevels.contains(issueMetaView.importance().getValue())){
            issueMetaView.importance().setValue(null);
        }
    }

    private CaseObject fillCaseCreateRequest(CaseObject caseObject) {
        Set<Person> caseMetaNotifiers = issueMetaView.getCaseMetaNotifiers();

        caseObject.setName(view.name().getValue());
        caseObject.setInfo(view.description().getValue());
        caseObject.setPrivateCase(view.isPrivate().getValue());
        caseObject.setStateId(issueMetaView.state().getValue().getId());
        caseObject.setImpLevel(issueMetaView.importance().getValue().getId());
        caseObject.setPauseDate(issueMetaView.pauseDate().getValue() == null ? null : issueMetaView.pauseDate().getValue().getTime());

        caseObject.setInitiatorCompany(issueMetaView.getCompany());
        caseObject.setInitiator(issueMetaView.getInitiator());
        caseObject.setProduct(DevUnit.fromProductShortView(issueMetaView.product().getValue()));
        caseObject.setManager(issueMetaView.getManager());
        caseObject.setNotifiers(caseMetaNotifiers);
        caseObject.setPlatformId(issueMetaView.platform().getValue() == null ? null : issueMetaView.platform().getValue().getId());
        caseObject.setAttachmentExists(!isEmpty(view.attachmentsContainer().getAll()));
        caseObject.setAttachments(new ArrayList<>(view.attachmentsContainer().getAll()));
        caseObject.setManagerCompanyId(issueMetaView.getManagerCompany().getId());

        if (policyService.hasPrivilegeFor(En_Privilege.ISSUE_WORK_TIME_VIEW) && policyService.personBelongsToHomeCompany()) {
            caseObject.setTimeElapsed(issueMetaView.getTimeElapsed());
            caseObject.setTimeElapsedType(issueMetaView.timeElapsedType().getValue() == null ? En_TimeElapsedType.NONE : issueMetaView.timeElapsedType().getValue());
        }

        createRequest.setTimeElapsed(issueMetaView.getTimeElapsed());
        createRequest.setTimeElapsedType(issueMetaView.timeElapsedType().getValue());

        return caseObject;
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
        if (isCustomerWithAutoOpenIssues(currentCompany) && issueMetaView.product().getValue() == null) {
            fireEvent(new NotifyEvents.Show(lang.errProductNotSelected(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if (issueMetaView.getCompany() == null) {
            fireEvent(new NotifyEvents.Show(lang.errSaveIssueNeedSelectCompany(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if (issueMetaView.getManager() == null && isStateWithRestrictions(issueMetaView.state().getValue().getId())) {
            fireEvent(new NotifyEvents.Show(lang.errSaveIssueNeedSelectManager(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if (issueMetaView.product().getValue() == null && isStateWithRestrictions(issueMetaView.state().getValue().getId())) {
            fireEvent(new NotifyEvents.Show(lang.errProductNotSelected(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if (!isPauseDateValid(issueMetaView.state().getValue().getId(), issueMetaView.pauseDate().getValue() == null ? null : issueMetaView.pauseDate().getValue().getTime())) {
            fireEvent(new NotifyEvents.Show(lang.errPauseDateError(), NotifyEvents.NotifyType.ERROR));
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
        this.subscriptionsListEmptyMessage = emptyMessage;

        if (isEmpty(subscriptionsList)) return subscriptionsListEmptyMessage;

        List<String> subscriptionsBasedOnPrivacyList = subscriptionsList.stream()
                .map(CompanySubscription::getEmail)
                .filter(mail -> !view.isPrivate().getValue() || CompanySubscription.isProteiRecipient(mail))
                .distinct()
                .collect( Collectors.toList());

        return isEmpty(subscriptionsBasedOnPrivacyList)
                ? subscriptionsListEmptyMessage
                : String.join(", ", subscriptionsBasedOnPrivacyList);
    }

    private List<CompanySubscription> filterByPlatformAndProduct(List<CompanySubscription> subscriptionsList) {
        this.subscriptionsList = subscriptionsList;

        if (isEmpty(subscriptionsList)) return subscriptionsList;

        return subscriptionsList.stream()
                .filter(companySubscription -> (companySubscription.getProductId() == null || Objects.equals(issueMetaView.product().getValue() == null ? null : issueMetaView.product().getValue().getId(), companySubscription.getProductId()))
                        && (companySubscription.getPlatformId() == null || Objects.equals(issueMetaView.platform().getValue() == null ? null : issueMetaView.platform().getValue().getId(), companySubscription.getPlatformId())))
                .collect( Collectors.toList());
    }

    private String transliteration(String input) {
        return TransliterationUtils.transliterate(input, LocaleInfo.getCurrentLocale().getLocaleName());
    }

    private void initiatorSelectorAllowAddNew(Long companyId) {
        issueMetaView.initiatorSelectorAllowAddNew(false);

        if (companyId == null) {
            return;
        }

        if (!policyService.hasPrivilegeFor(En_Privilege.CONTACT_CREATE)) {
            return;
        }

        homeCompanyService.isHomeCompany(companyId, result -> issueMetaView.initiatorSelectorAllowAddNew(!result));
    }

    private boolean isStateWithRestrictions(long caseStateId) {
        return CrmConstants.State.CREATED != caseStateId &&
                CrmConstants.State.CANCELED != caseStateId;
    }

    private boolean makePreviewDisplaying( String key ) {
        return Boolean.parseBoolean( localStorageService.getOrDefault( ISSUE_CREATE_PREVIEW_DISPLAYED + "_" + key, "false" ) );
    }

    private boolean isSystemScope() {
        return policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_CREATE);
    }

    private boolean isPauseDateValid(long currentStateId, Long pauseDate) {
        if (CrmConstants.State.PAUSED != currentStateId) {
            return true;
        }

        if (pauseDate != null && pauseDate > System.currentTimeMillis()) {
            return true;
        }

        return false;
    }

    private void setPlatformVisibility(AbstractIssueMetaView issueMetaView, boolean isVisible) {
        issueMetaView.platformVisibility().setVisible(isVisible);
        issueMetaView.setInitiatorBorderBottomVisible(!isVisible);
    }

    private void setManagerCompanyEnabled(AbstractIssueMetaView issueMetaView, Long stateId) {
        issueMetaView.managerCompanyEnabled().setEnabled(policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_EDIT) && stateId == CrmConstants.State.CUSTOMER_RESPONSIBILITY);
    }

    private void setCreatedCaseState(CaseState createdCaseState) {
        this.createdCaseState = createdCaseState;
    }

    private void updateProductsFilter(final AbstractIssueMetaView issueMetaView, Long companyId, Long platformId) {
        if (platformId != null) {
            issueMetaView.updateProductsByPlatformIds(new HashSet<>(Collections.singleton(platformId)));
        } else {
            requestPlatforms(companyId, platformOptions -> updateProductsFilter(issueMetaView, toSet(emptyIfNull(platformOptions), PlatformOption::getId)));
        }
    }

    private void updateProductsFilter(final AbstractIssueMetaView issueMetaView, Set<Long> platformIds) {
        if (isEmpty(platformIds)) {
            issueMetaView.updateProductsByPlatformIds(null);
            issueMetaView.productEnabled().setEnabled(false);
        } else {
            issueMetaView.updateProductsByPlatformIds(platformIds);
            issueMetaView.productEnabled().setEnabled(isProductEnabled(currentCompany));
        }
    }

    private boolean isProductEnabled(Company company) {
        if (policyService.hasPrivilegeFor(En_Privilege.ISSUE_PRODUCT_EDIT)) {
            return true;
        }

        if (isCompanyWithAutoOpenIssues(company)) {
            return true;
        }

        return false;
    }

    private boolean isCustomerWithAutoOpenIssues(Company company) {
        if (hasSystemScopeForEdit()) {
            return false;
        }

        if (!isCompanyWithAutoOpenIssues(company)) {
            return false;
        }

        return true;
    }

    private boolean hasSystemScopeForEdit() {
        return policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_EDIT);
    }

    private boolean isCompanyWithAutoOpenIssues(Company company) {
        return Boolean.TRUE.equals(company.getAutoOpenIssue());
    }

    private void resetProduct(AbstractIssueMetaView issueMetaView) {
        issueMetaView.product().setValue(null);
        onProductChanged();
    }

    private void setCurrentCompany(Company company) {
        this.currentCompany = company;
    }

    @Inject
    Lang lang;
    @Inject
    TextRenderControllerAsync textRenderController;
    @Inject
    LocalStorageService localStorageService;
    @Inject
    CaseStateFilterProvider caseStateFilter;

    @Inject
    AbstractIssueCreateView view;
    @Inject
    AbstractIssueMetaView issueMetaView;
    @Inject
    SLAControllerAsync slaService;

    @Inject
    PolicyService policyService;
    @Inject
    HomeCompanyService homeCompanyService;
    @Inject
    AttachmentServiceAsync attachmentService;
    @Inject
    CompanyControllerAsync companyService;
    @Inject
    IssueControllerAsync issueService;
    @Inject
    SiteFolderControllerAsync siteFolderController;
    @Inject
    CaseStateControllerAsync caseStateController;
    @Inject
    ProductModel productModel;
    @Inject
    ProductWithChildrenModel productWithChildrenModel;

    private boolean saving;
    private AppEvents.InitDetails init;
    private List<CompanySubscription> subscriptionsList;
    private String subscriptionsListEmptyMessage;
    private CaseObjectCreateRequest createRequest;
    private List<ProjectSla> slaList = new ArrayList<>();
    private AbstractCaseTagListActivity tagListActivity;
    private CaseState createdCaseState;
    private Company currentCompany;
    private static final En_CaseType ISSUE_CASE_TYPE = En_CaseType.CRM_SUPPORT;
}
