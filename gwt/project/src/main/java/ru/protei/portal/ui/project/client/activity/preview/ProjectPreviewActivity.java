package ru.protei.portal.ui.project.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.ProjectEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.RegionServiceAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

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
//        view.setPrivateIssue( value.isPrivateCase() );
        view.setName( value.getName() );
        view.setHeader( value.getId() == null ? "" : lang.projectHeader( value.getId().toString() ) );
        view.setCreationDate( value.getCreated() == null ? "" : DateFormatter.formatDateTime( value.getCreated() ) );
        view.state().setValue( value.getState() );
//        view.setCriticality( value.getImpLevel() );

        view.direction().setValue( value.getProductDirection() == null ? null : new ProductDirectionInfo( value.getProductDirection() ) );
//        view.setCompany( value.getInitiatorCompany() == null ? "" : value.getInitiatorCompany().getCname() );
//        view.setContact( value.getInitiator() == null ? "" : value.getInitiator().getDisplayName() );
//        Company ourCompany = value.getManager() == null ? null : value.getManager().getCompany();
//        view.setOurCompany( ourCompany == null ? "" : ourCompany.getCname() );

        view.headManager().setValue( value.getHeadManager() );
        view.setDetails( value.getDetails() == null ? "" : value.getDetails() );

//        fireEvent( new IssueEvents.ShowComments( view.getCommentsContainer(), value.getId() ) );
    }

    @Inject
    Lang lang;

    @Inject
    AbstractProjectPreviewView view;

    @Inject
    RegionServiceAsync regionService;

    private Long projectId;

    private AppEvents.InitDetails initDetails;
}
