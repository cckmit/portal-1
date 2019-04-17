package ru.protei.portal.ui.issue.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AttachmentServiceAsync;
import ru.protei.portal.ui.common.client.service.CompanyControllerAsync;
import ru.protei.portal.ui.common.client.service.IssueControllerAsync;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.common.shared.model.ShortRequestCallback;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        if(view.isAttached() && Objects.equals(issueId, event.issueId))
            addAttachments(event.attachments);
    }

    @Event
    public void onRemovingAttachments( AttachmentEvents.Remove event ) {
        if(view.isAttached() &&  Objects.equals(issueId, event.issueId)){
            event.attachments.forEach(view.attachmentsContainer()::remove);

            if(view.attachmentsContainer().isEmpty())
                fireEvent(new IssueEvents.ChangeIssue(issueId));
        }
    }

    @Event
    public void onShow( IssueEvents.ShowPreview event ) {
        event.parent.clear();
        event.parent.add( view.asWidget() );

        this.issueCaseNumber = event.issueCaseNumber;
        issueId = null;
        isPrivateCase = false;

        fillView(issueCaseNumber);
        view.watchForScroll( true );
        view.showFullScreen( false );
    }

    @Event
    public void onShow( IssueEvents.ShowFullScreen event ) {
        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );

        this.issueCaseNumber = event.issueCaseNumber;
        issueId = null;
        isPrivateCase = false;

        fillView(issueCaseNumber);
        view.showFullScreen( true );
    }

    @Event
    public void onChangeTimeElapsed( IssueEvents.ChangeTimeElapsed event ) {
        view.timeElapsed().setTime(event.timeElapsed);
    }

    @Override
    public void removeAttachment(Attachment attachment) {
        attachmentService.removeAttachmentEverywhere(En_CaseType.CRM_SUPPORT, attachment.getId(), new RequestCallback<Boolean>() {
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

                fireEvent(new CaseCommentEvents.Show.Builder(view.getCommentsContainer())
                        .withCaseType(En_CaseType.CRM_SUPPORT)
                        .withCaseId(issueId)
                        .withModifyEnabled(policyService.hasEveryPrivilegeOf(En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT))
                        .withElapsedTimeEnabled(policyService.hasPrivilegeFor(En_Privilege.ISSUE_WORK_TIME_VIEW))
                        .withCasePrivate(isPrivateCase)
                        .build());
            }
        });
    }

    @Override
    public void onGoToIssuesClicked() {
        fireEvent(new IssueEvents.Show());
    }

    @Override
    public void onFullScreenPreviewClicked() {
        fireEvent( new IssueEvents.ShowFullScreen(issueCaseNumber) );
    }

    private void fillView(CaseObject value ) {
        view.setPrivateIssue( value.isPrivateCase() );
        view.setCaseNumber(value.getCaseNumber());
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
        }
        fillSubscriptions(value);

        view.timeElapsedContainerVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_WORK_TIME_VIEW));
        Long timeElapsed = value.getTimeElapsed();
        view.timeElapsed().setTime(Objects.equals(0L, timeElapsed) ? null : timeElapsed);
        view.setLinks(value.getLinks() == null ? null : new HashSet<>(value.getLinks()));
        view.setTags(value.getTags());

        view.attachmentsContainer().clear();
        view.attachmentsContainer().add(value.getAttachments());

        fireEvent(new CaseCommentEvents.Show.Builder(view.getCommentsContainer())
                .withCaseType(En_CaseType.CRM_SUPPORT)
                .withCaseId(value.getId())
                .withModifyEnabled(policyService.hasEveryPrivilegeOf(En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT))
                .withElapsedTimeEnabled(policyService.hasPrivilegeFor(En_Privilege.ISSUE_WORK_TIME_VIEW))
                .withCasePrivate(value.isPrivateCase())
                .build());
    }

    private void fillSubscriptions( CaseObject value ) {
        List<CompanySubscription> companySubscriptions = value.getInitiatorCompany() == null ? null : value.getInitiatorCompany().getSubscriptions();
        String subscribers = formSubscribers( value.getNotifiers(), companySubscriptions, policyService.hasPrivilegeFor( En_Privilege.ISSUE_FILTER_MANAGER_VIEW ), value.isPrivateCase() );
        view.setSubscriptionEmails( subscribers );

        companyService.getCompanyWithParentCompanySubscriptions( value.getInitiatorCompanyId(), new ShortRequestCallback<List<CompanySubscription>>()
                .setOnSuccess( subscriptions -> {
                    String subscribers2 = formSubscribers( value.getNotifiers(), subscriptions, policyService.hasPrivilegeFor( En_Privilege.ISSUE_FILTER_MANAGER_VIEW ), value.isPrivateCase() );
                    view.setSubscriptionEmails( subscribers2 );
                } ) );
    }

    private void fillView( Long number ) {
        if (number == null) {
            fireEvent( new NotifyEvents.Show( lang.errIncorrectParams(), NotifyEvents.NotifyType.ERROR ) );
            return;
        }

        issueService.getIssue( number, new RequestCallback<CaseObject>() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errNotFound(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( CaseObject caseObject ) {
                fireEvent( new AppEvents.InitPanelName( caseObject.getCaseNumber().toString() ) );
                if(caseObject!=null) {
                    issueId = caseObject.getId();
                    isPrivateCase = caseObject.isPrivateCase();
                }
                fillView( caseObject );
            }
        } );
    }

    private String formSubscribers(Set<Person> notifiers, List< CompanySubscription > companySubscriptions, boolean isPersonsAllowed, boolean isPrivateCase){

        Stream<String> companySubscribers = Stream.empty();
        if ( companySubscriptions != null ) {
             companySubscribers = companySubscriptions.stream()
                     .map( CompanySubscription::getEmail )
                     .filter(mail -> !isPrivateCase || mail.endsWith("@protei.ru"));
        }

        Stream<String> personSubscribers = Stream.empty();
        if(isPersonsAllowed && notifiers != null){
            personSubscribers = notifiers.stream().map(Person::getDisplayShortName);
        }
        return Stream.concat(companySubscribers, personSubscribers).collect(Collectors.joining(", "));
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
    IssueControllerAsync issueService;
    @Inject
    AttachmentServiceAsync attachmentService;
    @Inject
    PolicyService policyService;
    @Inject
    CompanyControllerAsync companyService;

    private Long issueCaseNumber;
    private Long issueId;
    private boolean isPrivateCase;
    private AppEvents.InitDetails initDetails;
}
