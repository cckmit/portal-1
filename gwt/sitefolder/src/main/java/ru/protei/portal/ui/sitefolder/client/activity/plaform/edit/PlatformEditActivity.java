package ru.protei.portal.ui.sitefolder.client.activity.plaform.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.struct.UploadResult;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_FileUploadError;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AttachmentServiceAsync;
import ru.protei.portal.ui.common.client.service.SiteFolderControllerAsync;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public abstract class PlatformEditActivity implements Activity, AbstractPlatformEditActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        view.setFileUploadHandler(new AttachmentUploader.FileUploadHandler() {
            @Override
            public void onSuccess(Attachment attachment) {
                addAttachmentsToCase(Collections.singleton(attachment));
            }
            @Override
            public void onError(UploadResult result) {
                if (result.getError().equals(En_FileUploadError.SIZE_EXCEED)) {
                    fireEvent(new NotifyEvents.Show(lang.uploadFileSizeExceed() + result.getDetails(), NotifyEvents.NotifyType.ERROR));
                }
                else
                    fireEvent(new NotifyEvents.Show(lang.uploadFileError(), NotifyEvents.NotifyType.ERROR));
            }
        });
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(SiteFolderPlatformEvents.Edit event) {

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());

        fireEvent(new ActionBarEvents.Clear());
        if (event.platformId == null) {
            fireEvent(new AppEvents.InitPanelName(lang.siteFolderPlatformNew()));
            Platform platform = new Platform();
            if (event.company != null) {
                platform.setCompany(event.company);
            }
            fillView(platform);
            return;
        }
        fireEvent(new AppEvents.InitPanelName(lang.siteFolderPlatformEdit()));

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

        if (!isValid()) {
            fireEvent(new NotifyEvents.Show(lang.errFieldsRequired(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        fillPlatform(platform);

        siteFolderController.savePlatform(platform, new RequestCallback<Platform>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.siteFolderPlatformNotSaved(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Platform result) {
                fireEvent(new SiteFolderPlatformEvents.ChangeModel());
                fireEvent(new SiteFolderPlatformEvents.Changed(result));
                fireEvent(new Back());
            }
        });
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    @Override
    public void onOpenClicked() {
        if (platform != null) {
            fireEvent(new SiteFolderServerEvents.Show(platform.getId()));
        }
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
        EntityOption value = view.company().getValue();

        view.name().setValue(value == null ? "" : value.getDisplayText());
        fireShowCompanyContacts(value == null ? null : value.getId() );
    }

    @Override
    public void onRemoveAttachment(Attachment attachment) {
        attachmentService.removeAttachmentEverywhere(En_CaseType.SF_PLATFORM, attachment.getId(), new FluentCallback<Boolean>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.removeFileError(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess(result -> {
                    if (!result) {
                        fireEvent(new NotifyEvents.Show(lang.removeFileError(), NotifyEvents.NotifyType.ERROR));
                        return;
                    }
                    view.attachmentsContainer().remove(attachment);
                    platform.getAttachments().remove(attachment);
                }));
    }

    private void fillView(Platform platform) {
        this.platform = platform;
        boolean isNotNew = platform.getId() != null;
        boolean isCreatePrivilegeGranted = policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_CREATE);
        view.name().setValue(platform.getName());
        view.company().setValue(EntityOption.fromCompany(platform.getCompany()));
        view.manager().setValue(platform.getManager() == null ? null : platform.getManager().toShortNameShortView());
        view.parameters().setValue(platform.getParams());
        view.comment().setValue(platform.getComment());
        view.createButtonVisibility().setVisible(isCreatePrivilegeGranted);
        view.openButtonVisibility().setVisible(isNotNew);
        view.listContainerVisibility().setVisible(isNotNew);
        view.listContainerHeaderVisibility().setVisible(isNotNew);
        view.setCaseNumber(platform.getId());
        view.attachmentsContainer().clear();
        if (isNotNew) {
            view.attachmentsContainer().add(platform.getAttachments());
            fireEvent(new SiteFolderServerEvents.ShowList(view.listContainer(), platform.getId()));
        }

        fireShowCompanyContacts(platform.getCompanyId());
    }

    private void fireShowCompanyContacts(Long companyId) {
        if ( companyId == null ) {
            view.contactsContainer().clear();
            return;
        }

        fireEvent(new ContactEvents.ShowConciseTable(view.contactsContainer(), companyId).readOnly());
    }

    private void fillPlatform(Platform platform) {
        platform.setName(view.name().getValue());
        platform.setCompanyId(view.company().getValue().getId());
        platform.setManager(Person.fromPersonShortView(view.manager().getValue()));
        platform.setParams(view.parameters().getValue());
        platform.setComment(view.comment().getValue());
    }

    private boolean isValid() {
        return view.nameValidator().isValid() && view.companyValidator().isValid();
    }

    private void addAttachmentsToCase(Collection<Attachment> attachments) {

        view.attachmentsContainer().add(attachments);

        if (CollectionUtils.isEmpty(platform.getAttachments())) {
            platform.setAttachments(new ArrayList<>());
        }
        platform.getAttachments().addAll(attachments);
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
    AttachmentServiceAsync attachmentService;

    private Platform platform;
    private AppEvents.InitDetails initDetails;
}
