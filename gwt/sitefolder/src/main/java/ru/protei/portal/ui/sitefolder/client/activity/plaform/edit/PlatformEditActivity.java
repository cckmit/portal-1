package ru.protei.portal.ui.sitefolder.client.activity.plaform.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_FileUploadStatus;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.struct.Project;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AttachmentServiceAsync;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.client.service.SiteFolderControllerAsync;
import ru.protei.portal.ui.common.client.widget.uploader.AttachmentUploader;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.*;
import java.util.function.Consumer;

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

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());

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

    @Event
    public void onConfirmClicked(ConfirmDialogEvents.Confirm event) {
        if (!Objects.equals(event.identity, getClass().getName())) {
            return;
        }

        savePlatform();
    }

    @Override
    public void onSaveClicked() {
        if (!isValid()) {
            fireEvent(new NotifyEvents.Show(lang.errFieldsRequired(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        if (isNew(platform)) {
            savePlatform();
            return;
        }

        if (!companyChanged()) {
            savePlatform();
        } else {
            siteFolderController.getConnectedIssues(platform.getId(), new FluentCallback<List<Long>>()
                    .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.siteFolderPlatformNotSaved(), NotifyEvents.NotifyType.ERROR)))
                    .withSuccess(caseNumbers -> {
                        if (caseNumbers.isEmpty()) {
                            savePlatform();
                        } else {
                            fireEvent(new ConfirmDialogEvents.Show(getClass().getName(), lang.connectedIssuesExistDialog(reduceCaseNumbers(caseNumbers))));
                        }
                    })
            );
        }
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

    @Override
    public void refreshProjectSpecificFields() {
        if (view.project().getValue() == null) {
            clearProjectSpecificFields();
            return;
        }
        projectRequest(view.project().getValue().getId(), this::fillProjectSpecificFieldsOnRefresh);
    }

    private void savePlatform() {
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
                fireEvent(isNew(platform) ? new SiteFolderPlatformEvents.Show(true) : new Back());
                fireEvent(new NotifyEvents.Show(lang.siteFolderPlatformSaved(), NotifyEvents.NotifyType.SUCCESS));
            }
        });
    }

    private boolean isNew(Platform platform) {
        return platform.getId() == null;
    }

    private boolean companyChanged() {
        if (view.company().getValue() == null && platform.getCompanyId() != null) {
            return true;
        }

        if (view.company().getValue() != null && !Objects.equals(view.company().getValue().getId(), platform.getCompanyId())) {
            return true;
        }

        return false;
    }

    private void fillProjectSpecificFieldsOnRefresh(Project project) {
        view.company().setValue(project.getContragent());
        view.name().setValue(project.getContragent() == null ? null : project.getContragent().getDisplayText());
        view.manager().setValue(project.getManager() == null ? null : new PersonShortView(project.getManager()));
        view.companyEnabled().setEnabled(false);
        view.managerEnabled().setEnabled(false);
        view.companyValidator().setValid(true);
        fireShowCompanyContacts(project.getContragent().getId());
    }

    private void fillProjectSpecificFieldsOnLoad(Project project){
        view.company().setValue(project.getContragent());
        view.manager().setValue(project.getManager() == null ? null : new PersonShortView(project.getManager()));
        view.companyEnabled().setEnabled(false);
        view.managerEnabled().setEnabled(false);
        view.companyValidator().setValid(true);
        view.project().setValue(new EntityOption(project.getName(), project.getId()));
        fireShowCompanyContacts(project.getContragent().getId());
    }

    private void projectRequest(Long projectId, Consumer<Project> consumer) {
        regionService.getProjectInfo(projectId, new FluentCallback<Project>().withSuccess(consumer));
    }

    private void clearProjectSpecificFields() {
        view.company().setValue(null);
        view.manager().setValue(null);
        view.name().setValue(null);
        view.companyEnabled().setEnabled(true);
        view.managerEnabled().setEnabled(true);
        view.companyValidator().setValid(false);
    }

    private void fillView(Platform platform) {
        this.platform = platform;
        view.setPlatformIndependentProjects(true);
        boolean isNotNew = platform.getId() != null;
        boolean isCreatePrivilegeGranted = policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_CREATE);
        if (platform.getProjectId() != null){
            projectRequest(platform.getProjectId(), this::fillProjectSpecificFieldsOnLoad);
        }
        else{
            view.project().setValue(null);
            clearProjectSpecificFields();
            view.company().setValue(EntityOption.fromCompany(platform.getCompany()));
            view.manager().setValue(platform.getManager() == null ? null : platform.getManager().toShortNameShortView());
            fireShowCompanyContacts(platform.getCompanyId());
        }
        view.name().setValue(platform.getName());
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
        platform.setParams(view.parameters().getValue());
        platform.setComment(view.comment().getValue());
        if (view.project().getValue() == null){
            platform.setProjectId(null);
            platform.setCompanyId(view.company().getValue().getId());
            platform.setManager(Person.fromPersonShortView(view.manager().getValue()));
        }
        else {
            platform.setProjectId(view.project().getValue().getId());
            platform.setCompany(null);
            platform.setCompanyId(null);
            platform.setManager(null);
        }
    }

    private boolean isValid() {
        if (view.project().getValue() != null)
            return view.nameValidator().isValid();
        else
            return view.nameValidator().isValid() && view.companyValidator().isValid();
    }

    private void addAttachmentsToCase(Collection<Attachment> attachments) {

        view.attachmentsContainer().add(attachments);

        if (CollectionUtils.isEmpty(platform.getAttachments())) {
            platform.setAttachments(new ArrayList<>());
        }
        platform.getAttachments().addAll(attachments);
    }

    private String reduceCaseNumbers(List<Long> caseNumbers) {
        return caseNumbers.stream()
                .map(String::valueOf)
                .reduce((num1, num2) -> num1 + ", " + num2)
                .orElse("");
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
    @Inject
    RegionControllerAsync regionService;

    private Platform platform;
    private AppEvents.InitDetails initDetails;
}
