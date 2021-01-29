package ru.protei.portal.ui.issue.client.activity.edit;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.annotation.ContextAware;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.NumberUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.CaseNameAndDescriptionChangeRequest;
import ru.protei.portal.core.model.struct.CaseObjectMetaJira;
import ru.protei.portal.core.model.util.CaseTextMarkupUtil;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.util.TransliterationUtils;
import ru.protei.portal.ui.common.client.activity.casetag.taglist.AbstractCaseTagListActivity;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AttachmentControllerAsync;
import ru.protei.portal.ui.common.client.service.IssueControllerAsync;
import ru.protei.portal.ui.common.client.util.ClipboardUtils;
import ru.protei.portal.ui.common.client.widget.uploader.impl.AttachmentUploader;
import ru.protei.portal.ui.common.client.widget.uploader.impl.PasteInfo;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.issue.client.view.edit.IssueInfoWidget;
import ru.protei.portal.ui.issue.client.view.edit.IssueNameDescriptionEditWidget;

import java.util.*;
import java.util.logging.Logger;

import static ru.protei.portal.core.model.helper.CaseCommentUtils.addImageInMessage;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.helper.StringUtils.isBlank;
import static ru.protei.portal.core.model.util.CaseStateUtil.isTerminalState;
import static ru.protei.portal.core.model.util.CrmConstants.Jira.NO_EXTENDED_PRIVACY_PROJECT;

public abstract class IssueEditActivity implements
        AbstractIssueEditActivity,
        AbstractIssueNameDescriptionEditWidgetActivity
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

                issueInfoWidget.attachmentsVisibility().setVisible(!issueInfoWidget.attachmentsListContainer().isEmpty());
                issueInfoWidget.setCountOfAttachments(size(issueInfoWidget.attachmentsListContainer().getAll()));

                fireIssueChanged(issue.getId());
            }

            @Override
            public void onError( En_FileUploadStatus status, String details ) {
                fireEvent( new NotifyEvents.Show( En_FileUploadStatus.SIZE_EXCEED_ERROR.equals( status ) ? lang.uploadFileSizeExceed() + " (" + details + "Mb)" : lang.uploadFileError(), NotifyEvents.NotifyType.ERROR ) );
            }
        };

        view.setFileUploadHandler( uploadHandler );
        issueNameDescriptionEditWidget.setFileUploader(view.getFileUploader());
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
            fireEvent(new ErrorPageEvents.ShowForbidden(container));
            return;
        }

        backHandler = event.backHandler != null ? event.backHandler : () -> fireEvent(new Back());

        viewModeIsPreview(false);
        container.clear();
        Window.scrollTo(0, 0);
        requestIssue(event.caseNumber, container);
    }

    @Event
    public void onShow( IssueEvents.ShowPreview event ) {
        HasWidgets container = event.parent;
        if (!hasAccess()) {
            fireEvent(new ErrorPageEvents.ShowForbidden(container));
            return;
        }

        backHandler = event.backHandler != null ? event.backHandler : () -> fireEvent(new Back());

        viewModeIsPreview(true);
        container.clear();
        requestIssue(event.issueCaseNumber, container);
    }

    @Event
    public void onShow( IssueEvents.ShowFullScreen event ) {
        HasWidgets container = initDetails.parent;
        if (!hasAccess()) {
            fireEvent(new ErrorPageEvents.ShowForbidden(container));
            return;
        }

        backHandler = () -> fireEvent(new IssueEvents.Show(false));

        viewModeIsPreview(false);
        container.clear();
        requestIssue(event.issueCaseNumber, container);
    }

    @Event
    public void onAddingAttachments( AttachmentEvents.Add event ) {
        if(view.isAttached() && issue.getId().equals(event.issueId)) {
            addAttachmentsToCase(event.attachments);

            issueInfoWidget.setCountOfAttachments(size(issueInfoWidget.attachmentsListContainer().getAll()));
            issueInfoWidget.attachmentsVisibility().setVisible(!issueInfoWidget.attachmentsListContainer().isEmpty());

            fireIssueChanged(issue.getId());
        }
    }

    @Event
    public void onRemovingAttachments( AttachmentEvents.Remove event ) {
        if(view.isAttached() && issue.getId().equals(event.issueId)) {
            event.attachments.forEach( issueInfoWidget.attachmentsListContainer()::remove);
            issue.getAttachments().removeAll(event.attachments);
            issue.setAttachmentExists(!issue.getAttachments().isEmpty());

            issueInfoWidget.setCountOfAttachments(size(issueInfoWidget.attachmentsListContainer().getAll()));
            issueInfoWidget.attachmentsVisibility().setVisible(!issueInfoWidget.attachmentsListContainer().isEmpty());

            fireIssueChanged(issue.getId());
        }
    }

    @Event
    public void onStateChanged( IssueEvents.IssueStateChanged event ) {
        if (isReadOnly()) return;
        if (view.isAttached()) {
            reloadComments();
            if (isTerminalState(event.stateId)) {
                fireEvent(new CaseCommentEvents.DisableNewComment());
            }
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
    public void onProductChanged(IssueEvents.IssueProductChanged event) {
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

    @Event
    public void onIssueMetaChanged(IssueEvents.IssueMetaChanged event) {
        if (issue == null) {
            return;
        }
        if (view.isAttached()) {
            if (isTerminalState(event.meta.getStateId())) {
                fireEvent(new CaseCommentEvents.DisableNewComment());
            }
        }
        boolean isCreatingSubtaskAllowed = isCreatingSubtaskAllowed(
                event.meta.getStateId(),
                issue.getInitiatorCompany().getAutoOpenIssue(),
                issue.getExtAppType());
        view.createSubtaskButtonVisibility().setVisible(isCreatingSubtaskAllowed);
    }

    @Event
    public void onFavoriteStateChanged(IssueEvents.IssueFavoriteStateChanged event) {
        if (issue == null) {
            return;
        }

        if (!Objects.equals(issue.getId(), event.issueId)) {
            return;
        }

        issue.setFavorite(event.isFavorite);
        view.setFavoriteButtonActive(event.isFavorite);
    }

    @Event
    public void onIssueLinkChanged(CaseLinkEvents.Changed event) {

        if (!En_CaseType.CRM_SUPPORT.equals(event.caseType)) return;

        CaseLink caseLink = event.caseLink;
        if (En_BundleType.PARENT_FOR.equals(caseLink.getBundleType())) {
            fireEvent(new IssueEvents.IssueStateUpdated(caseLink.getCaseId()));
        }
        if (En_BundleType.SUBTASK.equals(caseLink.getBundleType())) {
            fireEvent(new IssueEvents.IssueNotifiersUpdated(caseLink.getCaseId()));
            fireEvent(new IssueEvents.ChangeIssue(NumberUtils.parseLong(caseLink.getRemoteId())));
        }
    }

    @Override
    public void removeAttachment(Attachment attachment) {
        if (isReadOnly()) return;
        attachmentController.removeAttachmentEverywhere(En_CaseType.CRM_SUPPORT, attachment.getId(), new FluentCallback<Long>()
                .withError((throwable, defaultErrorHandler, status) -> {
                    if (En_ResultStatus.NOT_FOUND.equals(status)) {
                        fireEvent(new NotifyEvents.Show(lang.fileNotFoundError(), NotifyEvents.NotifyType.ERROR));
                        return;
                    }

                    if (En_ResultStatus.NOT_REMOVED.equals(status)) {
                        fireEvent(new NotifyEvents.Show(lang.removeFileError(), NotifyEvents.NotifyType.ERROR));
                        return;
                    }

                    defaultErrorHandler.accept(throwable);
                })
                .withSuccess(result -> {
                    issueInfoWidget.attachmentsListContainer().remove(attachment);
                    issue.getAttachments().remove(attachment);
                    issue.setAttachmentExists(!issue.getAttachments().isEmpty());

                    issueInfoWidget.setCountOfAttachments(size(issueInfoWidget.attachmentsListContainer().getAll()));
                    issueInfoWidget.attachmentsVisibility().setVisible(!issueInfoWidget.attachmentsListContainer().isEmpty());

                    fireIssueChanged(issue.getId());

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

        issueInfoWidget.descriptionReadOnlyVisibility().setVisible(false);
        view.getInfoContainer().add(issueInfoWidget);

        En_TextMarkup textMarkup = CaseTextMarkupUtil.recognizeTextMarkup(issue);
        issueNameDescriptionEditWidget.setIssueIdNameDescription(
                new CaseNameAndDescriptionChangeRequest(issue.getId(), issue.getName(), issue.getInfo(), issue.getAttachments()), textMarkup);
    }

    @Override
    public void onIssueNameInfoChanged(CaseNameAndDescriptionChangeRequest changeRequest) {
        issue.setName(changeRequest.getName());
        issue.setInfo(changeRequest.getInfo());
        issueInfoWidget.descriptionReadOnlyVisibility().setVisible(true);
        fillView(issue);
        fireEvent(new IssueEvents.ChangeIssue(issue.getId()));
    }

    @Override
    public void onOpenEditViewClicked() {
        fireEvent(new IssueEvents.Edit(issue.getCaseNumber()).withBackHandler(backHandler));
    }

    @Override
    public void onAddTagClicked(IsWidget target) {
        boolean isCanEditTags = policyService.hasPrivilegeFor(En_Privilege.ISSUE_EDIT);
        fireEvent(new CaseTagEvents.ShowSelector(target.asWidget(), ISSUE_CASE_TYPE, isCanEditTags, tagListActivity));
    }

    @Override
    public void onAddLinkClicked(IsWidget target) {
        fireEvent(new CaseLinkEvents.ShowLinkSelector(target, ISSUE_CASE_TYPE));
    }

    @Override
    public void onBackClicked() {
        backHandler.run();
    }

    @Override
    public void onCopyNumberClicked() {
        copyToClipboardNotify(ClipboardUtils.copyToClipboard(String.valueOf(issue.getCaseNumber())));
    }

    @Override
    public void onCopyNumberAndNameClicked() {
        copyToClipboardNotify(ClipboardUtils.copyToClipboard( lang.crmPrefix() + issue.getCaseNumber() + " " + issue.getName() ));
    }

    @Override
    public void onFavoriteStateChanged() {
        if (issue == null) {
            return;
        }

        if (issue.isFavorite()) {
            issueController.removeFavoriteState(policyService.getProfileId(), issue.getId(), new FluentCallback<Long>()
                    .withSuccess(result -> onSuccessChangeFavoriteState(issue, view))
            );
        } else {
            issueController.addFavoriteState(policyService.getProfileId(), issue.getId(), new FluentCallback<Long>()
                    .withSuccess(result -> onSuccessChangeFavoriteState(issue, view))
            );
        }
    }

    @Override
    public void onCreateSubtaskClicked() {
        fireEvent(new IssueEvents.CreateSubtask(issue.getCaseNumber()));
    }

    private void fireIssueChanged(Long issueId) {
        if (issueId == null) {
            return;
        }

        fireEvent(new IssueEvents.ChangeIssue(issueId));
    }

    private void onSuccessChangeFavoriteState(CaseObject issue, AbstractIssueEditView view) {
        issue.setFavorite(!issue.isFavorite());

        view.setFavoriteButtonActive(issue.isFavorite());

        fireEvent(new IssueEvents.ChangeIssue(issue.getId()));
        fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
    }

    private void fireSuccessCopyNotify() {
        fireEvent(new NotifyEvents.Show(lang.copiedToClipboardSuccessfully(), NotifyEvents.NotifyType.SUCCESS));
    }

    private void fireErrorCopyNotify() {
        fireEvent( new NotifyEvents.Show( lang.errCopyToClipboard(), NotifyEvents.NotifyType.ERROR ) );
    }

    private void addImageToMessage(Integer strPosition, Attachment attach) {
        issueNameDescriptionEditWidget.description().setValue(
                addImageInMessage(isJiraMarkupCase(issue), issueNameDescriptionEditWidget.description().getValue(), strPosition, attach));
    }

    private void requestIssue(Long number, HasWidgets container) {
        issueController.getIssue(number, new FluentCallback<CaseObject>()
                .withError((throwable, defaultErrorHandler, status) -> {
                    if (En_ResultStatus.PERMISSION_DENIED.equals(status)) {
                        fireEvent(new ErrorPageEvents.ShowForbidden());
                        return;
                    }

                    defaultErrorHandler.accept(throwable);
                })
                .withSuccess(issue -> {
                    IssueEditActivity.this.issue = issue;
                    fillView(issue);
                    showLinks(issue);
                    showTags(issue);
                    showMeta(issue);
                    showComments(issue);
                    showPlansHistory(issue);
                    attachToContainer(container);
                }));
    }

    private void showLinks(CaseObject issue) {
        boolean readOnly = isReadOnly();
        view.addLinkButtonVisibility().setVisible(!readOnly);
        fireEvent(new CaseLinkEvents.Show(view.getLinksContainer())
                .withCaseId(issue.getId())
                .withCaseType(En_CaseType.CRM_SUPPORT)
                .withReadOnly(readOnly));
    }

    private void showTags(CaseObject issue) {
        boolean readOnly = isReadOnly();
        boolean isEditTagEnabled = policyService.hasPrivilegeFor(En_Privilege.ISSUE_EDIT);
        view.addTagButtonVisibility().setVisible(isEditTagEnabled);
        fireEvent(new CaseTagEvents.ShowList(view.getTagsContainer(), En_CaseType.CRM_SUPPORT, issue.getId(), readOnly, a -> tagListActivity = a));
    }

    private void showMeta(CaseObject issue) {
        fireEvent(new IssueEvents.EditMeta(view.getMetaContainer())
                .withMeta(makeMeta(issue))
                .withMetaNotifiers(makeMetaNotifiers(issue))
                .withMetaJira(makeMetaJira(issue))
                .withReadOnly(isReadOnly()));
    }

    private void showComments(CaseObject issue) {
        CaseCommentEvents.Show show = new CaseCommentEvents.Show( issueInfoWidget.getCommentsContainer(),
                issue.getId(), En_CaseType.CRM_SUPPORT, hasAccess() && !isReadOnly(), issue.getCreatorId() );
        show.isElapsedTimeEnabled = policyService.hasPrivilegeFor( En_Privilege.ISSUE_WORK_TIME_VIEW );
        show.isPrivateVisible = !issue.isPrivateCase() && policyService.hasPrivilegeFor( En_Privilege.ISSUE_PRIVACY_VIEW );
        show.isPrivateCase = issue.isPrivateCase();
        show.isNewCommentEnabled = !isTerminalState(issue.getStateId());
        show.textMarkup =  CaseTextMarkupUtil.recognizeTextMarkup( issue );
        show.initiatorCompanyId = issue.getInitiatorCompany().getId();
        show.isMentionEnabled = policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_VIEW);
        show.extendedPrivacyType =  selectExtendedPrivacyType( issue );
        fireEvent( show );

    }

    private void showPlansHistory(CaseObject issue) {
        fireEvent(new CaseHistoryEvents.Load(issue.getId(), issueInfoWidget.getHistoryContainer()));
    }

    private boolean selectExtendedPrivacyType(CaseObject issue) {
        return En_ExtAppType.JIRA.getCode().equals(issue.getExtAppType()) &&
                !issue.getName().startsWith(NO_EXTENDED_PRIVACY_PROJECT);
    }

    private void reloadComments() {
        fireEvent(new CaseCommentEvents.Reload());
    }

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
        view.setName(makeName(issue.getName(), issue.getJiraUrl(), issue.getExtAppType(), issue.getJiraProjects()));
        view.setIntegration(makeIntegrationName(issue));

        view.setCaseNumber( issue.getCaseNumber() );
        issueInfoWidget.setDescription(issue.getInfo(), CaseTextMarkupUtil.recognizeTextMarkup(issue));

        issueInfoWidget.setPrivateCase(issue.isPrivateCase());

        issueInfoWidget.attachmentsListContainer().clear();
        issueInfoWidget.attachmentsListContainer().add(issue.getAttachments());

        boolean isAttachmentsEmpty = isEmpty(issue.getAttachments());

        issueInfoWidget.attachmentsVisibility().setVisible(!isAttachmentsEmpty);
        issueInfoWidget.setCountOfAttachments(size(issue.getAttachments()));

        view.addAttachmentUploaderVisibility().setVisible(!readOnly);
        view.getInfoContainer().clear();
        view.getInfoContainer().add(issueInfoWidget);

        view.nameAndDescriptionEditButtonVisibility().setVisible(!readOnly && selfIssue);
        view.setFavoriteButtonActive(issue.isFavorite());

        boolean isCreatingSubtaskAllowed = isCreatingSubtaskAllowed(
                issue.getStateId(),
                issue.getInitiatorCompany().getAutoOpenIssue(),
                issue.getExtAppType());
        view.createSubtaskButtonVisibility().setVisible(isCreatingSubtaskAllowed);
    }

    private void viewModeIsPreview( boolean isPreviewMode){
        view.backButtonVisibility().setVisible(!isPreviewMode);
        view.showEditViewButtonVisibility().setVisible(isPreviewMode);
        view.setPreviewStyles(isPreviewMode);
    }

    private String makeName( String issueName, String jiraUrl, String extAppType, List<String> jiraProjects ) {

        if (issueName == null) return "";

        jiraUrl = En_ExtAppType.JIRA.getCode().equals( extAppType ) ? jiraUrl : "";
        if (StringUtils.isEmpty(jiraUrl) || stream(jiraProjects).noneMatch(issueName::startsWith)) {
            return SimpleHtmlSanitizer.sanitizeHtml(issueName).asString();
        } else {
            String idJiraProject = issueName.split( " " )[0];
            String remainingName = "&nbsp;" + issueName.substring( idJiraProject.length() );

            return "<a href='"+ jiraUrl + idJiraProject +"' target='_blank'>" + idJiraProject + "</a>"
                    + "<label>" + SimpleHtmlSanitizer.sanitizeHtml(remainingName).asString() + "</label>";
        }
    }

    private void addAttachmentsToCase(Collection<Attachment> attachments) {
        if (issue.getAttachments() == null || issue.getAttachments().isEmpty()) {
            issue.setAttachments(new ArrayList<>());
        }

        if (isEmpty(attachments)) {
            return;
        }

        issueInfoWidget.attachmentsListContainer().add(attachments);
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

    private En_TextMarkup isJiraMarkupCase(CaseObject issue) {
        return En_ExtAppType.JIRA.getCode().equals(issue.getExtAppType()) ? En_TextMarkup.JIRA_WIKI_MARKUP : En_TextMarkup.MARKDOWN;
    }

    private void copyToClipboardNotify(Boolean success) {
        if (success) {
            fireSuccessCopyNotify();
        } else {
            fireErrorCopyNotify();
        }
    }

    private String makeIntegrationName(CaseObject issue) {
        En_ExtAppType extAppType = En_ExtAppType.forCode(issue.getExtAppType());
        if (extAppType == null) {
            return null;
        }
        String name = extAppType.getCode();
        if (isBlank(name)) {
            return null;
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    private boolean isCreatingSubtaskAllowed(Long stateId, boolean isAutoOpenIssue, String extAppType) {
        return policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_EDIT) &&
                !isTerminalState(stateId) && CrmConstants.State.CREATED != stateId &&
                !isAutoOpenIssue &&
                En_ExtAppType.forCode(extAppType) == null;
    }

    @Inject
    AbstractIssueEditView view;

    @Inject
    IssueControllerAsync issueController;
    @Inject
    AttachmentControllerAsync attachmentController;

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
    private AbstractCaseTagListActivity tagListActivity;
    private Runnable backHandler = () -> fireEvent(new Back());
    private static final En_CaseType ISSUE_CASE_TYPE = En_CaseType.CRM_SUPPORT;

    private static final Logger log = Logger.getLogger(IssueEditActivity.class.getName());
}
