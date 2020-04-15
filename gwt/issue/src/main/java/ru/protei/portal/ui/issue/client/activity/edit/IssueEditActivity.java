package ru.protei.portal.ui.issue.client.activity.edit;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
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
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AttachmentServiceAsync;
import ru.protei.portal.ui.common.client.service.IssueControllerAsync;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.common.client.widget.uploader.PasteInfo;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.issue.client.view.edit.IssueInfoWidget;
import ru.protei.portal.ui.issue.client.view.edit.IssueNameDescriptionEditWidget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.logging.Logger;

import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;
import static ru.protei.portal.ui.common.client.util.CaseCommentUtils.addImageInMessage;

public abstract class IssueEditActivity implements
        AbstractIssueEditActivity,
        AbstractIssueNameDescriptionEditWidgetActivity,
        Activity
{

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
        issueNameDescriptionEditWidget.setActivity( this );
        issueInfoWidget.setActivity( this );

        AttachmentUploader.FileUploadHandler uploadHandler = new AttachmentUploader.FileUploadHandler() {
            @Override
            public void onSuccess(Attachment attachment, PasteInfo pasteInfo) {
                if (pasteInfo != null && attachment.getMimeType().startsWith("image/")) {
                    addImageToMessage(pasteInfo.strPosition, attachment);
                    issueNameDescriptionEditWidget.addTempAttachment(attachment);
                }
                addAttachmentsToCase( Collections.singleton( attachment ) );
            }

            @Override
            public void onError( En_FileUploadStatus status, String details ) {
                fireEvent( new NotifyEvents.Show( En_FileUploadStatus.SIZE_EXCEED_ERROR.equals( status ) ? lang.uploadFileSizeExceed() + " (" + details + "Mb)" : lang.uploadFileError(), NotifyEvents.NotifyType.ERROR ) );
            }
        };

        issueInfoWidget.setFileUploadHandler( uploadHandler );
        issueNameDescriptionEditWidget.setFileUploader(issueInfoWidget.getFileUploader());

        setNotifyFunctionsForJavascript(this);
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
        HasWidgets container = initDetails.parent;
        if (!hasAccess()) {
            fireEvent(new ForbiddenEvents.Show(container));
            return;
        }
        viewModeIsPreview(false);
        container.clear();
        requestIssue(event.caseNumber, container);
    }

    @Event
    public void onShow( IssueEvents.ShowPreview event ) {
        HasWidgets container = event.parent;
        if (!hasAccess()) {
            fireEvent(new ForbiddenEvents.Show(container));
            return;
        }
        viewModeIsPreview(true);
        container.clear();
        requestIssue(event.issueCaseNumber, container);
    }

    @Event
    public void onShow( IssueEvents.ShowFullScreen event ) {
        HasWidgets container = initDetails.parent;
        if (!hasAccess()) {
            fireEvent(new ForbiddenEvents.Show(container));
            return;
        }
        viewModeIsPreview(false);
        container.clear();
        requestIssue(event.issueCaseNumber, container);
    }

    @Event
    public void onAddingAttachments( AttachmentEvents.Add event ) {
        if(view.isAttached() && issue.getId().equals(event.issueId)) {
            addAttachmentsToCase(event.attachments);
        }
    }

    @Event
    public void onRemovingAttachments( AttachmentEvents.Remove event ) {
        if(view.isAttached() && issue.getId().equals(event.issueId)) {
            event.attachments.forEach( issueInfoWidget.attachmentsContainer()::remove);
            issue.getAttachments().removeAll(event.attachments);
            issue.setAttachmentExists(!issue.getAttachments().isEmpty());
        }
    }

    @Event
    public void onStateChanged( IssueEvents.IssueStateChanged event ) {
        if (isReadOnly()) return;
        if (view.isAttached()) {
            reloadComments();
        }
        fireEvent( new IssueEvents.ChangeIssue(event.issueId) );
    }

    @Event
    public void onImportanceChanged( IssueEvents.IssueImportanceChanged event ) {
        if (isReadOnly()) return;
        if (view.isAttached()) {
            reloadComments();
        }
        fireEvent( new IssueEvents.ChangeIssue(event.issueId) );
    }

    @Event
    public void onManagerChanged( IssueEvents.IssueManagerChanged event ) {
        if (isReadOnly()) return;
        if (view.isAttached()) {
            reloadComments();
        }
        fireEvent( new IssueEvents.ChangeIssue(event.issueId) );
    }

    @Override
    public void removeAttachment(Attachment attachment) {
        if (isReadOnly()) return;
        attachmentController.removeAttachmentEverywhere(En_CaseType.CRM_SUPPORT, attachment.getId(), new FluentCallback<Boolean>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.removeFileError(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess(result -> {
                    issueInfoWidget.attachmentsContainer().remove(attachment);
                    issue.getAttachments().remove(attachment);
                    issue.setAttachmentExists(!issue.getAttachments().isEmpty());
                    showComments( issue );
                }));
    }

    @Override
    public void onNameAndDescriptionEditClicked() {
        boolean isAllowedEditNameAndDescription = isSelfIssue(issue);
        boolean readOnly = isReadOnly();
        if (!isAllowedEditNameAndDescription || readOnly) return;
        view.nameAndDescriptionEditButtonVisibility().setVisible(false);
        view.nameVisibility().setVisible(false);

        view.getInfoContainer().clear();
        view.getInfoContainer().add(issueNameDescriptionEditWidget);

        issueInfoWidget.getDescriptionRO().addClassName(UiConstants.Styles.HIDE);
        view.getInfoContainer().add(issueInfoWidget);

        En_TextMarkup textMarkup = CaseTextMarkupUtil.recognizeTextMarkup(issue);
        issueNameDescriptionEditWidget.setIssueIdNameDescription(
                new CaseNameAndDescriptionChangeRequest(issue.getId(), issue.getName(), issue.getInfo(), issue.getAttachments()), textMarkup);
    }

    @Override
    public void onIssueNameInfoChanged(CaseNameAndDescriptionChangeRequest changeRequest) {
        issue.setName(changeRequest.getName());
        issue.setInfo(changeRequest.getInfo());
        issueInfoWidget.getDescriptionRO().removeClassName(UiConstants.Styles.HIDE);
        fillView(issue);
        fireEvent(new IssueEvents.ChangeIssue(issue.getId()));
    }

    @Override
    public void onOpenEditViewClicked() {
        fireEvent(new IssueEvents.Edit(issue.getCaseNumber()));
    }

    @Override
    public void onAddTagClicked(IsWidget target) {
        fireEvent(new CaseTagEvents.ShowTagSelector(target));
    }

    @Override
    public void onAddLinkClicked(IsWidget target) {
        fireEvent(new CaseLinkEvents.ShowLinkSelector(target, lang.issues()));
    }

    @Override
    public void onBackClicked() {
        fireEvent(new Back());
    }

    public void fireSuccessCopyNotify() {
        fireEvent(new NotifyEvents.Show(lang.issueCopiedToClipboard(), NotifyEvents.NotifyType.SUCCESS));
    }

    public void fireErrorCopyNotify() {
        fireEvent( new NotifyEvents.Show( lang.errCopyToClipboard(), NotifyEvents.NotifyType.ERROR ) );
    }

    private void addImageToMessage(Integer strPosition, Attachment attach) {
        issueNameDescriptionEditWidget.description().setValue(
                addImageInMessage(issueNameDescriptionEditWidget.description().getValue(), strPosition, attach));
    }

    private void requestIssue(Long number, HasWidgets container) {
        issueController.getIssue(number, new FluentCallback<CaseObject>()
                .withError(throwable -> {
                    if (throwable instanceof RequestFailedException && En_ResultStatus.PERMISSION_DENIED.equals(((RequestFailedException) throwable).status)) {
                        fireEvent(new ForbiddenEvents.Show());
                    }
                })
                .withSuccess(issue -> {
                    IssueEditActivity.this.issue = issue;
                    fillView(issue);
                    showLinks(issue);
                    showTags(issue);
                    showMeta(issue);
                    showComments(issue);
                    attachToContainer(container);
                }));
    }

    private void showLinks(CaseObject issue) {
        boolean readOnly = isReadOnly();
        view.addLinkButtonVisibility().setVisible(!readOnly);
        fireEvent(new CaseLinkEvents.Show(view.getLinksContainer())
                .withCaseId(issue.getId())
                .withCaseType(En_CaseType.CRM_SUPPORT)
                .withPageId(lang.issues())
                .withReadOnly(readOnly));
    }

    private void showTags(CaseObject issue) {
        boolean readOnly = isReadOnly();
        boolean isEditTagEnabled = policyService.hasPrivilegeFor(En_Privilege.ISSUE_EDIT);
        view.addTagButtonVisibility().setVisible(isEditTagEnabled);
        fireEvent(new CaseTagEvents.Show( view.getTagsContainer(), En_CaseType.CRM_SUPPORT, isEditTagEnabled,
                issue.getId(), readOnly
        ));
    }

    private void showMeta(CaseObject issue) {
        fireEvent(new IssueEvents.EditMeta(view.getMetaContainer())
                .withMeta(makeMeta(issue))
                .withMetaNotifiers(makeMetaNotifiers(issue))
                .withMetaJira(makeMetaJira(issue))
                .withReadOnly(isReadOnly()));
    }

    private void showComments(CaseObject issue) {
        fireEvent( new CaseCommentEvents.Show( issueInfoWidget.getCommentsContainer() )
                .withCaseType( En_CaseType.CRM_SUPPORT )
                .withCaseId( issue.getId() )
                .withModifyEnabled( hasAccess() && !isReadOnly() )
                .withElapsedTimeEnabled( policyService.hasPrivilegeFor( En_Privilege.ISSUE_WORK_TIME_VIEW ) )
                .withPrivateVisible( !issue.isPrivateCase() && policyService.hasPrivilegeFor( En_Privilege.ISSUE_PRIVACY_VIEW ) )
                .withPrivateCase( issue.isPrivateCase() )
                .withTextMarkup( CaseTextMarkupUtil.recognizeTextMarkup( issue ) ) );
    }

    private void reloadComments() {
        fireEvent(new CaseCommentEvents.Reload());
    }

    private static native void setNotifyFunctionsForJavascript(AbstractIssueEditActivity activity)/*-{
        $wnd.fireSuccessCopyNotify = function () {
            activity.@ru.protei.portal.ui.issue.client.activity.edit.IssueEditActivity::fireSuccessCopyNotify()();
        }
        $wnd.fireErrorCopyNotify = function () {
            activity.@ru.protei.portal.ui.issue.client.activity.edit.IssueEditActivity::fireErrorCopyNotify()();
        }
    }-*/;

    private void attachToContainer(HasWidgets container) {
        container.add(view.asWidget());
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

    private void fillView(CaseObject issue) {
        boolean selfIssue = isSelfIssue(issue);
        boolean readOnly = isReadOnly();

        view.setCaseNumber(issue.getCaseNumber());

        if (policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_VIEW)) {
            view.setPrivateIssue(issue.isPrivateCase());
        }

        view.setCreatedBy(lang.createBy(transliteration(issue.getCreator().getDisplayShortName()), DateFormatter.formatDateTime(issue.getCreated())));
        view.nameVisibility().setVisible(true);
        view.setName(makeName(issue.getName(), issue.getJiraUrl(), issue.getExtAppType()));
        view.setCopyNameText(String.valueOf(issue.getCaseNumber()));
        view.setCopyNameAndNumberText(lang.crmPrefix() + issue.getCaseNumber() + " " + issue.getName());

        issueInfoWidget.setCaseNumber( issue.getCaseNumber() );
        issueInfoWidget.setDescription(issue.getInfo(), CaseTextMarkupUtil.recognizeTextMarkup(issue));
        issueInfoWidget.attachmentsContainer().clear();
        issueInfoWidget.attachmentsContainer().add(issue.getAttachments());
        issueInfoWidget.attachmentUploaderVisibility().setVisible(!readOnly);
        view.getInfoContainer().clear();
        view.getInfoContainer().add(issueInfoWidget);

        view.nameAndDescriptionEditButtonVisibility().setVisible(!readOnly && selfIssue);

    }

    private void viewModeIsPreview( boolean isPreviewMode){
        view.backButtonVisibility().setVisible(!isPreviewMode);
        view.showEditViewButtonVisibility().setVisible(isPreviewMode);
        view.setPreviewStyles(isPreviewMode);
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

    private boolean hasAccess() {
        return policyService.hasPrivilegeFor(En_Privilege.ISSUE_VIEW);
    }

    private boolean isReadOnly() {
        return !policyService.hasPrivilegeFor(En_Privilege.ISSUE_EDIT);
    }

    @Inject
    AbstractIssueEditView view;

    @Inject
    IssueControllerAsync issueController;
    @Inject
    AttachmentServiceAsync attachmentController;

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
