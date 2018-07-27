package ru.protei.portal.ui.project.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.ProjectEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.HashSet;
import java.util.stream.Collectors;

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

        fillView( projectId );
        view.watchForScroll( true );
        view.showFullScreen( false );
    }

    @Event
    public void onShow( ProjectEvents.ShowFullScreen event ) {
        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );

        this.projectId = event.issueId;

        fillView( projectId );
        view.showFullScreen( true );
    }

    @Override
    public void onFullScreenPreviewClicked() {
        fireEvent( new ProjectEvents.ShowFullScreen( projectId ) );
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
            }
        });
    }

    private void fillView( Long id ) {
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

    private void fillView( ProjectInfo value ) {
        this.project = value;
        view.setName( value.getName() );
        view.setHeader( value.getId() == null ? "" : lang.projectHeader( value.getId().toString() ) );
        view.setCreationDate( value.getCreated() == null ? "" : DateFormatter.formatDateTime( value.getCreated() ) );
        view.state().setValue( value.getState() );
        view.direction().setValue( value.getProductDirection() == null ? null : new ProductDirectionInfo( value.getProductDirection() ) );
        view.headManager().setValue( value.getHeadManager() );
        view.deployManagers().setValue( new HashSet<>( value.getManagers() ) );
        view.details().setText( value.getDescription() == null ? "" : value.getDescription() );
        view.region().setValue( value.getRegion() );
        Company customer = value.getCustomer();
        view.company().setValue(customer == null ? null : customer.toEntityOption());
        view.products().setValue(value.getProducts());
        view.customerType().setValue(value.getCustomerType());

        fireEvent( new IssueEvents.ShowComments( view.getCommentsContainer(), value.getId(), false ) );

    }

    private void readView() {
        project.setName( view.getName() );
        project.setProductDirection( EntityOption.fromProductDirectionInfo( view.direction().getValue() ) );
        project.setHeadManager( view.headManager().getValue() );
        project.setManagers( view.deployManagers().getValue().stream().collect( Collectors.toList() ) );
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
