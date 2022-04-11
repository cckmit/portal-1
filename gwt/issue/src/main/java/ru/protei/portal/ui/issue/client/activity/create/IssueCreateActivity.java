package ru.protei.portal.ui.issue.client.activity.create;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.annotation.ContextAware;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.PlatformQuery;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.util.TransliterationUtils;
import ru.protei.portal.core.model.util.UiResult;
import ru.protei.portal.core.model.view.*;
import ru.protei.portal.ui.common.client.activity.casetag.taglist.AbstractCaseTagListActivity;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.ConfigStorage;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.En_IssueValidationResultLang;
import ru.protei.portal.ui.common.client.lang.En_ResultStatusLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.*;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyModel;
import ru.protei.portal.ui.common.client.widget.selector.company.CustomerCompanyModel;
import ru.protei.portal.ui.common.client.widget.selector.company.SubcontractorCompanyModel;
import ru.protei.portal.ui.common.client.widget.selector.product.ProductModel;
import ru.protei.portal.ui.common.client.widget.selector.product.ProductWithChildrenModel;
import ru.protei.portal.ui.common.client.widget.uploader.impl.AttachmentUploader;
import ru.protei.portal.ui.common.client.widget.uploader.impl.PasteInfo;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
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

import static ru.protei.portal.core.model.helper.CaseCommentUtils.addImageInMessage;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.util.CrmConstants.SOME_LINKS_NOT_SAVED;
import static ru.protei.portal.ui.common.client.common.UiConstants.ISSUE_CREATE_PREVIEW_DISPLAYED;
import static ru.protei.portal.ui.common.client.util.AttachmentUtils.getRemoveErrorHandler;


/**
 * Активность создания обращения
 */
public abstract class IssueCreateActivity implements AbstractIssueCreateActivity, AbstractIssueMetaActivity {
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
                view.attachmentsListContainer().add(attachment);

                view.attachmentsVisibility().setVisible(!view.attachmentsListContainer().isEmpty());
                view.setCountOfAttachments(size(view.attachmentsListContainer().getAll()));
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

        companyModel.showDeprecated(false);
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        Company userCompany = event.profile.getCompany();
        customerCompanyModel.setSubcontractorId(userCompany.getId());
        customerCompanyModel.setActive(true);
        subcontractorCompanyModel.setCompanyId(userCompany.getId());
        subcontractorCompanyModel.setActive(true);
        issueMetaView.setCompanyModel(isSubcontractorCompany(userCompany) ? customerCompanyModel : companyModel);
        issueMetaView.setManagerCompanyModel(event.profile.hasSystemScopeForPrivilege(En_Privilege.ISSUE_CREATE) ? subcontractorCompanyModel : companyModel);
    }

    @Event
    public void onInit(AppEvents.InitDetails init) {
        this.init = init;
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(IssueEvents.Create event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.ISSUE_EDIT)) {
            fireEvent(new ErrorPageEvents.ShowForbidden(init.parent));
            return;
        }

        placeView(init.parent, view);

        subscriptionsList = null;
        subscriptionsListEmptyMessage = null;

        if (createRequest == null) {
            createRequest = new CaseObjectCreateRequest(initCaseObject());
        }

        fillView(createRequest);
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
        issueController.createIssue(createRequest, new FluentCallback<UiResult<Long>>()
                .withError(throwable -> {
                    if (throwable instanceof RequestFailedException && 
                            ((RequestFailedException)throwable).status == En_ResultStatus.VALIDATION_ERROR) {
                        RequestFailedException rf = (RequestFailedException)throwable;
                        fireEvent(new NotifyEvents.Show( resultStatusLang.getMessage(rf.status) + ": " +
                                validationResultLang.getMessage(rf.issueValidationResult), 
                                NotifyEvents.NotifyType.ERROR));
                    } else {
                        defaultErrorHandler.accept(throwable);
                    }
                    unlockSave();
                })
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
        attachmentController.removeAttachmentEverywhere(En_CaseType.CRM_SUPPORT, null, attachment.getId(), new FluentCallback<Long>()
                .withError(getRemoveErrorHandler(this, lang))
                .withSuccess(result -> {
                    view.attachmentsListContainer().remove(attachment);
                    view.setCountOfAttachments(size(view.attachmentsListContainer().getAll()));
                    view.attachmentsVisibility().setVisible(!view.attachmentsListContainer().isEmpty());
                })
        );
    }

    @Override
    public void onInitiatorCompanyChanged() {
        Company companyOption = issueMetaView.getCompany();

        fillImportanceSelector(companyOption.getId());

        fillInitiatorValue(issueMetaView, companyOption);

        companyController.getCompanyOmitPrivileges(companyOption.getId(), new FluentCallback<Company>()
                .withSuccess(company -> {
                    setCurrentCompany(company);
                    requestPlatforms(company.getId(), platformOptions -> {
                                fillPlatformValue(issueMetaView, platformOptions);

                                if (isCompanyWithAutoOpenIssues(company)) {
                                    issueMetaView.product().setValue(null);
                                }

                                updateProductsFilter(issueMetaView.platform().getValue(), platformOptions, company);

                                requestSla(
                                        issueMetaView.platform().getValue() == null ? null : issueMetaView.platform().getValue().getId(),
                                        slaList -> fillSla(getSlaByImportanceLevel(slaList, issueMetaView.importance().getValue()))
                                );

                                updateSubscriptions(issueMetaView.getManagerCompany().getId(), companyOption.getId());
                            }
                    );
                })
        );

        issueMetaView.setPlatformFilter(platformOption -> companyOption.getId().equals(platformOption.getCompanyId()));

        updateCompanyCaseStates(companyOption.getId());

        subcontractorCompanyModel.setCompanyId(companyOption.getId());
    }

    @Override
    public void onCreateContactClicked() {
        if (issueMetaView.getCompany() == null) {
            return;
        }

        createRequest.setCaseObject(fillCaseCreateRequest(createRequest.getCaseObject()));

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
        fillSla(getSlaByImportanceLevel(slaList, issueMetaView.importance().getValue()));
    }

    @Override
    public void onPauseDateChanged() {
        issueMetaView.setPauseDateValid(
                isPauseDateValid(
                        issueMetaView.state().getValue().getId(),
                        issueMetaView.pauseDate().getValue() == null ? null : issueMetaView.pauseDate().getValue().getTime())
        );
    }

    @Override
    public void onStateChange() {
        issueMetaView.pauseDate().setValue(null);
        issueMetaView.pauseDateContainerVisibility().setVisible(CrmConstants.State.PAUSED == issueMetaView.state().getValue().getId());
        boolean stateValid = isPauseDateValid(issueMetaView.state().getValue().getId(), issueMetaView.pauseDate().getValue() == null ? null : issueMetaView.pauseDate().getValue().getTime());
        issueMetaView.setPauseDateValid(stateValid);

        issueMetaView.setAutoCloseVisible(!isCustomer() && issueMetaView.state().getValue().getId() == CrmConstants.State.TEST_CUST);

        updateProductMandatory(issueMetaView, isCompanyWithAutoOpenIssues(issueMetaView.getCompany()));
        updateManagerMandatory(issueMetaView);

        if (issueMetaView.autoClose().getValue() && issueMetaView.state().getValue().getId() != CrmConstants.State.TEST_CUST) {
            resetAutoCloseAndDeadline(issueMetaView);
        }
    }

    @Override
    public void onProductChanged() {
        setSubscriptionEmails(getSubscriptionsBasedOnPrivacy(filterByPlatformAndProduct(subscriptionsList), subscriptionsListEmptyMessage));
    }

    @Override
    public void onPlatformChanged() {
        requestSla(
                issueMetaView.platform().getValue() == null ? null : issueMetaView.platform().getValue().getId(),
                slaList -> fillSla(getSlaByImportanceLevel(slaList, issueMetaView.importance().getValue()))
        );

        if (isCompanyWithAutoOpenIssues(currentCompany)) {
            issueMetaView.product().setValue(null);
            updateProductsFilter(issueMetaView, issueMetaView.getCompany().getId(), issueMetaView.platform().getValue() == null ? null : issueMetaView.platform().getValue().getId());
        }

        setSubscriptionEmails(getSubscriptionsBasedOnPrivacy(filterByPlatformAndProduct(subscriptionsList), subscriptionsListEmptyMessage));
    }

    @Override
    public void onManagerChanged() {
        updateProductMandatory(issueMetaView, isCompanyWithAutoOpenIssues(issueMetaView.getCompany()));
    }

    @Override
    public void onManagerCompanyChanged() {
        issueMetaView.setManager(null);
        issueMetaView.updateManagersCompanyFilter(issueMetaView.getManagerCompany().getId());

        updateSubscriptions(issueMetaView.getManagerCompany().getId(), issueMetaView.getCompany().getId());
    }

    @Override
    public void onPlansChanged() {
        createRequest.setPlans(issueMetaView.ownerPlans().getValue());
    }

    @Override
    public void onFavoriteStateChanged() {
        view.setFavoriteButtonActive(!view.isFavoriteButtonActive());
    }

    @Override
    public void onAutoCloseChanged() {
        if (issueMetaView.autoClose().getValue()) {
            Date now = new Date();
            CalendarUtil.addDaysToDate(now, configStorage.getConfigData().autoCloseDefaultDeadline);
            issueMetaView.deadline().setValue(now);
        } else {
            issueMetaView.deadline().setValue(null);
        }
    }

    @Override
    public void onDeadlineChanged() {
        issueMetaView.setDeadlineValid(
                isDeadlineFieldValid(issueMetaView.isDeadlineEmpty(), issueMetaView.deadline().getValue()));
    }

    private void placeView(HasWidgets parent, AbstractIssueCreateView view) {
        parent.clear();
        parent.add(view.asWidget());
        Window.scrollTo(0, 0);
    }

    private void updateSubscriptions(Long... companyIds) {
        companyController.getCompanyWithParentCompanySubscriptions(
                new HashSet<>(Arrays.asList(companyIds)),
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

    private void fillView(CaseObjectCreateRequest createRequest) {
        CaseObject caseObject = createRequest.getCaseObject();

        view.privacyVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_PRIVACY_VIEW));
        view.isPrivate().setValue(caseObject.isPrivateCase());

        view.name().setValue(caseObject.getName());
        view.description().setValue(caseObject.getInfo());
        view.setDescriptionPreviewAllowed(makePreviewDisplaying(AbstractIssueEditView.DESCRIPTION));
        view.setFavoriteButtonActive(Boolean.TRUE.equals(caseObject.isFavorite()));

        fillMetaView(new CaseObjectMeta(caseObject), caseObject.getNotifiers(), caseObject.getTimeElapsedType(), createRequest.getPlans());

        fillAttachmentsContainer(view, caseObject.getAttachments());

        fireEvent(new CaseLinkEvents.Show(view.getLinksContainer()).withCaseType(En_CaseType.CRM_SUPPORT).withLinks(createRequest.getLinks()));
        fireEvent(new CaseTagEvents.ShowList(view.getTagsContainer(), En_CaseType.CRM_SUPPORT, createRequest.getTags(), false, a -> tagListActivity = a));

        view.saveVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_EDIT));
        unlockSave();
    }

    private void fillMetaView(CaseObjectMeta caseObjectMeta, Set<Person> notifiers, En_TimeElapsedType timeElapsedType, Set<PlanOption> planOptions) {
        issueMetaView.companyEnabled().setEnabled(true);
        issueMetaView.initiatorEnabled().setEnabled(!policyService.isSubcontractorCompany());
        issueMetaView.productEnabled().setEnabled(isProductEnabled(caseObjectMeta.getInitiatorCompany()));
        issueMetaView.caseSubscriptionContainer().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_MANAGER_VIEW));
        issueMetaView.stateEnabled().setEnabled(true);
        issueMetaView.timeElapsedContainerVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_WORK_TIME_VIEW));
        issueMetaView.timeElapsedEditContainerVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_EDIT));
        issueMetaView.timeElapsedHeaderVisibility().setVisible(false);
        setPlatformVisibility(issueMetaView, policyService.hasPrivilegeFor(En_Privilege.ISSUE_PLATFORM_EDIT));
        issueMetaView.setStateWorkflow(En_CaseStateWorkflow.NO_WORKFLOW);

        issueMetaView.setCaseMetaNotifiers(notifiers);

        importanceService.getImportanceLevel(CrmConstants.ImportanceLevel.BASIC, new FluentCallback<ImportanceLevel>()
                .withSuccess(value -> {
                    issueMetaView.importance().setValue(value);
                    requestSla(caseObjectMeta.getPlatformId(), slaList -> fillSla(getSlaByImportanceLevel(slaList, value)));
                })
        );

        fillImportanceSelector(caseObjectMeta.getInitiatorCompanyId());
        fillStateSelector(caseObjectMeta.getStateId());
        issueMetaView.pauseDate().setValue(caseObjectMeta.getPauseDate() == null ? null : new Date(caseObjectMeta.getPauseDate()));
        issueMetaView.pauseDateContainerVisibility().setVisible(CrmConstants.State.PAUSED == caseObjectMeta.getStateId());
        issueMetaView.setPauseDateValid(isPauseDateValid(caseObjectMeta.getStateId(), caseObjectMeta.getPauseDate()));
        issueMetaView.setCompany(caseObjectMeta.getInitiatorCompany());

        setCurrentCompany(caseObjectMeta.getInitiatorCompany());
        subcontractorCompanyModel.setCompanyId(caseObjectMeta.getInitiatorCompanyId());

        issueMetaView.setInitiator(caseObjectMeta.getInitiator());

        boolean isCompanyWithAutoOpenIssues = isCompanyWithAutoOpenIssues(caseObjectMeta.getInitiatorCompany());

        updateProductModel(issueMetaView, isCompanyWithAutoOpenIssues);
        updateProductMandatory(issueMetaView, isCompanyWithAutoOpenIssues);
        issueMetaView.product().setValue(ProductShortView.fromProduct(caseObjectMeta.getProduct()));

        issueMetaView.setTimeElapsed(caseObjectMeta.getTimeElapsed());
        fillManagerInfoContainer(issueMetaView, caseObjectMeta);

        issueMetaView.slaContainerVisibility().setVisible(isSystemScope());

        issueMetaView.setPlanCreatorId(policyService.getProfile().getId());

        if (!policyService.hasPrivilegeFor(En_Privilege.ISSUE_PLAN_EDIT)) {
            issueMetaView.ownerPlansContainerVisibility().setVisible(false);
            issueMetaView.otherPlansContainerVisibility().setVisible(false);
            issueMetaView.setPlansLabelVisible(false);
        } else {
            issueMetaView.setPlansLabelVisible(true);
            fillOwnerPlansContainer(issueMetaView, planOptions, policyService.getProfile());
            fillOtherPlansContainer(issueMetaView, planOptions, policyService.getProfile());
        }

        issueMetaView.setTimeElapsedType(timeElapsedType == null ? En_TimeElapsedType.NONE : timeElapsedType);

        Company initiatorCompany = caseObjectMeta.getInitiatorCompany();

        fillImportanceSelector(initiatorCompany.getId());

        fillInitiatorValue(issueMetaView, initiatorCompany);

        companyController.getCompanyOmitPrivileges(initiatorCompany.getId(), new FluentCallback<Company>()
                .withSuccess(company -> {
                    setCurrentCompany(company);
                    requestPlatforms(company.getId(), platforms -> {
                                fillPlatformValue(issueMetaView, caseObjectMeta, platforms);

                                updateProductsFilter(issueMetaView.platform().getValue(), platforms, company);

                                requestSla(
                                        issueMetaView.platform().getValue() == null ? null : issueMetaView.platform().getValue().getId(),
                                        slaList -> fillSla(getSlaByImportanceLevel(slaList, issueMetaView.importance().getValue()))
                                );

                                updateSubscriptions(caseObjectMeta.getManagerCompanyId(), caseObjectMeta.getInitiatorCompanyId());
                            }
                    );
                })
        );

        issueMetaView.setPlatformFilter(platformOption -> caseObjectMeta.getInitiatorCompanyId().equals(platformOption.getCompanyId()));

        updateCompanyCaseStates(initiatorCompany.getId());

        setCustomerVisibility(issueMetaView, policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_CREATE));

        issueMetaView.setAutoCloseVisible(!isCustomer() && caseObjectMeta.getStateId() == CrmConstants.State.TEST_CUST);
        issueMetaView.autoClose().setValue(null);

        issueMetaView.deadline().setValue(caseObjectMeta.getDeadline() != null ? new Date(caseObjectMeta.getDeadline()) : null);
        issueMetaView.setDeadlineValid(isDeadlineValid(caseObjectMeta.getDeadline()));
        issueMetaView.workTrigger().setValue(caseObjectMeta.getWorkTrigger());
    }

    private void fillInitiatorValue(final AbstractIssueMetaView issueMetaView, Company initiatorCompany) {
        issueMetaView.setInitiatorFilter(initiatorCompany.getId());
        initiatorSelectorAllowAddNew(initiatorCompany.getId());
        updateInitiatorInfo(policyService.getProfile(), initiatorCompany.getId());
    }

    private void fillPlatformValue(final AbstractIssueMetaView issueMetaView, CaseObjectMeta caseObjectMeta, List<PlatformOption> platforms) {
        if (caseObjectMeta.getPlatformId() == null) {
            fillPlatformValue(issueMetaView, platforms);
        } else {
            issueMetaView.platform().setValue(new PlatformOption(caseObjectMeta.getPlatformName(), caseObjectMeta.getPlatformId(), caseObjectMeta.getInitiatorCompanyId()));
        }
    }

    private void fillPlatformValue(final AbstractIssueMetaView issueMetaView, List<PlatformOption> platforms) {
        if (platforms != null && platforms.size() == 1) {
            issueMetaView.platform().setValue(platforms.get(0));
        } else {
            issueMetaView.platform().setValue(null);
        }
    }

    private void updateProductsFilter(PlatformOption selectedPlatform, List<PlatformOption> platforms, Company company) {
        boolean isCompanyWithAutoOpenIssues = isCompanyWithAutoOpenIssues(company);

        updateProductModel(issueMetaView, isCompanyWithAutoOpenIssues);
        updateProductMandatory(issueMetaView, isCompanyWithAutoOpenIssues);

        if (!isCompanyWithAutoOpenIssues) {
            issueMetaView.updateProductsByPlatformIds(new HashSet<>());
        } else {
            updateProductsFilter(
                    issueMetaView,
                    selectedPlatform == null ?
                            toSet(emptyIfNull(platforms), PlatformOption::getId) :
                            new HashSet<>(Collections.singleton(issueMetaView.platform().getValue().getId()))
            );
        }
    }

    private void updateCompanyCaseStates(Long companyId) {
        companyController.getCompanyCaseStates(companyId, new ShortRequestCallback<List<CaseState>>()
                .setOnSuccess(caseStates -> {
                    issueMetaView.setStateFilter(caseStateFilter.makeFilter(caseStates));
                    fireEvent(new CaseStateEvents.UpdateSelectorOptions());
                })
        );
    }

    private void updateInitiatorInfo(Profile profile, Long currentCompanyId) {
        if (!Objects.equals(profile.getCompany().getId(), currentCompanyId)) {
            issueMetaView.setInitiator(null);
        } else {
            Person initiator = Person.fromPersonFullNameShortView(new PersonShortView(transliteration(profile.getFullName()), profile.getId(), profile.isFired()));
            issueMetaView.setInitiator(initiator);
        }
    }

    private void fillAttachmentsContainer(AbstractIssueCreateView view, List<Attachment> attachments) {
        boolean isAttachmentsEmpty = isEmpty(attachments);

        view.attachmentsListContainer().clear();
        view.attachmentsVisibility().setVisible(!isAttachmentsEmpty);
        view.setCountOfAttachments(size(attachments));

        if (!isAttachmentsEmpty) {
            view.attachmentsListContainer().add(attachments);
        }
    }

    private void fillOwnerPlansContainer(final AbstractIssueMetaView issueMetaView, Set<PlanOption> plans, Profile profile) {
        if (!profile.hasPrivilegeFor(En_Privilege.PLAN_EDIT)) {
            issueMetaView.ownerPlansContainerVisibility().setVisible(false);
            return;
        }

        issueMetaView.ownerPlansContainerVisibility().setVisible(true);
        issueMetaView.ownerPlans().setValue(getOwnerPlans(plans, profile.getId()));
        issueMetaView.setPlanCreatorId(profile.getId());
    }

    private void fillOtherPlansContainer(final AbstractIssueMetaView issueMetaView, Set<PlanOption> plans, Profile profile) {
        Set<PlanOption> otherPlans = getOtherPlans(plans, profile.getId());

        issueMetaView.otherPlansContainerVisibility().setVisible(true);
        issueMetaView.setOtherPlans(otherPlans.stream().map(PlanOption::getDisplayText).collect(Collectors.joining(", ")));
    }

    private Set<PlanOption> getOwnerPlans(Set<PlanOption> plans, Long personId) {
        return stream(plans)
                .filter(plan -> personId.equals(plan.getCreatorId()))
                .collect(Collectors.toSet());
    }

    private Set<PlanOption> getOtherPlans(Set<PlanOption> plans, Long personId) {
        return stream(plans)
                .filter(plan -> !personId.equals(plan.getCreatorId()))
                .collect(Collectors.toSet());
    }


    private void fillManagerInfoContainer(final AbstractIssueMetaView issueMetaView, final CaseObjectMeta caseObjectMeta) {
        issueMetaView.managerCompanyEnabled().setEnabled(policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_CREATE));
        issueMetaView.managerEnabled().setEnabled(policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_CREATE) || policyService.isSubcontractorCompany());

        if (caseObjectMeta.getManagerCompanyId() != null) {
            issueMetaView.setManagerCompany(new EntityOption(caseObjectMeta.getManagerCompanyName(), caseObjectMeta.getManagerCompanyId()));
            issueMetaView.updateManagersCompanyFilter(caseObjectMeta.getManagerCompanyId());
        } else {
            if (policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_CREATE) || policyService.isSubcontractorCompany()) {
                issueMetaView.setManagerCompany(policyService.getUserCompany().toEntityOption());
                issueMetaView.updateManagersCompanyFilter(policyService.getUserCompany().getId());
            } else {
                homeCompanyService.getHomeCompany(CrmConstants.Company.HOME_COMPANY_ID, company -> {
                    issueMetaView.setManagerCompany(company);
                    issueMetaView.updateManagersCompanyFilter(company.getId());
                });
            }
        }

        issueMetaView.setManager(caseObjectMeta.getManager());
        updateManagerMandatory(issueMetaView);
    }

    private void requestSla(Long platformId, Consumer<List<ProjectSla>> slaConsumer) {
        if (!isSystemScope()) {
            return;
        }

        if (platformId == null) {
            slaList.clear();
            issueMetaView.setValuesContainerWarning(true);
            issueMetaView.setSlaTimesContainerTitle(lang.projectSlaDefaultValues());
            slaConsumer.accept(slaList);
            return;
        }

        slaController.getSlaByPlatformId(platformId, new FluentCallback<List<ProjectSla>>()
                .withSuccess(result -> {
                    if (result.isEmpty()) {
                        slaList.clear();
                    } else {
                        slaList = result;
                    }

                    issueMetaView.setValuesContainerWarning(result.isEmpty());
                    issueMetaView.setSlaTimesContainerTitle(result.isEmpty() ? lang.projectSlaDefaultValues() : lang.projectSlaSetValuesByManager());
                    slaConsumer.accept(slaList);
                })
        );
    }

    private ProjectSla getSlaByImportanceLevel(List<ProjectSla> slaList, final ImportanceLevel importanceLevel) {
        if (CollectionUtils.isEmpty(slaList)) {
            return makeDefaultProjectSla(importanceLevel);
        }

        return slaList
                .stream()
                .filter(sla -> Objects.equals(importanceLevel.getId(), sla.getImportanceLevelId()))
                .findAny()
                .orElse(new ProjectSla());
    }

    private void fillSla(ProjectSla sla) {
        issueMetaView.slaReactionTime().setTime(sla.getReactionTime());
        issueMetaView.slaTemporarySolutionTime().setTime(sla.getTemporarySolutionTime());
        issueMetaView.slaFullSolutionTime().setTime(sla.getFullSolutionTime());
    }

    private CaseObject initCaseObject() {
        CaseObject caseObject = new CaseObject();
        caseObject.setStateId(CrmConstants.State.CREATED);
        caseObject.setImpLevel(CrmConstants.ImportanceLevel.BASIC);
        caseObject.setPrivateCase(true);
        if (policyService.isSubcontractorCompany()) {
            homeCompanyService.getHomeCompany(CrmConstants.Company.HOME_COMPANY_ID, company -> caseObject.setInitiatorCompany(Company.fromEntityOption(company)));
        } else {
            caseObject.setInitiatorCompany(policyService.getUserCompany());
        }
        caseObject.setAutoClose(false);
        caseObject.setWorkTrigger(En_WorkTrigger.NONE);

        return caseObject;
    }

    private void fillImportanceSelector(Long id) {
        importanceService.getImportanceLevels(id, new FluentCallback<List<ImportanceLevel>>()
                .withSuccess(importanceLevelList -> {
                    issueMetaView.fillImportanceOptions(importanceLevelList);
                    checkImportanceSelectedValue(importanceLevelList);
                }));
    }

    private void checkImportanceSelectedValue(List<ImportanceLevel> importanceLevels) {
        if (!importanceLevels.contains(issueMetaView.importance().getValue())) {
            issueMetaView.importance().setValue(null);
        }
    }

    private void fillStateSelector(Long id) {
        issueMetaView.state().setValue(null);
        caseStateController.getCaseStateWithoutCompaniesOmitPrivileges(id, new FluentCallback<CaseState>()
                .withSuccess(caseState -> issueMetaView.state().setValue(caseState)));
    }

    private CaseObject fillCaseCreateRequest(CaseObject caseObject) {
        Set<Person> caseMetaNotifiers = issueMetaView.getCaseMetaNotifiers();

        caseObject.setName(view.name().getValue());
        caseObject.setInfo(view.description().getValue());
        caseObject.setPrivateCase(view.isPrivate().getValue());
        CaseState caseState = issueMetaView.state().getValue();
        caseObject.setStateId(caseState.getId());
        caseObject.setStateName(caseState.getState());
        caseObject.setImpLevel(issueMetaView.importance().getValue().getId());
        caseObject.setPauseDate(issueMetaView.pauseDate().getValue() == null ? null : issueMetaView.pauseDate().getValue().getTime());

        caseObject.setInitiatorCompany(issueMetaView.getCompany());
        caseObject.setInitiator(issueMetaView.getInitiator());
        caseObject.setProduct(DevUnit.fromProductShortView(issueMetaView.product().getValue()));
        caseObject.setManager(issueMetaView.getManager());
        caseObject.setNotifiers(caseMetaNotifiers);
        caseObject.setPlatformId(issueMetaView.platform().getValue() == null ? null : issueMetaView.platform().getValue().getId());
        caseObject.setPlatformName(issueMetaView.platform().getValue() == null ? null : issueMetaView.platform().getValue().getDisplayText());
        caseObject.setAttachmentExists(!isEmpty(view.attachmentsListContainer().getAll()));
        caseObject.setAttachments(new ArrayList<>(view.attachmentsListContainer().getAll()));
        caseObject.setManagerCompanyId(issueMetaView.getManagerCompany().getId());
        caseObject.setManagerCompanyName(issueMetaView.getManagerCompany().getDisplayText());
        if (policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_CREATE)) {
            caseObject.setDeadline(issueMetaView.deadline().getValue() != null? issueMetaView.deadline().getValue().getTime() : null);
            caseObject.setWorkTrigger(issueMetaView.workTrigger().getValue());
        }
        caseObject.setFavorite(view.isFavoriteButtonActive());

        if (policyService.hasPrivilegeFor(En_Privilege.ISSUE_WORK_TIME_VIEW) && policyService.personBelongsToHomeCompany()) {
            caseObject.setTimeElapsed(issueMetaView.getTimeElapsed());
            caseObject.setTimeElapsedType(issueMetaView.timeElapsedType().getValue() == null ? En_TimeElapsedType.NONE : issueMetaView.timeElapsedType().getValue());
        }

        createRequest.setTimeElapsed(issueMetaView.getTimeElapsed());
        createRequest.setTimeElapsedType(issueMetaView.timeElapsedType().getValue());

        caseObject.setAutoClose(issueMetaView.autoClose().getValue());

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
        if (isCompanyWithAutoOpenIssues(currentCompany) && issueMetaView.product().getValue() == null) {
            fireEvent(new NotifyEvents.Show(lang.errProductNotSelected(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if (issueMetaView.autoClose().getValue() && issueMetaView.deadline().getValue() == null) {
            fireEvent(new NotifyEvents.Show(lang.errDeadlineNotSelectedOnAutoClose(), NotifyEvents.NotifyType.ERROR));
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

        if (issueMetaView.getManager() != null && issueMetaView.product().getValue() == null) {
            fireEvent(new NotifyEvents.Show(lang.errProductNotSelected(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if (!isDeadlineFieldValid(issueMetaView.isDeadlineEmpty(), issueMetaView.deadline().getValue())) {
            fireEvent(new NotifyEvents.Show(lang.errDeadlineError(), NotifyEvents.NotifyType.ERROR));
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

    private boolean isDeadlineFieldValid(boolean isEmptyDeadlineField, Date date) {
        if (issueMetaView.autoClose().getValue() && date == null) {
            return false;
        }

        if (date == null) {
            return isEmptyDeadlineField;
        }

        return isDeadlineValid(date.getTime());
    }

    private boolean isDeadlineValid(Long date) {
        return date == null || date > System.currentTimeMillis();
    }

    private void setPlatformVisibility(AbstractIssueMetaView issueMetaView, boolean isVisible) {
        issueMetaView.platformVisibility().setVisible(isVisible);
        issueMetaView.setInitiatorBorderBottomVisible(!isVisible);
    }

    private void setCustomerVisibility(AbstractIssueMetaView issueMetaView, boolean isVisible) {
        issueMetaView.deadlineContainerVisibility().setVisible(isVisible);
        issueMetaView.workTriggerVisibility().setVisible(isVisible);
        issueMetaView.setProductBorderBottomVisible(!isVisible);
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

        if (policyService.isSubcontractorCompany()) {
            return true;
        }

        return false;
    }

    private boolean isCompanyWithAutoOpenIssues(Company company) {
        return Boolean.TRUE.equals(company.getAutoOpenIssue());
    }

    private void setCurrentCompany(Company company) {
        this.currentCompany = company;
    }

    private void updateProductModel(AbstractIssueMetaView metaView, boolean isCompanyWithAutoOpenIssues) {
        metaView.setProductModel(isCompanyWithAutoOpenIssues ? productWithChildrenModel : productModel);
    }

    private void updateProductMandatory(AbstractIssueMetaView metaView, boolean isCompanyWithAutoOpenIssues) {
        Long stateId = metaView.state().getValue() == null ? null : metaView.state().getValue().getId();
        boolean isStateWithRestrictions = stateId != null && isStateWithRestrictions(stateId);

        metaView.setProductMandatory(isCompanyWithAutoOpenIssues || metaView.getManager() != null || isStateWithRestrictions);
    }

    private void updateManagerMandatory(AbstractIssueMetaView metaView) {
        Long stateId = metaView.state().getValue() == null ? null : metaView.state().getValue().getId();
        boolean isStateWithRestrictions = stateId != null && isStateWithRestrictions(stateId);

        metaView.setManagerMandatory(isStateWithRestrictions);
    }

    private boolean isSubcontractorCompany(Company userCompany) {
        return userCompany.getCategory() == En_CompanyCategory.SUBCONTRACTOR;
    }

    private ProjectSla makeDefaultProjectSla(ImportanceLevel importanceLevel) {
        return new ProjectSla(importanceLevel.getId(), importanceLevel.getReactionTime(),
                importanceLevel.getTemporarySolutionTime(), importanceLevel.getFullSolutionTime());
    }

    private boolean isCustomer() {
        return !policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_VIEW);
    }

    public void resetAutoCloseAndDeadline(AbstractIssueMetaView metaView) {
        metaView.autoClose().setValue(false);
        metaView.deadline().setValue(null);
        metaView.setDeadlineValid(true);

    }

    @Inject
    Lang lang;
    @Inject
    LocalStorageService localStorageService;
    @Inject
    CaseStateFilterProvider caseStateFilter;

    @Inject
    AbstractIssueCreateView view;
    @Inject
    AbstractIssueMetaView issueMetaView;

    @Inject
    PolicyService policyService;
    @Inject
    HomeCompanyService homeCompanyService;

    @Inject
    TextRenderControllerAsync textRenderController;
    @Inject
    SLAControllerAsync slaController;
    @Inject
    AttachmentControllerAsync attachmentController;
    @Inject
    CompanyControllerAsync companyController;
    @Inject
    IssueControllerAsync issueController;
    @Inject
    SiteFolderControllerAsync siteFolderController;
    @Inject
    CaseStateControllerAsync caseStateController;

    @Inject
    ProductModel productModel;
    @Inject
    ProductWithChildrenModel productWithChildrenModel;

    @Inject
    CompanyModel companyModel;
    @Inject
    SubcontractorCompanyModel subcontractorCompanyModel;
    @Inject
    CustomerCompanyModel customerCompanyModel;

    @Inject
    ImportanceLevelControllerAsync importanceService;

    @Inject
    En_ResultStatusLang resultStatusLang;
    @Inject
    En_IssueValidationResultLang validationResultLang;
    @Inject
    DefaultErrorHandler defaultErrorHandler;
    @Inject
    ConfigStorage configStorage;

    @ContextAware
    CaseObjectCreateRequest createRequest;

    private boolean saving;
    private AppEvents.InitDetails init;
    private List<CompanySubscription> subscriptionsList;
    private String subscriptionsListEmptyMessage;
    private List<ProjectSla> slaList = new ArrayList<>();
    private AbstractCaseTagListActivity tagListActivity;
    private Company currentCompany;
    private static final En_CaseType ISSUE_CASE_TYPE = En_CaseType.CRM_SUPPORT;
}
