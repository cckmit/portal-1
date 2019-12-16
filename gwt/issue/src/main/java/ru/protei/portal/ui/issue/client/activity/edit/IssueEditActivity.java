package ru.protei.portal.ui.issue.client.activity.edit;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.annotation.ContextAware;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.struct.CaseObjectMetaJira;
import ru.protei.portal.core.model.util.CaseTextMarkupUtil;
import ru.protei.portal.core.model.util.TransliterationUtils;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.*;
import ru.protei.portal.ui.common.client.util.ClipboardUtils;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.common.shared.model.*;
import ru.protei.portal.ui.issue.client.activity.preview.AbstractIssuePreviewActivity;
import ru.protei.portal.ui.issue.client.activity.preview.AbstractIssuePreviewView;
import ru.protei.portal.ui.issue.client.view.edit.IssueNameInfoWidget;

import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Активность создания и редактирования обращения
 */
public abstract class IssueEditActivity implements AbstractIssueEditActivity,
        AbstractIssuePreviewActivity,
        Activity {

    @PostConstruct
    public void onInit() {
        editView.setActivity( this );
        previewView.setActivity( this );
        issueNameInfoWidget.setActivity( this );

        editView.setFileUploadHandler( new AttachmentUploader.FileUploadHandler() {
            @Override
            public void onSuccess(Attachment attachment) {
                addAttachmentsToCase(Collections.singleton(attachment));
            }
            @Override
            public void onError(En_FileUploadStatus status, String details) {
                fireEvent(new NotifyEvents.Show(En_FileUploadStatus.SIZE_EXCEED_ERROR.equals(status) ? lang.uploadFileSizeExceed() + " (" + details + "Mb)" : lang.uploadFileError(), NotifyEvents.NotifyType.ERROR));
            }
        });
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

        requestIssue( event.issueCaseNumber, previewView);

        previewView.backBtnVisibility().setVisible(false);
        previewView.setFullScreen(false);
        event.parent.add( previewView.asWidget() );
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
            event.attachments.forEach( editView.attachmentsContainer()::remove);
            issue.getAttachments().removeAll(event.attachments);
            issue.setAttachmentExists(!issue.getAttachments().isEmpty());
        }
    }

    @Override
    public void removeAttachment(Attachment attachment) {
        attachmentService.removeAttachmentEverywhere(En_CaseType.CRM_SUPPORT, attachment.getId(), new FluentCallback<Boolean>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.removeFileError(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess(result -> {
                    editView.attachmentsContainer().remove(attachment);
                    issue.getAttachments().remove(attachment);
                    issue.setAttachmentExists(!issue.getAttachments().isEmpty());
                    fireEvent(new CaseCommentEvents.Show( editView.getCommentsContainer())
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
    public void renderMarkupText(String text, Consumer<String> consumer) {
        En_TextMarkup textMarkup = CaseTextMarkupUtil.recognizeTextMarkup(issue);
        textRenderController.render(text, textMarkup, new FluentCallback<String>()
                .withError(throwable -> consumer.accept(null))
                .withSuccess(consumer));
    }

//    @Override
//    public void onDisplayPreviewChanged( String key, boolean isDisplay ) {
//        localStorageService.set( ISSUE_EDIT + "_" + key, String.valueOf( isDisplay ) );
//    }

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
    public void onEditNameAndDescriptionClicked(AbstractIssueDetailsView view) {
        boolean isAllowedEditNameAndDescription = isSelfIssue(issue);
        if (!isAllowedEditNameAndDescription) return;
        view.editNameAndDescriptionButtonVisibility().setVisible( false );
        issueNameInfoWidget.edit();
//        if (isEditingNameAndDescriptionView) {
//            switchToRONameAndDescriptionView( view, issue);
//            view.setNameAndDescriptionButtonsPanelVisibility(false);
//        } else {
//            boolean isAllowedEditNameAndDescription = isSelfIssue(issue);
//            if (isAllowedEditNameAndDescription) {
//                switchToEditingNameAndDescriptionView(issue);
//                view.setNameAndDescriptionButtonsPanelVisibility(true);
//            }
//        }
    }


    @Override
    public void onIssueNameInfoChanged( CaseObject issue ) {
        editView.editNameAndDescriptionButtonVisibility().setVisible( true );
        previewView.editNameAndDescriptionButtonVisibility().setVisible( true );
    }

    @Override
    public void onFullScreenPreviewClicked() {
        displayFullScreen();
    }

    private void displayFullScreen() {
        initDetails.parent.clear();

        previewView.backBtnVisibility().setVisible(true);
        previewView.setFullScreen(true);
        initDetails.parent.add( previewView.asWidget() );
    }

    private void requestIssue( Long number, final AbstractIssueDetailsView view ) {
        issueService.getIssue(number, new RequestCallback<CaseObject>() {
            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onSuccess(CaseObject issue) {
                IssueEditActivity.this.issue = issue;
//                initDetails.parent.add(view.asWidget());
                fillView(issue, view);

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

    private void fillView(CaseObject issue, AbstractIssueDetailsView view) {

        view.attachmentsContainer().clear();

        view.setCaseNumber(issue.getCaseNumber());
        view.setPrivateIssue(issue.isPrivateCase());
        view.attachmentsContainer().add(issue.getAttachments());
        view.setCreatedBy(lang.createBy(transliteration(issue.getCreator().getDisplayShortName()), DateFormatter.formatDateTime(issue.getCreated())));


//        issueNameInfoWidget.setNameRO(issue.getName() == null ? "" : issue.getName(), En_ExtAppType.JIRA.getCode().equals(issue.getExtAppType()) ? issue.getJiraUrl() : "");
        renderMarkupText(issue.getInfo(), converted -> issueNameInfoWidget.setDescriptionRO(converted));
//        issueNameWidget.copyNumberAndNameVisibility().setVisible(true);
        issueNameInfoWidget.setIssue(issue);
        view.getNameInfoContainer().add( issueNameInfoWidget );

//        switchToRONameAndDescriptionView(view, issue);
        view.editNameAndDescriptionButtonVisibility().setVisible(isSelfIssue(issue));
//        view.setNameAndDescriptionButtonsPanelVisibility(false);

        fireEvent(new CaseLinkEvents.Show(view.getLinksContainer())
                .withCaseId(issue.getId())
                .withCaseType(En_CaseType.CRM_SUPPORT));

        fireEvent(new CaseTagEvents.Show(view.getTagsContainer())
                .withCaseId(issue.getId())
                .withCaseType(En_CaseType.CRM_SUPPORT)
                .withAddEnabled(policyService.hasGrantAccessFor( En_Privilege.ISSUE_EDIT ))
                .withEditEnabled(policyService.hasGrantAccessFor( En_Privilege.ISSUE_EDIT )));

        fireEvent(new CaseCommentEvents.Show(view.getCommentsContainer())
                .withCaseType(En_CaseType.CRM_SUPPORT)
                .withCaseId(issue.getId())
                .withModifyEnabled(policyService.hasEveryPrivilegeOf(En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT))
                .withElapsedTimeEnabled(policyService.hasPrivilegeFor(En_Privilege.ISSUE_WORK_TIME_VIEW))
                .withPrivateVisible(!issue.isPrivateCase() && policyService.hasPrivilegeFor(En_Privilege.ISSUE_PRIVACY_VIEW))
                .withPrivateCase(issue.isPrivateCase())
                .withTextMarkup(CaseTextMarkupUtil.recognizeTextMarkup(issue)));

        fireEvent( new IssueEvents.EditMeta( view.getMetaContainer(), makeMeta( issue ), makeMetaNotifiers( issue ), makeMetaJira( issue ) ) );

    }




    private boolean makePreviewDisplaying( String key ) {
        return Boolean.parseBoolean( localStorageService.getOrDefault( ISSUE_EDIT + "_" + key, "false" ) );
    }


    private void addAttachmentsToCase(Collection<Attachment> attachments){
        if (issue.getAttachments() == null || issue.getAttachments().isEmpty())
            issue.setAttachments(new ArrayList<>());

        if (attachments != null && !attachments.isEmpty()) {
            editView.attachmentsContainer().add(attachments);
            issue.getAttachments().addAll(attachments);
            issue.setAttachmentExists(true);
        }
    }

    private boolean isSelfIssue(CaseObject issue) {
        return issue.getCreator() != null && Objects.equals(issue.getCreator().getId(), authProfile.getId());
    }

    private String transliteration(String input) {
        return TransliterationUtils.transliterate(input, LocaleInfo.getCurrentLocale().getLocaleName());
    }

//    private void switchToRONameAndDescriptionView( AbstractIssueDetailsView view1, CaseObject issue ) {
//        isEditingNameAndDescriptionView = false;
//        view.switchToRONameAndDescriptionView(true);
//        view.name().setValue(null);
//        view.description().setValue(null);
//        view.setNameRO(issue.getName() == null ? "" : issue.getName(), En_ExtAppType.JIRA.getCode().equals(issue.getExtAppType()) ? issue.getJiraUrl() : "");
//        renderMarkupText(issue.getInfo(), converted -> this.view.setDescriptionRO(converted));
//        view.copyNumberAndNameVisibility().setVisible(true);
//    }
//
//    private void switchToEditingNameAndDescriptionView(CaseObject issue) {
//        isEditingNameAndDescriptionView = true;
//        view.setDescriptionPreviewAllowed(makePreviewDisplaying(AbstractIssueEditView.DESCRIPTION));
//        view.switchToRONameAndDescriptionView(false);
//        view.name().setValue(issue.getName());
//        view.description().setValue(issue.getInfo());
//        view.setNameRO(null, "");
//        view.setDescriptionRO(null);
//        view.copyNumberAndNameVisibility().setVisible(false);
//    }

    @Inject
    AbstractIssueEditView editView;
    @Inject
    AbstractIssuePreviewView previewView;
//    @Inject
//    AbstractIssueMetaView metaView;

    @Inject
    IssueControllerAsync issueService;
    @Inject
    AttachmentServiceAsync attachmentService;

    @Inject
    Lang lang;
    @Inject
    PolicyService policyService;
//    @Inject
//    CompanyControllerAsync companyService;
//    @Inject
//    CaseStateFilterProvider caseStateFilter;
    @Inject
    TextRenderControllerAsync textRenderController;
    @Inject
    LocalStorageService localStorageService;

    @Inject
    IssueNameInfoWidget issueNameInfoWidget;

    @ContextAware
    CaseObject issue;

//    private List<CompanySubscription> subscriptionsList;
//    private String subscriptionsListEmptyMessage;
    private Profile authProfile;
//    private boolean isEditingNameAndDescriptionView = false;

    private AppEvents.InitDetails initDetails;

    private static final Logger log = Logger.getLogger(IssueEditActivity.class.getName());
    private static final String ISSUE_EDIT = "issue_edit_is_preview_displayed";
    public static final Boolean EDIT_MODE = null;
}
