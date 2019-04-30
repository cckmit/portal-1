package ru.protei.portal.ui.project.client.activity.preview;

import com.google.inject.Inject;
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
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Активность превью проекта
 */
public abstract class ProjectPreviewActivity implements AbstractProjectPreviewActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onInit( AppEvents.InitDetails event ) {
        this.initDetails = event;
    }

    @Event
    public void onShow( ProjectEvents.ShowPreview event ) {
        event.parent.clear();
        event.parent.add( view.asWidget() );

        this.projectId = event.issueId;

        showView( projectId );
        view.watchForScroll( true );
        view.showFullScreen( false );
    }

    @Event
    public void onShow( ProjectEvents.ShowFullScreen event ) {
        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );

        this.projectId = event.issueId;

        showView( projectId );
        view.showFullScreen( true );
    }

    @Event
    public void onConfirmRemove(ConfirmDialogEvents.Confirm event) {

        if (!getClass().getName().equals(event.identity)) {
            return;
        }

        if (!policyService.hasPrivilegeFor(En_Privilege.PROJECT_REMOVE)) {
            return;
        }

        regionService.removeProject(project.getId(), new FluentCallback<Boolean>()
                .withSuccess(result -> {
                    fireEvent(new ProjectEvents.Show());
                    fireEvent(new NotifyEvents.Show(lang.projectRemoveSucceeded(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new ProjectEvents.ChangeModel());
                })
        );
    }

    @Override
    public void onFullScreenPreviewClicked() {
        fireEvent( new ProjectEvents.ShowFullScreen( projectId ) );
    }

    @Override
    public void onRemoveClicked() {
        if (project == null) {
            return;
        }
        fireEvent(new ConfirmDialogEvents.Show(getClass().getName(), lang.projectRemoveConfirmMessage(project.getName())));
    }

    @Override
    public void onProjectChanged() {
        if ( !policyService.hasPrivilegeFor( En_Privilege.PROJECT_EDIT ) ) {
            return;
        }

        readView();
        regionService.saveProject( project, new RequestCallback<Void>(){
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errNotSaved(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( Void aVoid ) {
                fireEvent( new ProjectEvents.Changed( project ) );
                fireEvent( new ProjectEvents.ChangeModel() );
            }
        });
    }

    private void showView(Long id ) {
        if (id == null) {
            fireEvent( new NotifyEvents.Show( lang.errIncorrectParams(), NotifyEvents.NotifyType.ERROR ) );
            return;
        }

        regionService.getProject( id, new RequestCallback<ProjectInfo>() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errNotFound(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( ProjectInfo project ) {
                fireEvent( new AppEvents.InitPanelName( project.getId().toString() ) );
                fillView( project );
            }
        } );
    }

    private void fillView(ProjectInfo value ) {
        this.project = value;
        view.setName( value.getName() );
        view.setInitiatorShortName( value.getCreator() == null ? "" : value.getCreator().getDisplayShortName() );
        view.setCreationDate( value.getCreated() == null ? "" : DateFormatter.formatDateTime( value.getCreated() ) );
        view.state().setValue( value.getState() );
        view.direction().setValue( value.getProductDirection() == null ? null : new ProductDirectionInfo( value.getProductDirection() ) );
        view.team().setValue( new HashSet<>( value.getTeam() ) );
        view.details().setText( value.getDescription() == null ? "" : value.getDescription() );
        view.region().setValue( value.getRegion() );
        Company customer = value.getCustomer();
        view.company().setValue(customer == null ? null : customer.toEntityOption());
        view.products().setValue(value.getProducts());
        view.customerType().setValue(value.getCustomerType());

        view.removeBtnVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.PROJECT_REMOVE));

        fireEvent(new CaseCommentEvents.Show.Builder(view.getCommentsContainer())
                .withCaseType(En_CaseType.PROJECT)
                .withCaseId(value.getId())
                .withModifyEnabled(policyService.hasEveryPrivilegeOf(En_Privilege.PROJECT_VIEW, En_Privilege.PROJECT_EDIT))
                .build());
        fireEvent(new ProjectEvents.ShowProjectDocuments(view.getDocumentsContainer(), project.getId()));
    }

    private void readView() {
        project.setName( view.getName() );
        project.setProductDirection( EntityOption.fromProductDirectionInfo( view.direction().getValue() ) );
        project.setTeam( new ArrayList<>(view.team().getValue()) );
        project.setState( view.state().getValue() );
        project.setDescription( view.details().getText() );
        project.setRegion( view.region().getValue() );
        project.setProducts(view.products().getValue());
        project.setCustomer(Company.fromEntityOption(view.company().getValue()));
        project.setCustomerType(view.customerType().getValue());
    }

    @Inject
    Lang lang;
    @Inject
    AbstractProjectPreviewView view;
    @Inject
    RegionControllerAsync regionService;
    @Inject
    PolicyService policyService;

    private Long projectId;
    ProjectInfo project;

    private AppEvents.InitDetails initDetails;
}
