package ru.protei.portal.ui.issue.client.activity.preview;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.JiraMetaData;
import ru.protei.portal.core.model.util.CaseTextMarkupUtil;
import ru.protei.portal.core.model.util.TransliterationUtils;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.*;
import ru.protei.portal.ui.common.client.util.ClipboardUtils;
import ru.protei.portal.ui.common.client.util.LinkUtils;
import ru.protei.portal.ui.common.client.widget.timefield.WorkTimeFormatter;
import ru.protei.portal.ui.common.client.service.AttachmentServiceAsync;
import ru.protei.portal.ui.common.client.service.CompanyControllerAsync;
import ru.protei.portal.ui.common.client.service.IssueControllerAsync;
import ru.protei.portal.ui.common.client.service.TextRenderControllerAsync;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
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
            public void onError(En_FileUploadStatus status, String details) {
                if (En_FileUploadStatus.SIZE_EXCEED_ERROR.equals(status)) {
                    fireEvent(new NotifyEvents.Show(lang.uploadFileSizeExceed() + " (" + details + "Mb)", NotifyEvents.NotifyType.ERROR));
                }
                else {
                    fireEvent(new NotifyEvents.Show(lang.uploadFileError(), NotifyEvents.NotifyType.ERROR));
                }
            }
        });
        workTimeFormatter = new WorkTimeFormatter(lang);
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
        view.backBtnVisibility().setVisible(false);
        view.isFullScreen(false);
    }

    @Event
    public void onShow( IssueEvents.ShowFullScreen event ) {
        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );

        this.issueCaseNumber = event.issueCaseNumber;
        issueId = null;
        isPrivateCase = false;

        fillView(issueCaseNumber);
        view.backBtnVisibility().setVisible(true);
        view.isFullScreen(true);
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
                        .withPrivateVisible(!isPrivateCase && policyService.hasPrivilegeFor(En_Privilege.ISSUE_PRIVACY_VIEW))
                        .withPrivateCase(isPrivateCase)
                        .withTextMarkup(textMarkup)
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

    @Override
    public void onCopyClicked() {
        int status = ClipboardUtils.copyToClipboard(lang.crmPrefix() + caseObject.getCaseNumber() + " " + caseObject.getName());

        if (status != 0) {
            fireEvent(new NotifyEvents.Show(lang.errCopyToClipboard(), NotifyEvents.NotifyType.ERROR));
        } else {
            fireEvent(new NotifyEvents.Show(lang.issueCopiedToClipboard(), NotifyEvents.NotifyType.SUCCESS));
        }
    }

    private void fillView(CaseObject value ) {
        this.caseObject = value;
        view.setPrivateIssue( value.isPrivateCase() );
        view.setCaseNumber(value.getCaseNumber());
        view.setCreatedBy(lang.createBy(transliteration(value.getCreator().getDisplayShortName()), DateFormatter.formatDateTime(value.getCreated())));

        view.setState( value.getStateId() );
        view.setImportance( value.getImpLevel() );
        view.setProduct( value.getProduct() == null ? "" : value.getProduct().getName() );

        String contact = value.getInitiator() == null ? "" : transliteration(value.getInitiator().getDisplayName());
        Company initiatorCompany = value.getInitiatorCompany();
        if ( initiatorCompany != null ) {
            contact += " (" + transliteration(initiatorCompany.getCname()) + ")";
        }
        view.setContact( contact );
        String manager = value.getManager() == null ? "" : transliteration(value.getManager().getDisplayName() + " (" + value.getManager().getCompany().getCname() + ")");
        view.setManager( manager );
        view.setName( value.getName() == null ? "" : value.getName(), En_ExtAppType.JIRA.getCode().equals(value.getExtAppType()));

        view.setPlatformName(value.getPlatformId() == null ? "" : value.getPlatformName());
        view.setPlatformLink(LinkUtils.makeLink(Platform.class, value.getPlatformId()));
        view.setPlatformVisibility(policyService.hasPrivilegeFor(En_Privilege.ISSUE_PLATFORM_VIEW));

        view.setInfo( value.getInfo() == null ? "" : value.getInfo() );

        fillSubscriptions(value);

        view.timeElapsedContainerVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_WORK_TIME_VIEW));
        Long timeElapsed = value.getTimeElapsed();
        view.timeElapsed().setTime(Objects.equals(0L, timeElapsed) ? null : timeElapsed);
//        view.setLinks(value.getLinks() == null ? null : new HashSet<>(value.getLinks()));
        view.setTags(value.getTags() == null ? new HashSet<>() : value.getTags());

        view.attachmentsContainer().clear();
        view.attachmentsContainer().add(value.getAttachments());

        if (StringUtils.isNotBlank(value.getInfo())) {
            En_TextMarkup textMarkup = CaseTextMarkupUtil.recognizeTextMarkup(value);
            textRenderController.render(value.getInfo(), textMarkup, new FluentCallback<String>()
                    .withError(throwable -> {})
                    .withSuccess(rendered -> view.setInfo(rendered)));
        }

        fillViewForJira(value);

        fireEvent(new CaseCommentEvents.Show.Builder(view.getCommentsContainer())
                .withCaseType(En_CaseType.CRM_SUPPORT)
                .withCaseId(value.getId())
                .withModifyEnabled(policyService.hasEveryPrivilegeOf(En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT))
                .withElapsedTimeEnabled(policyService.hasPrivilegeFor(En_Privilege.ISSUE_WORK_TIME_VIEW))
                .withPrivateVisible(!isPrivateCase && policyService.hasPrivilegeFor(En_Privilege.ISSUE_PRIVACY_VIEW))
                .withPrivateCase(isPrivateCase)
                .withTextMarkup(textMarkup)
                .build());
    }

    private void fillViewForJira(CaseObject value) {

        view.jiraContainerVisibility().setVisible(false);

        if (!En_ExtAppType.JIRA.getCode().equals(value.getExtAppType())) {
            return;
        }

        JiraMetaData meta = value.getJiraMetaData();
        boolean isSeverityDisplayed = En_JiraSLAIssueType.byPortal().contains(En_JiraSLAIssueType.forIssueType(meta.getIssueType()));

        view.jiraContainerVisibility().setVisible(true);

        view.setJiraIssueType(meta.getIssueType());
        view.setJiraSeverity(isSeverityDisplayed ? meta.getSeverity() : null);

        slaController.getJiraSLAEntry(meta.getSlaMapId(), meta.getIssueType(), meta.getSeverity(), new FluentCallback<JiraSLAMapEntry>()
            .withError(throwable -> {
                view.setJiraTimeOfReaction(null);
                view.setJiraTimeOfDecision(null);
            })
            .withSuccess(entry -> {
                String timeOfReaction = entry.getTimeOfReactionMinutes() == null ? null : workTimeFormatter.asString(entry.getTimeOfReactionMinutes());
                String timeOfDecision = entry.getTimeOfDecisionMinutes() == null ? null : workTimeFormatter.asString(entry.getTimeOfDecisionMinutes());
                String description = entry.getDescription();
                String severity = StringUtils.isNotBlank(description) ? description : meta.getSeverity();
                view.setJiraTimeOfReaction(timeOfReaction);
                view.setJiraTimeOfDecision(timeOfDecision);
                view.setJiraSeverity(isSeverityDisplayed ? severity : null);
            }));
    }

    private void fillSubscriptions( CaseObject value ) {
        List<CompanySubscription> companySubscriptions = value.getInitiatorCompany() == null ? null : value.getInitiatorCompany().getSubscriptions();
        String subscribers = formSubscribers( value.getNotifiers(), companySubscriptions, policyService.hasPrivilegeFor( En_Privilege.ISSUE_FILTER_MANAGER_VIEW ), value.isPrivateCase() );
        view.setSubscriptionEmails( transliteration(subscribers) );

        companyService.getCompanyWithParentCompanySubscriptions( value.getInitiatorCompanyId(), new ShortRequestCallback<List<CompanySubscription>>()
                .setOnSuccess( subscriptions -> {
                    String subscribers2 = formSubscribers( value.getNotifiers(), subscriptions, policyService.hasPrivilegeFor( En_Privilege.ISSUE_FILTER_MANAGER_VIEW ), value.isPrivateCase() );
                    view.setSubscriptionEmails( transliteration(subscribers2) );
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
                issueId = caseObject.getId();
                isPrivateCase = caseObject.isPrivateCase();
                textMarkup = CaseTextMarkupUtil.recognizeTextMarkup(caseObject);

                requestCaseLinks(issueId);

                fillView( caseObject );
            }
        } );
    }

    private void requestCaseLinks( Long issueId ) {
        issueService.getCaseLinks(issueId, new FluentCallback<List<CaseLink>>().withSuccess( caseLinks ->
                view.setLinks(caseLinks == null ? null : new HashSet<>(caseLinks))
        ));
    }

    private String formSubscribers(Set<Person> notifiers, List< CompanySubscription > companySubscriptions, boolean isPersonsAllowed, boolean isPrivateCase){

        String message = null;
        if (CollectionUtils.isEmpty(companySubscriptions)) {
            message = lang.issueCompanySubscriptionNotDefined();
        }

        List<String> companySubscribers = new ArrayList<>();
        if (companySubscriptions != null) {
             companySubscribers = companySubscriptions.stream()
                     .map( CompanySubscription::getEmail )
                     .filter(mail -> !isPrivateCase || mail.endsWith("@protei.ru"))
                     .collect( Collectors.toList());
        }

        if (companySubscribers.isEmpty() && message == null) {
            message = lang.issueCompanySubscriptionBasedOnPrivacyNotDefined();
        }

        List<String> personSubscribers = new ArrayList<>();
        if(isPersonsAllowed && notifiers != null){
            personSubscribers = notifiers.stream().map(Person::getDisplayShortName)
                    .collect( Collectors.toList());
        }

        if (personSubscribers.isEmpty() && message != null) {
            return message;
        }

        return Stream.concat(companySubscribers.stream(), personSubscribers.stream()).collect(Collectors.joining(", "));
    }

    private void addAttachments(Collection<Attachment> attachs){
        if(view.attachmentsContainer().isEmpty())
            fireEvent(new IssueEvents.ChangeIssue(issueId));

        view.attachmentsContainer().add(attachs);
    }

    private String transliteration(String input) {
        return TransliterationUtils.transliterate(input, LocaleInfo.getCurrentLocale().getLocaleName());
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
    @Inject
    TextRenderControllerAsync textRenderController;
    @Inject
    SLAControllerAsync slaController;

    private Long issueCaseNumber;
    private Long issueId;
    private boolean isPrivateCase;
    private En_TextMarkup textMarkup;
    private AppEvents.InitDetails initDetails;
    private WorkTimeFormatter workTimeFormatter;
    private CaseObject caseObject;
}
