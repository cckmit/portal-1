package ru.protei.portal.ui.project.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
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

        if (event.id == null) {
            fireEvent(new AppEvents.InitPanelName(lang.newProject()));
            project = new ProjectInfo();
            fillView(project);
        } else {
            fireEvent(new AppEvents.InitPanelName(lang.projectEdit()));
            requestProject(event.id, this::fillView);
        }
    }

    @Override
    public void onSaveClicked() {
        if (!validateView()) {
            fireEvent(new NotifyEvents.Show(lang.errFieldsRequired(), NotifyEvents.NotifyType.ERROR));
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
                successAction.accept(projectInfo);
            }
        });
    }

    private void fillView(ProjectInfo projectInfo) {
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

        if (isNew(project)) {
            view.showComments(false);
            view.showDocuments(false);
            view.getCommentsContainer().clear();
            view.getDocumentsContainer().clear();
        } else {
            view.showComments(true);
            view.showDocuments(true);

            fireEvent(new CaseCommentEvents.Show.Builder(view.getCommentsContainer())
                    .withCaseType(En_CaseType.PROJECT)
                    .withCaseId(projectInfo.getId())
                    .withModifyEnabled(policyService.hasEveryPrivilegeOf(En_Privilege.PROJECT_VIEW, En_Privilege.PROJECT_EDIT))
                    .build());

            fireEvent(new ProjectEvents.ShowProjectDocuments(view.getDocumentsContainer(), project.getId()));
        }

        view.saveVisibility().setVisible( policyService.hasPrivilegeFor( En_Privilege.PROJECT_EDIT ) );
        view.saveEnabled().setEnabled(true);
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
        if(view.company().getValue() == null){
            return false;
        }

        boolean isFieldsValid = view.nameValidator().isValid() /*&&
                view.stateValidator().isValid() &&
                view.companyValidator().isValid()*/;

        return isFieldsValid;
    }

    private boolean isNew(ProjectInfo projectInfo) {
        return projectInfo.getId() == null;
    }

/*    @Override
    public void onNameChanged() {
        String value = view.name().getValue().trim();

        //isNameUnique не принимает пустые строки!
        if ( value.isEmpty()) {
            view.setNameStatus(NameStatus.NONE);
            return;
        }

        regionService.isProjectNameUnique(
                value,
                projectId,
                new RequestCallback<Boolean>() {
                    @Override
                    public void onError(Throwable throwable) {
                        view.setNameStatus(NameStatus.ERROR);
                    }

                    @Override
                    public void onSuccess(Boolean isUnique) {
                        view.setNameStatus(isUnique ? NameStatus.SUCCESS : NameStatus.ERROR);
                    }
                });
    }*/


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