package ru.protei.portal.ui.project.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.CaseCommentEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.ProjectEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Активность карточки создания и редактирования проектов
 */
public abstract class ProjectEditActivity implements AbstractProjectEditActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow(ProjectEvents.Edit event) {
        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
        resetView();

        if (event.id == null) {
            project = new ProjectInfo();
            fireEvent(new AppEvents.InitPanelName(lang.newProject()));
        } else {
            fireEvent(new AppEvents.InitPanelName(lang.projectEdit()));
            requestProject(event.id, this::fillView);
        }
    }

    @Override
    public void onSaveClicked() {
        if (!validateView()) {
            return;
        }

        fillProject(project);

        view.saveEnabled().setEnabled(false);

        regionService.saveProject( project, new RequestCallback<Void>(){
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errNotSaved(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( Void aVoid ) {
                fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                fireEvent(new ProjectEvents.ChangeModel());
                fireEvent(new ProjectEvents.Show());
                return;
            }
        });
}

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    private void requestProject(Long projectId, Consumer<ProjectInfo> successAction) {
        regionService.getProject( projectId, new RequestCallback<ProjectInfo>() {
            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onSuccess(ProjectInfo projectInfo) {
                project = projectInfo;
                successAction.accept(projectInfo);
            }
        });
    }

    private void resetView () {
        view.number().setValue(null);
        view.name().setValue("");
        view.description().setText("");
        view.state().setValue(En_RegionState.UNKNOWN);
        view.region().setValue(null);
        view.direction().setValue(null);
        view.customerType().setValue(null);
        view.company().setValue(null);
        view.team().setValue(null);
        view.products().setValue(null);

        view.getDocumentsContainer().clear();
        view.getCommentsContainer().clear();
        view.showComments(false);
        view.showDocuments(false);

        view.numberVisibility().setVisible(false);

        view.saveVisibility().setVisible( policyService.hasPrivilegeFor( En_Privilege.PROJECT_EDIT ) );
        view.saveEnabled().setEnabled(true);
    }

    private void fillView(ProjectInfo projectInfo) {
        view.number().setValue( projectInfo.getId().intValue() );
        view.name().setValue(projectInfo.getName());
        view.state().setValue( projectInfo.getState() );
        view.direction().setValue( projectInfo.getProductDirection() == null ? null : new ProductDirectionInfo( projectInfo.getProductDirection() ) );
        view.team().setValue( new HashSet<>( projectInfo.getTeam() ) );
        view.region().setValue( projectInfo.getRegion() );
        Company customer = projectInfo.getCustomer();
        view.company().setValue(customer == null ? null : customer.toEntityOption());
        view.description().setText(projectInfo.getDescription());
        view.products().setValue(projectInfo.getProducts());
        view.customerType().setValue(projectInfo.getCustomerType());

        view.numberVisibility().setVisible( true );

        view.showComments(true);
        view.showDocuments(true);

        fireEvent(new CaseCommentEvents.Show.Builder(view.getCommentsContainer())
                .withCaseType(En_CaseType.PROJECT)
                .withCaseId(projectInfo.getId())
                .withModifyEnabled(policyService.hasEveryPrivilegeOf(En_Privilege.PROJECT_VIEW, En_Privilege.PROJECT_EDIT))
                .build());

        fireEvent(new ProjectEvents.ShowProjectDocuments(view.getDocumentsContainer(), project.getId()));
    }

    private ProjectInfo fillProject(ProjectInfo projectInfo) {
        projectInfo.setName(view.name().getValue());
        projectInfo.setDescription(view.description().getText());
        projectInfo.setState(view.state().getValue());
        projectInfo.setCustomer(Company.fromEntityOption(view.company().getValue()));
        projectInfo.setCustomerType(view.customerType().getValue());
        projectInfo.setProducts(view.products().getValue());
        projectInfo.setProductDirection(EntityOption.fromProductDirectionInfo( view.direction().getValue() ));
        projectInfo.setRegion(view.region().getValue());
        projectInfo.setTeam(new ArrayList<>(view.team().getValue()));
        return project;
    }

    private boolean validateView() {
        if(!view.nameValidator().isValid()){
            fireEvent(new NotifyEvents.Show(lang.errEmptyName(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if(view.region().getValue() == null){
            fireEvent(new NotifyEvents.Show(lang.errSaveProjectNeedSelectRegion(), NotifyEvents.NotifyType.ERROR));
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

        return true;
    }

    private boolean isNew(ProjectInfo projectInfo) {
        return projectInfo.getId() == null;
    }

    @Inject
    Lang lang;
    @Inject
    AbstractProjectEditView view;
    @Inject
    RegionControllerAsync regionService;
    @Inject
    PolicyService policyService;
/*    @Inject
    LocalStorageService localStorageService;*/

    private ProjectInfo project;

    private AppEvents.InitDetails initDetails;

    private static final Logger log = Logger.getLogger(ProjectEditActivity.class.getName());
    private static final String PROJECT_EDIT = "project_edit_is_preview_displayed";
}