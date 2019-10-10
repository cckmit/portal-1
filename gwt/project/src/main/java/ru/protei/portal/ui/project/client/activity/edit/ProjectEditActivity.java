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
import ru.protei.portal.core.model.struct.Project;
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
import java.util.Collections;
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
    public void onInitDetails(AppEvents.InitDetails event) {
        this.initDetails = event;
    }

    @Event
    public void onShow (ProjectEvents.Edit event) {
        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
        resetView();

        if (event.id == null) {
            project = new Project();
        } else {
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

        regionService.saveProject( project, new RequestCallback<Project>(){
            @Override
            public void onError( Throwable throwable ) {
                view.saveEnabled().setEnabled(true);
                fireEvent( new NotifyEvents.Show( lang.errNotSaved(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( Project aVoid ) {
                view.saveEnabled().setEnabled(true);
                fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                fireEvent(new ProjectEvents.ChangeModel());
                fireEvent(isNew(project) ? new ProjectEvents.Show(true) : new Back());
            }
        });
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
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
        view.team().setValue(null);
        view.product().setValue(null);
        view.setHideNullValue(true);

        view.getDocumentsContainer().clear();
        view.getCommentsContainer().clear();
        view.showComments(false);
        view.showDocuments(false);

        view.numberVisibility().setVisible(false);

        view.saveVisibility().setVisible( policyService.hasPrivilegeFor( En_Privilege.PROJECT_EDIT ) );
        view.saveEnabled().setEnabled(true);
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
        view.description().setText(project.getDescription());
        view.product().setValue(project.getSingleProduct());
        view.customerType().setValue(project.getCustomerType());

        view.numberVisibility().setVisible( true );

        view.showComments(true);
        view.showDocuments(true);

        fireEvent(new CaseCommentEvents.Show.Builder(view.getCommentsContainer())
                .withCaseType(En_CaseType.PROJECT)
                .withCaseId(project.getId())
                .withModifyEnabled(policyService.hasEveryPrivilegeOf(En_Privilege.PROJECT_VIEW, En_Privilege.PROJECT_EDIT))
                .build());

        fireEvent(new ProjectEvents.ShowProjectDocuments(view.getDocumentsContainer(), this.project.getId()));
    }

    private Project fillProject(Project project) {
        project.setName(view.name().getValue());
        project.setDescription(view.description().getText());
        project.setState(view.state().getValue());
        project.setCustomer(Company.fromEntityOption(view.company().getValue()));
        project.setCustomerType(view.customerType().getValue());
        project.setProducts(view.product().getValue() == null ? null : new HashSet<>(Collections.singleton(view.product().getValue())));
        project.setProductDirection(EntityOption.fromProductDirectionInfo( view.direction().getValue() ));
        project.setRegion(view.region().getValue());
        project.setTeam(new ArrayList<>(view.team().getValue()));
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

    @Inject
    Lang lang;
    @Inject
    AbstractProjectEditView view;
    @Inject
    RegionControllerAsync regionService;
    @Inject
    PolicyService policyService;

    private Project project;

    private AppEvents.InitDetails initDetails;
}