package ru.protei.portal.ui.issue.client.activity.edit;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.annotation.ContextAware;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.CaseObjectMeta;
import ru.protei.portal.core.model.ent.CaseObjectMetaNotifiers;
import ru.protei.portal.core.model.struct.CaseNameAndDescriptionChangeRequest;
import ru.protei.portal.core.model.struct.CaseObjectMetaJira;
import ru.protei.portal.core.model.util.CaseTextMarkupUtil;
import ru.protei.portal.core.model.util.TransliterationUtils;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AttachmentServiceAsync;
import ru.protei.portal.ui.common.client.service.IssueControllerAsync;
import ru.protei.portal.ui.common.client.util.ClipboardUtils;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.issue.client.activity.preview.AbstractIssuePreviewActivity;
import ru.protei.portal.ui.issue.client.activity.preview.AbstractIssuePreviewView;
import ru.protei.portal.ui.issue.client.view.edit.AbstractIssueNameDescriptionEditWidgetActivity;
import ru.protei.portal.ui.issue.client.view.edit.IssueInfoWidget;
import ru.protei.portal.ui.issue.client.view.edit.IssueNameDescriptionEditWidget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.logging.Logger;

import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;

/**
 * Активность создания и редактирования обращения
 */
public abstract class IssueEditActivity implements AbstractIssueEditActivity,
        AbstractIssuePreviewActivity,
        AbstractIssueNameDescriptionEditWidgetActivity,
        Activity {

    @PostConstruct
    public void onInit() {
        editView.setActivity( this );
        previewView.setActivity( this );
        issueNameDescriptionEditWidget.setActivity( this );
        issueInfoWidget.setActivity( this );

        AttachmentUploader.FileUploadHandler uploadHandler = new AttachmentUploader.FileUploadHandler() {
            @Override
            public void onSuccess( Attachment attachment ) {
                addAttachmentsToCase( Collections.singleton( attachment ) );
            }

            @Override
            public void onError( En_FileUploadStatus status, String details ) {
                fireEvent( new NotifyEvents.Show( En_FileUploadStatus.SIZE_EXCEED_ERROR.equals( status ) ? lang.uploadFileSizeExceed() + " (" + details + "Mb)" : lang.uploadFileError(), NotifyEvents.NotifyType.ERROR ) );
            }
        };

        issueInfoWidget.setFileUploadHandler( uploadHandler );
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        this.authProfile = event.profile;
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Event
    public void onShow( IssueEvents.Edit event ) {
        if (!policyService.hasPrivilegeFor(En_Privilege.ISSUE_EDIT)) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }

        initDetails.parent.clear();
        initDetails.parent.add( editView.asWidget());

        requestIssue(event.caseNumber, editView );
    }

    @Event
    public void onShow( IssueEvents.ShowPreview event ) {
        event.parent.clear();

        previewView.backBtnVisibility().setVisible(false);
        previewView.setFullScreen(false);
        event.parent.add( previewView.asWidget() );

        requestIssue( event.issueCaseNumber, previewView);
    }

    @Event
    public void onShow( IssueEvents.ShowFullScreen event ) {
        displayFullScreen();
        requestIssue(event.issueCaseNumber, previewView);
    }

    @Event
    public void onAddingAttachments( AttachmentEvents.Add event ) {
        if(editView.isAttached() && issue.getId().equals(event.issueId)) {
            addAttachmentsToCase(event.attachments);
        }
    }

    @Event
    public void onRemovingAttachments( AttachmentEvents.Remove event ) {
        if(editView.isAttached() && issue.getId().equals(event.issueId)) {
            event.attachments.forEach( issueInfoWidget.attachmentsContainer()::remove);
            issue.getAttachments().removeAll(event.attachments);
            issue.setAttachmentExists(!issue.getAttachments().isEmpty());
        }
    }

    @Override
    public void removeAttachment(Attachment attachment) {
        attachmentService.removeAttachmentEverywhere(En_CaseType.CRM_SUPPORT, attachment.getId(), new FluentCallback<Boolean>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.removeFileError(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess(result -> {
                    issueInfoWidget.attachmentsContainer().remove(attachment);
                    issue.getAttachments().remove(attachment);
                    issue.setAttachmentExists(!issue.getAttachments().isEmpty());
                    fireEvent(new CaseCommentEvents.Show( issueInfoWidget.getCommentsContainer())
                            .withCaseType(En_CaseType.CRM_SUPPORT)
                            .withCaseId(issue.getId())
                            .withModifyEnabled(policyService.hasEveryPrivilegeOf(En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT))
                            .withElapsedTimeEnabled(policyService.hasPrivilegeFor(En_Privilege.ISSUE_WORK_TIME_VIEW))
                            .withPrivateVisible(!issue.isPrivateCase() && policyService.hasPrivilegeFor(En_Privilege.ISSUE_PRIVACY_VIEW))
                            .withPrivateCase(issue.isPrivateCase())
                            .withTextMarkup(CaseTextMarkupUtil.recognizeTextMarkup(issue)));
                }));
    }

    @Override
    public void onCopyNumberClicked() {
        boolean isCopied = ClipboardUtils.copyToClipboard(String.valueOf(issue.getCaseNumber()));

        if (isCopied) {
            fireEvent(new NotifyEvents.Show(lang.issueCopiedToClipboard(), NotifyEvents.NotifyType.SUCCESS));
        } else {
            fireEvent(new NotifyEvents.Show(lang.errCopyToClipboard(), NotifyEvents.NotifyType.ERROR));
        }
    }

    @Override
    public void onGoToIssuesClicked() {
        fireEvent(new IssueEvents.Show());
    }


    @Override
    public void onNameAndDescriptionEditClicked( AbstractIssueEditView view) {
        boolean isAllowedEditNameAndDescription = isSelfIssue(issue);
        if (!isAllowedEditNameAndDescription) return;
        view.nameAndDescriptionEditButtonVisibility().setVisible( false );
        view.setNameVisible(false);

        editView.getInfoContainer().clear();
        editView.getInfoContainer().add( issueNameDescriptionEditWidget );

        En_TextMarkup textMarkup = CaseTextMarkupUtil.recognizeTextMarkup( issue );
        issueNameDescriptionEditWidget.setIssueIdNameDescription(
                new CaseNameAndDescriptionChangeRequest(issue.getId(), issue.getName(), issue.getInfo()), textMarkup );
    }

    @Override
    public void onIssueNameInfoChanged( CaseNameAndDescriptionChangeRequest changeRequest ) {
        editView.nameAndDescriptionEditButtonVisibility().setVisible( true );
        editView.setNameVisible( true );
        editView.getInfoContainer().clear();
        editView.getInfoContainer().add( issueInfoWidget );
        issue.setName( changeRequest.getName() );
        issue.setInfo( changeRequest.getInfo() );
        fillView(issue, editView);
    }

    @Override
    public void onFullScreenPreviewClicked() {
        displayFullScreen();
    }

    @Override
    public void onBackClicked() {
        fireEvent(new Back());
    }

    @Override
    public void onCopyNumberAndName() {
        boolean isCopied = ClipboardUtils.copyToClipboard( lang.crmPrefix() + issue.getCaseNumber() + " " + issue.getName() );

        if (isCopied) {
            fireEvent( new NotifyEvents.Show( lang.issueCopiedToClipboard(), NotifyEvents.NotifyType.SUCCESS ) );
        } else {
            fireEvent( new NotifyEvents.Show( lang.errCopyToClipboard(), NotifyEvents.NotifyType.ERROR ) );
        }
    }

    private void displayFullScreen() {
        initDetails.parent.clear();

        previewView.backBtnVisibility().setVisible(true);
        previewView.setFullScreen(true);
        initDetails.parent.add( previewView.asWidget() );
    }

    private void requestIssue( Long number, final AbstractIssueView view ) {
        issueService.getIssue(number, new RequestCallback<CaseObject>() {
            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onSuccess(CaseObject issue) {
                IssueEditActivity.this.issue = issue;

                fillView(issue, view);

                fireEvent(new CaseLinkEvents.Show(view.getLinksContainer())
                        .withCaseId(issue.getId())
                        .withCaseType(En_CaseType.CRM_SUPPORT));

                fireEvent(new CaseTagEvents.Show(view.getTagsContainer())
                        .withCaseId(issue.getId())
                        .withCaseType(En_CaseType.CRM_SUPPORT)
                        .withAddEnabled(policyService.hasGrantAccessFor( En_Privilege.ISSUE_EDIT ))
                        .withEditEnabled(policyService.hasGrantAccessFor( En_Privilege.ISSUE_EDIT )));

                fireEvent(new CaseCommentEvents.Show(issueInfoWidget.getCommentsContainer())
                        .withCaseType(En_CaseType.CRM_SUPPORT)
                        .withCaseId(issue.getId())
                        .withModifyEnabled(policyService.hasEveryPrivilegeOf(En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT))
                        .withElapsedTimeEnabled(policyService.hasPrivilegeFor(En_Privilege.ISSUE_WORK_TIME_VIEW))
                        .withPrivateVisible(!issue.isPrivateCase() && policyService.hasPrivilegeFor(En_Privilege.ISSUE_PRIVACY_VIEW))
                        .withPrivateCase(issue.isPrivateCase())
                        .withTextMarkup(CaseTextMarkupUtil.recognizeTextMarkup(issue)));

                fireEvent( new IssueEvents.EditMeta( view.getMetaContainer(), makeMeta( issue ), makeMetaNotifiers( issue ), makeMetaJira( issue ) ) );

            }
        });
    }

    private CaseObjectMetaJira makeMetaJira( CaseObject issue ) {
        if (!En_ExtAppType.JIRA.getCode().equals(issue.getExtAppType())) return null;
        return new CaseObjectMetaJira(issue);
    }

    private CaseObjectMetaNotifiers makeMetaNotifiers( CaseObject issue ) {
        return new CaseObjectMetaNotifiers(issue);
    }

    private CaseObjectMeta makeMeta( CaseObject issue ) {
        return new CaseObjectMeta(issue);
    }

    private void fillView(CaseObject issue, AbstractIssueView view) {
        view.setCaseNumber(issue.getCaseNumber());
        view.setPrivateIssue(issue.isPrivateCase());
        view.setCreatedBy(lang.createBy(transliteration(issue.getCreator().getDisplayShortName()), DateFormatter.formatDateTime(issue.getCreated())));
        view.setName( makeName(issue.getName(), issue.getJiraUrl(), issue.getExtAppType()));

        issueInfoWidget.setCaseNumber( issue.getCaseNumber() );
        issueInfoWidget.setDescription(issue.getInfo());
        issueInfoWidget.attachmentsContainer().clear();
        issueInfoWidget.attachmentsContainer().add(issue.getAttachments());
        view.getInfoContainer().add( issueInfoWidget );

        editView.nameAndDescriptionEditButtonVisibility().setVisible(isSelfIssue(issue));
    }

    private String makeName( String issueName, String jiraUrl, String extAppType ) {
        issueName = (issueName == null ? "" : issueName);
        jiraUrl = En_ExtAppType.JIRA.getCode().equals( extAppType ) ? jiraUrl : "";

        if (jiraUrl.isEmpty() || !issueName.startsWith( "CLM" )) {
            return issueName;
        } else {
            String idCLM = issueName.split( " " )[0];
            String remainingName = "&nbsp;" + issueName.substring( idCLM.length() );

          return "<a href='"+ jiraUrl + idCLM +"' target='_blank'>"+idCLM+"</a>"
                    + "<label>"+remainingName+"</label>";
        }
    }

    private void addAttachmentsToCase(Collection<Attachment> attachments){
        if (issue.getAttachments() == null || issue.getAttachments().isEmpty())
            issue.setAttachments(new ArrayList<>());

        if (isEmpty(attachments)) {
            return;
        }

        issueInfoWidget.attachmentsContainer().add(attachments);
        issue.getAttachments().addAll(attachments);
        issue.setAttachmentExists(true);
    }

    private boolean isSelfIssue(CaseObject issue) {
        return issue.getCreator() != null && Objects.equals(issue.getCreator().getId(), authProfile.getId());
    }

    private String transliteration(String input) {
        return TransliterationUtils.transliterate(input, LocaleInfo.getCurrentLocale().getLocaleName());
    }

    @Inject
    AbstractIssueEditView editView;
    @Inject
    AbstractIssuePreviewView previewView;

    @Inject
    IssueControllerAsync issueService;
    @Inject
    AttachmentServiceAsync attachmentService;

    @Inject
    Lang lang;
    @Inject
    PolicyService policyService;

    @Inject
    IssueNameDescriptionEditWidget issueNameDescriptionEditWidget;
    @Inject
    IssueInfoWidget issueInfoWidget;

    @ContextAware
    CaseObject issue;
    private Profile authProfile;

    private AppEvents.InitDetails initDetails;

    private static final Logger log = Logger.getLogger(IssueEditActivity.class.getName());
}
