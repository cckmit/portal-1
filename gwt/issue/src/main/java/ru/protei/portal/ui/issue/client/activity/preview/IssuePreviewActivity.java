package ru.protei.portal.ui.issue.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanySubscription;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.AttachmentEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AttachmentServiceAsync;
import ru.protei.portal.ui.common.client.service.IssueServiceAsync;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Активность превью обращения
 */
public abstract class IssuePreviewActivity implements AbstractIssuePreviewActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
        view.setFileUploadHandler(new AttachmentUploader.FileUploadHandler() {
            @Override
            public void onSuccess(Attachment attachment) {
                addAttachments(Collections.singleton(attachment));
            }
            @Override
            public void onError() {
                fireEvent(new NotifyEvents.Show(lang.uploadFileError(), NotifyEvents.NotifyType.ERROR));
            }
        });
    }

    @Event
    public void onInit( AppEvents.InitDetails event ) {
        this.initDetails = event;
    }

    @Event
    public void onAddingAttachments( AttachmentEvents.Add event ) {
        if(view.isAttached() && issueId.equals(event.caseId))
            addAttachments(event.attachments);
    }

    @Event
    public void onRemovingAttachments( AttachmentEvents.Remove event ) {
        if(view.isAttached() && issueId.equals(event.caseId)){
            event.attachments.forEach(view.attachmentsContainer()::remove);

            if(view.attachmentsContainer().isEmpty())
                fireEvent(new IssueEvents.ChangeIssue(issueId));
        }
    }

    @Event
    public void onShow( IssueEvents.ShowPreview event ) {
        event.parent.clear();
        event.parent.add( view.asWidget() );

        this.issueId = event.issueId;

        fillView( issueId );
        view.watchForScroll( true );
        view.showFullScreen( false );
    }

    @Event
    public void onShow( IssueEvents.ShowFullScreen event ) {
        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );

        this.issueId = event.issueId;

        fillView( issueId );
        view.showFullScreen( true );
    }

    @Override
    public void removeAttachment(Attachment attachment) {
        attachmentService.removeAttachmentEverywhere(attachment.getId(), new RequestCallback<Boolean>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.removeFileError(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Boolean result) {
                if(!result){
                    onError(null);
                    return;
                }

                view.attachmentsContainer().remove(attachment);
                if(view.attachmentsContainer().isEmpty())
                    fireEvent(new IssueEvents.ChangeIssue(issueId));

                fireEvent( new IssueEvents.ShowComments( view.getCommentsContainer(), issueId ) );
            }
        });
    }

    @Override
    public void onFullScreenPreviewClicked() {
        fireEvent( new IssueEvents.ShowFullScreen( issueId ) );
    }

    private void fillView(CaseObject value ) {
        view.setPrivateIssue( value.isPrivateCase() );
        view.setCaseId(value.getId());
        view.setHeader( value.getCaseNumber() == null ? "" : lang.issueHeader( value.getCaseNumber().toString() ) );
        view.setCreationDate( value.getCreated() == null ? "" : DateFormatter.formatDateTime( value.getCreated() ) );
        view.setState( value.getStateId() );
        view.setCriticality( value.getImpLevel() );
        view.setProduct( value.getProduct() == null ? "" : value.getProduct().getName() );
        view.setContact( value.getInitiator() == null ? "" : value.getInitiator().getDisplayName() );
        Company ourCompany = value.getManager() == null ? null : value.getManager().getCompany();
        view.setOurCompany( ourCompany == null ? "" : ourCompany.getCname() );
        view.setManager( value.getManager() == null ? "" : value.getManager().getDisplayName() );
        view.setName( value.getName() == null ? "" : value.getName() );
        view.setInfo( value.getInfo() == null ? "" : value.getInfo() );
        Company initiator = value.getInitiatorCompany();
        if ( initiator == null ) {
            view.setCompany( "" );
        } else {
            view.setCompany( initiator.getCname() );
            view.setSubscriptionEmails( initiator.getSubscriptions() == null
                    ? ""
                    : initiator.getSubscriptions()
                    .stream()
                    .map( CompanySubscription::getEmail )
                    .collect( Collectors.joining(", ") ) );
        }

        view.attachmentsContainer().clear();
        view.attachmentsContainer().add(value.getAttachments());

        fireEvent( new IssueEvents.ShowComments( view.getCommentsContainer(), value.getId() ) );
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

//        attachmentService.getAttachmentsByCaseId(id, new RequestCallback<List<Attachment>>() {
//            @Override
//            public void onError(Throwable throwable) {
//                fireEvent( new NotifyEvents.Show( lang.attachmentsNotLoaded(), NotifyEvents.NotifyType.ERROR ) );
//            }
//
//            @Override
//            public void onSuccess(List<Attachment> result) {
//                view.attachmentsContainer().clear();
//                IssuePreviewActivity.this.addAttachments(result);
//            }
//        });
    }

    private void addAttachments(Collection<Attachment> attachs){
        if(view.attachmentsContainer().isEmpty())
            fireEvent(new IssueEvents.ChangeIssue(issueId));

        view.attachmentsContainer().add(attachs);
    }

    @Inject
    Lang lang;
    @Inject
    AbstractIssuePreviewView view;
    @Inject
    IssueServiceAsync issueService;
    @Inject
    AttachmentServiceAsync attachmentService;

    private Long issueId;
    private AppEvents.InitDetails initDetails;
}
