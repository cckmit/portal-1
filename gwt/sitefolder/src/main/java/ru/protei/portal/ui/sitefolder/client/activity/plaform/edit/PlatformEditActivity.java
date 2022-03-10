package ru.protei.portal.ui.sitefolder.client.activity.plaform.edit;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_FileUploadStatus;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AttachmentControllerAsync;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.client.service.SiteFolderControllerAsync;
import ru.protei.portal.ui.common.client.service.TextRenderControllerAsync;
import ru.protei.portal.ui.common.client.widget.uploader.impl.AttachmentUploader;
import ru.protei.portal.ui.common.client.widget.uploader.impl.PasteInfo;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

import static ru.protei.portal.core.model.util.CrmConstants.Platform.PARAMETERS_MAX_LENGTH;
import static ru.protei.portal.ui.common.client.common.UiConstants.COMMENT_DISPLAY_PREVIEW;
import static ru.protei.portal.ui.common.client.util.AttachmentUtils.getRemoveErrorHandler;

public abstract class PlatformEditActivity implements AbstractPlatformEditActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        view.setFileUploadHandler(new AttachmentUploader.FileUploadHandler() {
            @Override
            public void onSuccess(Attachment attachment, PasteInfo pasteInfo) {
                addAttachmentsToCase(Collections.singleton(attachment));
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
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(SiteFolderPlatformEvents.Edit event) {
        if (!hasPrivileges(event.platformId)) {
            fireEvent(new ErrorPageEvents.ShowForbidden());
            return;
        }

        initDetails.parent.clear();

        Window.scrollTo(0, 0);

        initDetails.parent.add(view.asWidget());
        previousCompanyName = EMPTY_NAME;

        this.fireBackEvent =
                event.backEvent == null ?
                () -> fireEvent(new Back()) :
                event.backEvent;

        fireEvent(new ActionBarEvents.Clear());
        if (event.platformId == null) {
            Platform platform = new Platform();
            if (event.company != null) {
                platform.setCompany(event.company);
            }
            fillView(platform);
            return;
        }

        siteFolderController.getPlatform(event.platformId, new RequestCallback<Platform>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetObject(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Platform result) {
                fillView(result);
            }
        });
    }

    @Override
    public void onSaveClicked() {
        String validationErrorMsg = validate();
        if (validationErrorMsg != null) {
            fireEvent(new NotifyEvents.Show(validationErrorMsg, NotifyEvents.NotifyType.ERROR));
            return;
        }

        fillPlatform(platform);

        view.saveEnabled().setEnabled(false);

        siteFolderController.savePlatform(platform, new FluentCallback<Platform>()
                .withError(throwable -> {
                    view.saveEnabled().setEnabled(true);
                    fireEvent(new NotifyEvents.Show(lang.siteFolderPlatformNotSaved(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess(result -> {
                    view.saveEnabled().setEnabled(true);
                    fireEvent(new SiteFolderPlatformEvents.ChangeModel());
                    fireEvent(new SiteFolderPlatformEvents.Changed(result));
                    fireBackEvent.run();
                    fireEvent(new NotifyEvents.Show(lang.siteFolderPlatformSaved(), NotifyEvents.NotifyType.SUCCESS));
                })
        );
    }

    @Override
    public void onCancelClicked() {
        fireBackEvent.run();
    }

    @Override
    public void onCreateClicked() {
        if (!policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_CREATE)) {
            return;
        }

        if (platform == null) {
            return;
        }

        fireEvent(SiteFolderServerEvents.Edit.withPlatform(platform));
    }

    @Override
    public void onCompanySelected() {
        EntityOption companyValue = view.company().getValue();

        if (StringUtils.isEmpty(view.name().getValue()) || previousCompanyName.equals(view.name().getValue())) {
            view.name().setValue(companyValue == null ? EMPTY_NAME : companyValue.getDisplayText());
        }

        previousCompanyName = companyValue == null ? EMPTY_NAME : companyValue.getDisplayText();
        fireShowCompanyContacts(companyValue == null ? null : companyValue.getId() );
    }

    @Override
    public void onRemoveAttachment(Attachment attachment) {
        attachmentService.removeAttachmentEverywhere(En_CaseType.SF_PLATFORM, platform.getCaseId(), attachment.getId(), new FluentCallback<Long>()
                .withError(getRemoveErrorHandler(this, lang))
                .withSuccess(result -> {
                    view.attachmentsContainer().remove(attachment);
                    platform.getAttachments().remove(attachment);
                })
        );
    }

    @Override
    public void refreshProjectSpecificFields() {
        if (view.project().getValue() == null) {
            clearProjectSpecificFields();
            return;
        }
        projectRequest(view.project().getValue().getId(), this::fillProjectSpecificFieldsOnRefresh);
    }

    @Override
    public void renderMarkdownText(String text, Consumer<String> consumer) {
        textRenderController.render(text, En_TextMarkup.MARKDOWN, new FluentCallback<String>()
                .withError(throwable -> consumer.accept(text))
                .withSuccess(consumer));
    }

    @Override
    public void onDisplayCommentPreviewClicked(boolean isDisplay) {
        localStorageService.set(COMMENT_DISPLAY_PREVIEW, String.valueOf(isDisplay));
    }

    private boolean isNew(Platform platform) {
        return platform.getId() == null;
    }

    private void fillProjectSpecificFieldsOnRefresh(ProjectInfo project) {
        view.company().setValue(project.getContragent());
        onCompanySelected();
        view.manager().setValue(project.getManager() == null ? null : new PersonShortView(project.getManager()));
        view.companyEnabled().setEnabled(false);
        view.managerEnabled().setEnabled(false);
        view.companyValidator().setValid(true);
    }

    private void fillProjectSpecificFieldsOnLoad(ProjectInfo project){
        view.company().setValue(project.getContragent());
        view.manager().setValue(project.getManager() == null ? null : new PersonShortView(project.getManager()));
        view.companyEnabled().setEnabled(false);
        view.managerEnabled().setEnabled(false);
        view.companyValidator().setValid(true);
        view.project().setValue(new EntityOption(project.getName(), project.getId()));
        fireShowCompanyContacts(project.getContragent().getId());
    }

    private void projectRequest(Long projectId, Consumer<ProjectInfo> consumer) {
        regionService.getProjectInfo(projectId, new FluentCallback<ProjectInfo>().withSuccess(consumer));
    }

    private void clearProjectSpecificFields() {
        view.company().setValue(null);
        onCompanySelected();
        view.manager().setValue(null);
        view.companyEnabled().setEnabled(true);
        view.managerEnabled().setEnabled(true);
        view.companyValidator().setValid(false);
    }

    private void fillView(Platform platform) {
        this.platform = platform;
        boolean isNotNew = platform.getId() != null;
        if (platform.getProjectId() != null){
            projectRequest(platform.getProjectId(), this::fillProjectSpecificFieldsOnLoad);
        }
        else{
            view.project().setValue(null);
            clearProjectSpecificFields();
            view.company().setValue(EntityOption.fromCompany(platform.getCompany()));
            view.manager().setValue(platform.getManager() == null ? null : platform.getManager());
            fireShowCompanyContacts(platform.getCompanyId());
        }
        view.name().setValue(platform.getName());
        view.parameters().setValue(platform.getParams());
        view.comment().setValue(platform.getComment());
        view.setDisplayCommentPreview(localStorageService.getBooleanOrDefault(COMMENT_DISPLAY_PREVIEW, false));
        view.serversContainerVisibility().setVisible(isNotNew);
        view.setCaseNumber(platform.getId());
        view.attachmentsContainer().clear();

        if (isNotNew) {
            view.attachmentsContainer().add(platform.getAttachments());
            fireEvent(new SiteFolderServerEvents.ShowTable(view.serversContainer(), platform));
        }
    }

    private void fireShowCompanyContacts(Long companyId) {
        if ( companyId == null ) {
            view.contactsContainer().clear();
            return;
        }

        if (policyService.hasPrivilegeFor(En_Privilege.CONTACT_VIEW)) {
            fireEvent( new ContactEvents.ShowConciseTable( view.contactsContainer(), companyId ).readOnly() );
        }
    }

    private void fillPlatform(Platform platform) {
        platform.setName(view.name().getValue());
        platform.setParams(view.parameters().getValue());
        platform.setComment(view.comment().getValue());
        if (view.project().getValue() == null){
            platform.setProjectId(null);
            platform.setCompanyId(view.company().getValue().getId());
            platform.setManager(view.manager().getValue());
        }
        else {
            platform.setProjectId(view.project().getValue().getId());
            platform.setCompany(null);
            platform.setCompanyId(null);
            platform.setManager(null);
        }
    }

    private String validate() {
        boolean isValid = view.project().getValue() != null
                            ? view.nameValidator().isValid()
                            : view.nameValidator().isValid() && view.companyValidator().isValid();
        if (!isValid) {
            return lang.errFieldsRequired();
        }

        if (view.parameters().getValue().length() > PARAMETERS_MAX_LENGTH) {
            return lang.errRemoteAccessParametersLengthExceeded(PARAMETERS_MAX_LENGTH);
        }

        return null;
    }

    private void addAttachmentsToCase(Collection<Attachment> attachments) {

        view.attachmentsContainer().add(attachments);

        if (CollectionUtils.isEmpty(platform.getAttachments())) {
            platform.setAttachments(new ArrayList<>());
        }
        platform.getAttachments().addAll(attachments);
    }

    private boolean hasPrivileges(Long platformId) {
        if (platformId == null && policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_CREATE)) {
            return true;
        }

        if (platformId != null && policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_EDIT)) {
            return true;
        }

        return false;
    }

    @Inject
    Lang lang;
    @Inject
    AbstractPlatformEditView view;
    @Inject
    SiteFolderControllerAsync siteFolderController;
    @Inject
    PolicyService policyService;
    @Inject
    AttachmentControllerAsync attachmentService;
    @Inject
    RegionControllerAsync regionService;
    @Inject
    TextRenderControllerAsync textRenderController;
    @Inject
    LocalStorageService localStorageService;

    private Platform platform;
    private String previousCompanyName = EMPTY_NAME;
    private AppEvents.InitDetails initDetails;
    private Runnable fireBackEvent = () -> fireEvent(new Back());

    private static final String EMPTY_NAME = "";
}
