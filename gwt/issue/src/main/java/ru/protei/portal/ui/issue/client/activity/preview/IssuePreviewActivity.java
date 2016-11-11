package ru.protei.portal.ui.issue.client.activity.preview;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.issue.client.service.IssueServiceAsync;

/**
 * Активность превью обращения
 */
public abstract class IssuePreviewActivity implements AbstractIssuePreviewActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onInit( AppEvents.InitDetails event ) {
        this.initDetails = event;
    }

    @Override
    public void onFullScreenPreviewClicked() {

    }

    @Event
    public void onShow( IssueEvents.ShowPreview event ) {
        Window.alert( "Show preview !" + event.issue);

        event.parent.clear();
        event.parent.add( view.asWidget() );

        this.issueId = event.issue.getId();

        fillView( event.issue );
        view.fullScreen().setVisible( true );
        view.preview().setStyleName( "preview" );
    }

    private void fillView( CaseObject value ) {
        view.setNumber( value.getCaseNumber().toString() );
        view.setCreationDate( value.getCreated() != null ? format.format( value.getCreated() ) : "" );
        view.setState( Long.toString( value.getStateId() ) );
        view.setCriticality( Integer.toString( value.getImpLevel() ) );
        view.setProduct( value.getProduct() != null ? value.getProduct().getName() : "" );
    }

    private void fillView( Long id ) {

        if (id == null) {
            fireEvent( new NotifyEvents.Show( lang.errIncorrectParams(), NotifyEvents.NotifyType.ERROR ) );
            return;
        }

        issueService.getIssue( id, new RequestCallback<CaseObject>() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errNotFound(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( CaseObject caseObject ) {
                fireEvent( new AppEvents.InitPanelName( caseObject.getCaseNumber().toString() ) );
                fillView( caseObject );
            }
        } );
    }

    @Inject
    Lang lang;

    @Inject
    AbstractIssuePreviewView view;

    @Inject
    IssueServiceAsync issueService;

    DateTimeFormat format = DateTimeFormat.getFormat("dd.MM.yyyy");

    private Long issueId;

    private AppEvents.InitDetails initDetails;
}
