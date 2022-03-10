package ru.protei.portal.ui.project.client.activity.edit;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.dto.ProductDirectionInfo;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.util.UiResult;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.model.view.PlanOption;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.activity.commenthistory.AbstractCommentAndHistoryListView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CompanyControllerAsync;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.util.CrmConstants.SOME_LINKS_NOT_SAVED;
import static ru.protei.portal.ui.project.client.util.AccessUtil.*;
import static ru.protei.portal.core.model.util.CrmConstants.State.*;

/**
 * Активность карточки создания и редактирования проектов
 */
public abstract class ProjectEditActivity implements AbstractProjectEditActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails event) {
        this.initDetails = event;
    }

    @Event
    public void onShow (ProjectEvents.Edit event) {

        En_Privilege privilege = event.id == null
                ? En_Privilege.PROJECT_CREATE
                : En_Privilege.PROJECT_EDIT;
        if (getAccessType(policyService, privilege) == En_ProjectAccessType.NONE) {
            fireEvent(new ErrorPageEvents.ShowForbidden(initDetails.parent));
            return;
        }

        initDetails.parent.clear();
        Window.scrollTo(0, 0);

        if (event.id == null) {
            project = new Project();
            fillView( project );
        } else {
            requestProject(event.id, this::fillView);
        }

        initDetails.parent.add(view.asWidget());
    }

    @Override
    public void onSaveClicked() {
        if (!validateView()) {
            return;
        }

        fillProject(project);

        view.saveEnabled().setEnabled(false);

        regionService.saveProject(project, new FluentCallback<UiResult<Project>>()
                .withError(throwable -> {
                    view.saveEnabled().setEnabled(true);
                    defaultErrorHandler.accept(throwable);
                })
                .withSuccess(projectSaveResult -> {
                    view.saveEnabled().setEnabled(true);

                    if (SOME_LINKS_NOT_SAVED.equals(projectSaveResult.getMessage())) {
                        fireEvent(new NotifyEvents.Show(lang.caseLinkSomeNotAdded(), NotifyEvents.NotifyType.INFO));
                    }

                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new ProjectEvents.ChangeModel());
                    fireEvent(new ProjectEvents.Show(!isNew(project)));
                })
        );
    }

    @Event
    public void onAddLink(CaseLinkEvents.Added event) {
        if (PROJECT_CASE_TYPE.equals(event.caseType)) {
            project.addLink(event.caseLink);
        }
    }

    @Event
    public void onRemoveLink(CaseLinkEvents.Removed event) {
        if (PROJECT_CASE_TYPE.equals(event.caseType)) {
            project.getLinks().remove(event.caseLink);
        }
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new ProjectEvents.Show(!isNew(project)));
    }

    @Override
    public void onAddLinkClicked(IsWidget anchor) {
        fireEvent(new CaseLinkEvents.ShowLinkSelector(anchor, PROJECT_CASE_TYPE));
    }

    @Override
    public void onDirectionChanged() {
        final Set<ProductDirectionInfo> directions = view.directions().getValue();

        if (isEmpty(directions)) {
            view.productEnabled().setEnabled(false);
            view.updateProductModel(new HashSet<>());
            view.products().setValue(null);
        } else {
            view.productEnabled().setEnabled(true);
            view.updateProductModel(toSet(directions, ProductDirectionInfo::getId));
            view.products().setValue(
                    stream(view.products().getValue()).
                            filter(productShortView -> {
                                final Set<Long> ids = toSet(productShortView.getProductDirection(), ProductDirectionInfo::getId);
                                return stream(directions).anyMatch(direction ->
                                        ids.contains(direction.id));
                            })
                            .collect(Collectors.toSet())
            );
        }
    }

    @Override
    public void onProductChanged() {
        final Set<ProductShortView> currentComplexes = stream(view.products().getValue())
                .filter(info -> info.getType() == En_DevUnitType.COMPLEX && info.getProductDirection() != null)
                .collect(Collectors.toSet());
        Set<ProductShortView> addedComplex = new HashSet<>(currentComplexes);
        addedComplex.removeAll(selectedComplexes);
        if (isNotEmpty(addedComplex)) {
            final Set<ProductDirectionInfo> directions = view.directions().getValue();
            directions.addAll(stream(currentComplexes)
                    .flatMap(productShortView -> stream(productShortView.getProductDirection()))
                    .collect(Collectors.toSet()));
            view.directions().setValue(directions);
            onDirectionChanged();
        }
        selectedComplexes = currentComplexes;
    }

    @Override
    public void onStateChanged() {
        view.pauseDateContainerVisibility().setVisible( view.state().getValue().getId().equals(PAUSED) );
    }

    @Override
    public void onCompanyChanged() {
        fillSlaContainerByDefault(view.company().getValue() == null ? null : view.company().getValue().getId());
    }

    private boolean isNew(Project project) {
        return project.getId() == null;
    }

    private void requestProject(Long projectId, Consumer<Project> successAction) {
        regionService.getProject( projectId, new RequestCallback<Project>() {
            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onSuccess(Project project) {
                ProjectEditActivity.this.project = project;
                successAction.accept(project);
            }
        });
    }

    private void fillView(Project project) {
        view.setNumber( isNew( project ) ? null : project.getId().intValue() );
        view.name().setValue( isNew( project ) ? "" : project.getName());
        long stateId = isNew(project) ? UNKNOWN : project.getStateId();
        String stateName = isNew(project) ? "unknown" : project.getStateName();
        CaseState state = new CaseState(stateId, stateName);
        view.state().setValue( state );
        view.directions().setValue(isEmpty(project.getProductDirectionEntityOptionList())? null : toSet(project.getProductDirectionEntityOptionList(), option -> new ProductDirectionInfo(option)));
        view.productEnabled().setEnabled(isNotEmpty(project.getProductDirectionEntityOptionList()));
        view.team().setValue( project.getTeam() == null ? null : new HashSet<>( project.getTeam() ) );
        view.region().setValue( project.getRegion() );
        Company customer = project.getCustomer();
        view.company().setValue(customer == null ? null : customer.toEntityOption());
        view.companyEnabled().setEnabled(isNew( project ));
        view.description().setText(project.getDescription());

        view.products().setValue(new HashSet<>(emptyIfNull(project.getProductShortViewList())));
        selectedComplexes.addAll(stream(project.getProductShortViewList()).filter(product -> product.getType() == En_DevUnitType.COMPLEX).collect(Collectors.toSet()) );

        if (isNew( project )) view.setHideNullValue(true);
        view.customerType().setValue(project.getCustomerType());
        view.updateProductModel( toSet(project.getProductDirectionEntityOptionList(), EntityOption::getId));
        view.pauseDateContainerVisibility().setVisible( Objects.equals(project.getStateId(), PAUSED) );
        view.pauseDate().setValue( project.getPauseDate() == null ? null : new Date( project.getPauseDate() ) );

        if (customer != null && isNotEmpty(project.getProjectSlas())) {
            synchronizeProjectSla(
                    project.getProjectSlas(),
                    customer.getId(),
                    projectSlaList -> changeSlaContainerState(projectSlaList, true)
            );
        } else {
            fillSlaContainerByDefault(project.getCustomerId());
        }

        view.numberVisibility().setVisible( !isNew( project ) );

        view.getCommentsContainer().clear();
        view.showComments(!isNew( project ));
        view.getDocumentsContainer().clear();
        view.showDocuments(!isNew( project ));

        view.technicalSupportValidity().setValue(project.getTechnicalSupportValidity());

        view.plans().setValue(stream(project.getProjectPlans())
                .map(PlanOption::fromPlan)
                .collect(Collectors.toSet()));
        view.workCompletionDate().setValue(project.getWorkCompletionDate());
        view.purchaseDate().setValue(project.getPurchaseDate());

        fillCaseLinks(project);

        view.subcontractors().setValue(project.getSubcontractors() == null ? null :
                project.getSubcontractors().stream().map(Company::toEntityOption).collect(Collectors.toSet()));

        En_Privilege actionPrivilege = isNew(project)
                ? En_Privilege.PROJECT_CREATE
                : En_Privilege.PROJECT_EDIT;

        if (!isNew(project)) {
            view.getCommentsContainer().clear();
            view.getCommentsContainer().add(commentAndHistoryView.asWidget());
            CommentAndHistoryEvents.Show show = new CommentAndHistoryEvents.Show(
                commentAndHistoryView,
                project.getId(),
                En_CaseType.PROJECT,
                canAccessProject(policyService, actionPrivilege, project.getTeam()),
                project.getCreatorId()
            );
            show.isPrivateVisible = canAccessProjectPrivateElements(policyService, En_Privilege.PROJECT_VIEW, project.getTeam());
            show.isPrivateCase = false;
            show.isNewCommentEnabled = canAccessProject(policyService, actionPrivilege, project.getTeam());
            show.initiatorCompanyId = project.getCustomerId();
            show.isMentionEnabled = policyService.hasSystemScopeForPrivilege(En_Privilege.PROJECT_VIEW);
            fireEvent(show);
        }

        fireEvent(new ProjectEvents.ShowProjectDocuments(view.getDocumentsContainer(), this.project.getId()));

        view.saveVisibility().setVisible(canAccessProject(policyService, actionPrivilege, project.getTeam()));
        view.saveEnabled().setEnabled(true);

        view.setTechnicalSupportDateValid(true);
        view.setWorkCompletionDateValid(true);
        view.setPurchaseDateValid(true);
    }

    private void fillSlaContainerByDefault(Long companyId) {
        if (companyId == null) {
            changeSlaContainerState(null, false);
            return;
        }

        companyService.getCompanyImportanceItems(companyId, new FluentCallback<List<CompanyImportanceItem>>()
                .withSuccess(importanceLevels -> {
                    if (isEmpty(importanceLevels)) {
                        return;
                    }

                    changeSlaContainerState(createProjectSlaList(importanceLevels), true);
                })
        );
    }

    private void synchronizeProjectSla(List<ProjectSla> currentProjectSlaList, Long companyId, Consumer<List<ProjectSla>> projectSlaListConsumer) {
        companyService.getCompanyImportanceItems(companyId, new FluentCallback<List<CompanyImportanceItem>>()
                .withSuccess(companyImportanceItems ->
                        projectSlaListConsumer.accept(toList(companyImportanceItems, companyImportanceItem -> getProjectSla(currentProjectSlaList, companyImportanceItem))
                )
        ));
    }

    private List<ProjectSla> createProjectSlaList(List<CompanyImportanceItem> companyImportanceItems) {
        return toList(companyImportanceItems, companyImportanceItem ->
                new ProjectSla(companyImportanceItem.getImportanceLevelId(), companyImportanceItem.getImportanceCode())
        );
    }

    private void changeSlaContainerState(List<ProjectSla> projectSlaList, boolean isVisible) {
        view.slaInput().setValue(projectSlaList);
        view.slaVisibility().setVisible(isVisible);
    }

    private ProjectSla getProjectSla(List<ProjectSla> projectSlaList, CompanyImportanceItem companyImportanceItem) {
        return projectSlaList
                .stream()
                .filter(projectSla -> companyImportanceItem.getImportanceLevelId().equals(projectSla.getImportanceLevelId()))
                .findAny()
                .orElse(new ProjectSla(companyImportanceItem.getImportanceLevelId(), companyImportanceItem.getImportanceCode()));
    }

    private Project fillProject(Project project) {
        project.setName(view.name().getValue());
        project.setDescription(view.description().getText());
        Long stateId = view.state().getValue().getId();
        project.setStateId(stateId);
        project.setPauseDate((!Objects.equals(stateId, PAUSED)) ? null : view.pauseDate().getValue().getTime());
        project.setCustomer(Company.fromEntityOption(view.company().getValue()));
        project.setCustomerType(view.customerType().getValue());
        project.setProducts(toSet(view.products().getValue(), DevUnit::fromProductShortView));
        project.setTechnicalSupportValidity(view.technicalSupportValidity().getValue());
        project.setWorkCompletionDate(view.workCompletionDate().getValue());
        project.setPurchaseDate(view.purchaseDate().getValue());
        project.setProductDirections( toSet(view.directions().getValue(), DevUnit::fromProductDirectionInfo));
        project.setRegion(view.region().getValue());
        project.setTeam(new ArrayList<>(view.team().getValue()));
        project.setProjectSlas(view.slaInput().getValue());
        project.setProjectPlans(stream(view.plans().getValue())
                .map(PlanOption::toPlan)
                .collect(Collectors.toList()));
        project.setSubcontractors(stream(view.subcontractors().getValue())
                .map(Company::fromEntityOption)
                .collect(Collectors.toList()));
        return project;
    }

    private void fillCaseLinks(Project project) {

        En_Privilege actionPrivilege = isNew(project)
                ? En_Privilege.PROJECT_CREATE
                : En_Privilege.PROJECT_EDIT;
        boolean canAction = canAccessProject(policyService, actionPrivilege, project.getTeam());
        boolean canView = canAccessProject(policyService, En_Privilege.ISSUE_VIEW, project.getTeam());

        view.getLinksContainer().clear();
        view.addLinkButtonVisibility().setVisible(canAction);
        if (canView) {
            fireEvent(new CaseLinkEvents.Show(view.getLinksContainer())
                    .withCaseId(project.getId())
                    .withCaseType(PROJECT_CASE_TYPE)
                    .withReadOnly(!canAction));
        }
    }

    private boolean validateView() {
        if(!view.nameValidator().isValid()){
            fireEvent(new NotifyEvents.Show(lang.errEmptyName(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if(isEmpty(view.directions().getValue())){
            fireEvent(new NotifyEvents.Show(lang.errSaveProjectNeedSelectDirection(), NotifyEvents.NotifyType.ERROR));
            return false;
        }
        if(view.customerType().getValue() == null){
            fireEvent(new NotifyEvents.Show(lang.errSaveProjectNeedSelectCustomerType(), NotifyEvents.NotifyType.ERROR));
            return false;
        }
        if(view.company().getValue() == null){
            fireEvent(new NotifyEvents.Show(lang.errSaveProjectNeedSelectCompany(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if (!view.slaValidator().isValid()) {
            fireEvent(new NotifyEvents.Show(lang.projectSlaNotValid(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if ( view.state().getValue().getId().equals(PAUSED) ) {
            Date pauseDate = view.pauseDate().getValue();
            if (pauseDate == null) {
                fireEvent(new NotifyEvents.Show(lang.errSaveProjectPauseDate(), NotifyEvents.NotifyType.ERROR));
                return false;
            }

            if (!Objects.equals(project.getPauseDate(), pauseDate.getTime()) && pauseDate.getTime() < System.currentTimeMillis()) {
                fireEvent(new NotifyEvents.Show(lang.errSaveProjectPauseDate(), NotifyEvents.NotifyType.ERROR));
                return false;
            }
        }

        if (!hasHeadManager(CollectionUtils.emptyIfNull(view.team().getValue()))) {
            fireEvent(new NotifyEvents.Show(lang.errSaveProjectHeadManager() ,NotifyEvents.NotifyType.ERROR));
            return false;
        }

        return true;
    }

    private boolean hasHeadManager(Set<PersonProjectMemberView> team) {
        return team.stream().anyMatch(personProjectMemberView -> En_PersonRoleType.HEAD_MANAGER.equals(personProjectMemberView.getRole()));
    }

    @Inject
    Lang lang;
    @Inject
    AbstractProjectEditView view;
    @Inject
    RegionControllerAsync regionService;
    @Inject
    PolicyService policyService;
    @Inject
    DefaultErrorHandler defaultErrorHandler;
    @Inject
    CompanyControllerAsync companyService;
    @Inject
    private AbstractCommentAndHistoryListView commentAndHistoryView;

    private Project project;
    private Set<ProductShortView> selectedComplexes = new HashSet<>();

    private static final En_CaseType PROJECT_CASE_TYPE = En_CaseType.PROJECT;

    private AppEvents.InitDetails initDetails;
    private static final Logger log = Logger.getLogger( ProjectEditActivity.class.getName() );
}
