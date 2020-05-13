package ru.protei.portal.ui.project.client.activity.edit;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.util.UiResult;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.struct.Project;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.function.Consumer;

import static ru.protei.portal.core.model.util.CrmConstants.SOME_LINKS_NOT_SAVED;

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
        if (!hasPrivileges(event.id)) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }

        clearParentContainer(initDetails.parent);
        initDetails.parent.add(view.asWidget());

        if (event.id == null) {
            project = new Project();
            resetView();
        } else {
            resetView();
            requestProject(event.id, this::fillView);
        }
    }

    private void clearParentContainer(HasWidgets parent) {
        parent.clear();

//        При переходе с таблицы, сохраняется положение скролла таблицы

        Window.scrollTo(0, 0);
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
                    fireEvent(isNew(project) ? new ProjectEvents.Show(false) : new Back());
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
        fireEvent(new ProjectEvents.Show(true));
    }

    @Override
    public void onAddLinkClicked(IsWidget anchor) {
        fireEvent(new CaseLinkEvents.ShowLinkSelector(anchor, PROJECT_CASE_TYPE, false));
    }

    @Override
    public void onDirectionChanged() {
        view.updateProductDirection(view.direction().getValue() == null ? null : view.direction().getValue().id);
        view.product().setValue(null);
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

    private void resetView () {
        view.setNumber(null);
        view.name().setValue("");
        view.description().setText("");
        view.state().setValue(En_RegionState.UNKNOWN);
        view.region().setValue(null);
        view.direction().setValue(null);
        view.customerType().setValue(null);
        view.company().setValue(null);
        view.companyEnabled().setEnabled(true);
        view.team().setValue(null);
        view.product().setValue(null);
        view.setHideNullValue(true);
        view.updateProductDirection(null);

        view.getDocumentsContainer().clear();
        view.getCommentsContainer().clear();
        view.showComments(false);
        view.showDocuments(false);

        view.technicalSupportValidity().setValue(null);
        view.setDateValid(true);

        view.numberVisibility().setVisible(false);

        view.saveVisibility().setVisible( hasPrivileges(project == null ? null : project.getId()) );
        view.saveEnabled().setEnabled(true);
        view.slaInput().setValue(null);

        if (project == null || project.getId() == null) fillCaseLinks(null);
    }

    private void fillView(Project project) {
        view.setNumber( project.getId().intValue() );
        view.name().setValue(project.getName());
        view.state().setValue( project.getState() );
        view.direction().setValue( project.getProductDirection() == null ? null : new ProductDirectionInfo( project.getProductDirection() ) );
        view.team().setValue( new HashSet<>( project.getTeam() ) );
        view.region().setValue( project.getRegion() );
        Company customer = project.getCustomer();
        view.company().setValue(customer == null ? null : customer.toEntityOption());
        view.companyEnabled().setEnabled(project.getId() == null);
        view.description().setText(project.getDescription());
        view.product().setValue(project.getSingleProduct());
        view.customerType().setValue(project.getCustomerType());
        view.updateProductDirection(project.getProductDirection() == null ? null : project.getProductDirection().getId());

        view.slaInput().setValue(project.getProjectSlas());
        view.numberVisibility().setVisible( true );

        view.showComments(true);
        view.showDocuments(true);

        view.technicalSupportValidity().setValue(project.getTechnicalSupportValidity());

        fillCaseLinks(project.getId());

        fireEvent(new CaseCommentEvents.Show(view.getCommentsContainer())
                .withCaseType(En_CaseType.PROJECT)
                .withCaseId(project.getId())
                .withModifyEnabled(hasPrivileges(project.getId())));

        fireEvent(new ProjectEvents.ShowProjectDocuments(view.getDocumentsContainer(), this.project.getId()));
    }

    private Project fillProject(Project project) {
        project.setName(view.name().getValue());
        project.setDescription(view.description().getText());
        project.setState(view.state().getValue());
        project.setCustomer(Company.fromEntityOption(view.company().getValue()));
        project.setCustomerType(view.customerType().getValue());
        project.setProducts(new HashSet<>(view.product().getValue() == null ? Collections.emptyList() : Collections.singleton(view.product().getValue())));
        project.setTechnicalSupportValidity(view.technicalSupportValidity().getValue());
        project.setProductDirection(EntityOption.fromProductDirectionInfo( view.direction().getValue() ));
        project.setRegion(view.region().getValue());
        project.setTeam(new ArrayList<>(view.team().getValue()));
        project.setProjectSlas(view.slaInput().getValue());
        return project;
    }

    private void fillCaseLinks(Long projectId){

        view.getLinksContainer().clear();

        view.addLinkButtonVisibility().setVisible(hasPrivileges(projectId));

        if(policyService.hasPrivilegeFor(En_Privilege.ISSUE_VIEW)){
            fireEvent(new CaseLinkEvents.Show(view.getLinksContainer())
                    .withCaseId(projectId)
                    .withCaseType(PROJECT_CASE_TYPE)
                    .withReadOnly(!hasPrivileges(projectId)));
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

        return true;
    }

    private boolean hasPrivileges(Long projectId) {
        if (projectId == null && policyService.hasPrivilegeFor(En_Privilege.PROJECT_CREATE)) {
            return true;
        }

        if (projectId != null && policyService.hasPrivilegeFor(En_Privilege.PROJECT_EDIT)) {
            return true;
        }

        return false;
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

    private Project project;

    private static final En_CaseType PROJECT_CASE_TYPE = En_CaseType.PROJECT;

    private AppEvents.InitDetails initDetails;
}
