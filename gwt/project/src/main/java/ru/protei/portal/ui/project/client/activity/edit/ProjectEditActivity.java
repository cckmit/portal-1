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
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.util.UiResult;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.model.view.PlanOption;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.dict.En_RegionState.PAUSED;
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.core.model.util.CrmConstants.SOME_LINKS_NOT_SAVED;
import static ru.protei.portal.ui.project.client.util.AccessUtil.*;

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
                .withError(throwable -> view.saveEnabled().setEnabled(true))
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
        view.updateProductDirection(view.direction().getValue() == null ? null : view.direction().getValue().id);
        view.product().setValue(null);
    }

    @Override
    public void onStateChanged() {
        view.pauseDateContainerVisibility().setVisible( PAUSED == view.state().getValue() );
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
        view.state().setValue( isNew( project ) ? En_RegionState.UNKNOWN : project.getState() );
        view.direction().setValue( project.getProductDirection() == null ? null : new ProductDirectionInfo( project.getProductDirection() ) );
        view.team().setValue( project.getTeam() == null ? null : new HashSet<>( project.getTeam() ) );
        view.region().setValue( project.getRegion() );
        Company customer = project.getCustomer();
        view.company().setValue(customer == null ? null : customer.toEntityOption());
        view.companyEnabled().setEnabled(isNew( project ));
        view.description().setText(project.getDescription());
        view.product().setValue(project.getSingleProduct());
        if (isNew( project )) view.setHideNullValue(true);
        view.customerType().setValue(project.getCustomerType());
        view.updateProductDirection(project.getProductDirection() == null ? null : project.getProductDirection().getId());
        view.pauseDateContainerVisibility().setVisible( PAUSED == project.getState() );
        view.pauseDate().setValue( project.getPauseDate() == null ? null : new Date( project.getPauseDate() ) );

        view.slaInput().setValue(project.getProjectSlas());
        view.numberVisibility().setVisible( !isNew( project ) );

        view.getCommentsContainer().clear();
        view.showComments(!isNew( project ));
        view.getDocumentsContainer().clear();
        view.showDocuments(!isNew( project ));

        view.technicalSupportValidity().setValue(project.getTechnicalSupportValidity());
        if (isNew( project )) view.setDateValid( true );

        view.plans().setValue(stream(project.getProjectPlans())
                .map(PlanOption::fromPlan)
                .collect(Collectors.toSet()));

        fillCaseLinks(project);

        En_Privilege actionPrivilege = isNew(project)
                ? En_Privilege.PROJECT_CREATE
                : En_Privilege.PROJECT_EDIT;

        if (!isNew(project)) {
            CaseCommentEvents.Show show = new CaseCommentEvents.Show(
                view.getCommentsContainer(),
                project.getId(),
                En_CaseType.PROJECT,
                canAccessProject(policyService, actionPrivilege, project.getTeam()),
                project.getCreatorId()
            );
            show.isPrivateVisible = canAccessProjectPrivateElements(policyService, En_Privilege.PROJECT_VIEW, project.getTeam());
            show.isPrivateCase = false;
            show.isNewCommentEnabled = canAccessProject(policyService, actionPrivilege, project.getTeam());
            fireEvent(show);
        }

        fireEvent(new ProjectEvents.ShowProjectDocuments(view.getDocumentsContainer(), this.project.getId()));

        view.saveVisibility().setVisible(canAccessProject(policyService, actionPrivilege, project.getTeam()));
        view.saveEnabled().setEnabled(true);
    }

    private Project fillProject(Project project) {
        project.setName(view.name().getValue());
        project.setDescription(view.description().getText());
        project.setState(view.state().getValue());
        project.setPauseDate( (PAUSED != view.state().getValue()) ? null : view.pauseDate().getValue().getTime() );
        project.setCustomer(Company.fromEntityOption(view.company().getValue()));
        project.setCustomerType(view.customerType().getValue());
        project.setProducts(new HashSet<>(view.product().getValue() == null ? Collections.emptyList() : Collections.singleton(view.product().getValue())));
        project.setTechnicalSupportValidity(view.technicalSupportValidity().getValue());
        project.setProductDirection(EntityOption.fromProductDirectionInfo( view.direction().getValue() ));
        project.setRegion(view.region().getValue());
        project.setTeam(new ArrayList<>(view.team().getValue()));
        project.setProjectSlas(view.slaInput().getValue());
        project.setProjectPlans(stream(view.plans().getValue())
                .map(PlanOption::toPlan)
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

        if(view.direction().getValue() == null){
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

        if (PAUSED.equals( view.state().getValue() )) {
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
        return team.stream().anyMatch(personProjectMemberView -> En_DevUnitPersonRoleType.HEAD_MANAGER.equals(personProjectMemberView.getRole()));
    }

    @Inject
    Lang lang;
    @Inject
    AbstractProjectEditView view;
    @Inject
    RegionControllerAsync regionService;
    @Inject
    PolicyService policyService;

    private Project project;

    private static final En_CaseType PROJECT_CASE_TYPE = En_CaseType.PROJECT;

    private AppEvents.InitDetails initDetails;
    private static final Logger log = Logger.getLogger( ProjectEditActivity.class.getName() );
}
