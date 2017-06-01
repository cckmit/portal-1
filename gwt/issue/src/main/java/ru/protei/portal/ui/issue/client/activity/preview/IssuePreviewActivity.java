package ru.protei.portal.ui.issue.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanySubscription;
import ru.protei.portal.ui.common.client.common.AttachmentCollection;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AttachmentServiceAsync;
import ru.protei.portal.ui.common.client.service.IssueServiceAsync;
import ru.protei.portal.ui.common.client.widget.uploader.FileUploader;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.issue.client.activity.table.IssueTableActivity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Активность превью обращения
 */
public abstract class IssuePreviewActivity implements AbstractIssuePreviewActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
        view.setFileUploadHandler(new FileUploader.FileUploadHandler() {
            @Override
            public void onSuccess(Attachment attachment) {
                attachmentCollection.addAttachment(attachment);
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
    public void onShow( IssueEvents.ShowPreview event ) {
        event.parent.clear();
        event.parent.add( view.asWidget() );
        attachmentCollection.clear();

        this.issueId = event.issueId;

        fillView( issueId );
        view.watchForScroll( true );
        view.showFullScreen( false );
    }

    @Event
    public void onShow( IssueEvents.ShowFullScreen event ) {
        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );
        attachmentCollection.clear();

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
                attachmentCollection.removeAttachment(attachment);
                fireEvent( new IssueEvents.ShowComments( view.getCommentsContainer(), issueId, attachmentCollection ) );
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

        fireEvent( new IssueEvents.ShowComments( view.getCommentsContainer(), value.getId(), attachmentCollection ) );
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

        attachmentService.getAttachmentsByCaseId(id, new RequestCallback<List<Attachment>>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent( new NotifyEvents.Show( lang.attachmentsNotLoaded(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess(List<Attachment> result) {
                view.attachmentsContainer().clear();
                result.forEach(attachmentCollection::addAttachment);
            }
        });

    }

    @Inject
    Lang lang;
    @Inject
    AbstractIssuePreviewView view;
    @Inject
    IssueServiceAsync issueService;
    @Inject
    AttachmentServiceAsync attachmentService;
    @Inject
    IssueTableActivity issueTableActivity;

    private AttachmentCollection attachmentCollection = new AttachmentCollection() {
        @Override
        public void addAttachment(Attachment attachment) {
            if(isEmpty())
                issueTableActivity.updateRow(issueId);

            put(attachment.getId(), attachment);
            view.attachmentsContainer().add(attachment);
        }

        @Override
        public void removeAttachment(Attachment attachment) {
            remove(attachment.getId());
            view.attachmentsContainer().remove(attachment);

            if(isEmpty())
                issueTableActivity.updateRow(issueId);
        }
    };

    private Long issueId;

    private AppEvents.InitDetails initDetails;
}
