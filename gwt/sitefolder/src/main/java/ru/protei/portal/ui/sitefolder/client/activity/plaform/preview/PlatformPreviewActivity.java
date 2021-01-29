package ru.protei.portal.ui.sitefolder.client.activity.plaform.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.client.service.SiteFolderControllerAsync;
import ru.protei.portal.ui.common.client.util.ClipboardUtils;
import ru.protei.portal.ui.common.client.util.LinkUtils;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.function.Consumer;

public abstract class PlatformPreviewActivity implements AbstractPlatformPreviewActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow(SiteFolderPlatformEvents.ShowPreview event) {
        event.parent.clear();
        event.parent.add(view.asWidget());

        platformId = event.platform.getId();

        platformRequest(event.platform.getId(), this::fillView);
        view.footerContainerVisibility().setVisible(false);
        view.isFullScreen(false);
    }

    @Event
    public void onShow(SiteFolderPlatformEvents.ShowFullScreen event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.SITE_FOLDER_VIEW)) {
            fireEvent(new ErrorPageEvents.ShowForbidden());
            return;
        }
        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());

        platformId = event.platformId;

        platformRequest(event.platformId, this::fillView);
        view.footerContainerVisibility().setVisible(true);
        view.isFullScreen(true);
    }

    @Event
    public void onInit( AppEvents.InitDetails event ) {
        this.initDetails = event;
    }

    @Override
    public void onOpenServersClicked() {
        if (platformId != null) {
            fireEvent(new SiteFolderServerEvents.Show(platformId, false));
        }
    }

    @Override
    public void onFullScreenClicked() {
        if (platformId != null) {
            fireEvent(new SiteFolderPlatformEvents.ShowFullScreen(platformId));
        }
    }

    @Override
    public void onGoToIssuesClicked() {
        fireEvent(new SiteFolderPlatformEvents.Show(true));
    }

    @Override
    public void onCopyPreviewLinkClicked() {
        copyToClipboardNotify(ClipboardUtils.copyToClipboard(GWT.getHostPageBaseURL() + "#sfplatform_preview:id=" + platformId));
    }

    private void copyToClipboardNotify(Boolean success) {
        if (success) {
            fireSuccessCopyNotify();
        } else {
            fireErrorCopyNotify();
        }
    }

    private void fireSuccessCopyNotify() {
        fireEvent(new NotifyEvents.Show(lang.copiedToClipboardSuccessfully(), NotifyEvents.NotifyType.SUCCESS));
    }

    private void fireErrorCopyNotify() {
        fireEvent( new NotifyEvents.Show( lang.errCopyToClipboard(), NotifyEvents.NotifyType.ERROR ) );
    }


    private void platformRequest(Long platformId, Consumer<Platform> consumer) {
        siteFolderController.getPlatform(platformId, new FluentCallback<Platform>().withSuccess(consumer));
    }

    private void projectRequest(Long projectId, Consumer<ProjectInfo> consumer) {
        regionService.getProjectInfo(projectId, new FluentCallback<ProjectInfo>().withSuccess(consumer));
    }

    private void fillProjectSpecificFields (ProjectInfo project){
        view.setCompany(project.getContragent() == null ? "" : project.getContragent().getDisplayText());
        view.setManager(project.getManager() == null ? null : project.getManager().getDisplayText());
        view.setProject(project.getName(), LinkUtils.makePreviewLink(Project.class, project.getId()));
        view.setTechnicalSupportValidity(formatTechnicalSupportValidityOrErrorMsg(project));
        showContacts(project.getContragent() == null ? null : project.getContragent().getId());
    }

    private void fillView( Platform value ) {
        if (value == null) {
            return;
        }
        view.setName(value.getName() == null ? "" : value.getName());
        view.setParameters(value.getParams() == null ? "" : value.getParams());

        view.setComment(value.getComment() == null ? "" : value.getComment());

        view.attachmentsContainer().clear();
        view.attachmentsContainer().add(value.getAttachments());

        fireEvent(new SiteFolderServerEvents.ShowDetailedList(view.serversContainer(), value.getId()));
        if (value.getProjectId() != null){
            projectRequest(value.getProjectId(), this::fillProjectSpecificFields);
        }
        else {
            view.setProject("", "");
            view.setCompany(value.getCompany() == null ? "" : (value.getCompany().getCname() == null ? "" : value.getCompany().getCname()));
            view.setManager(value.getManager() == null ? "" : (value.getManager().getDisplayShortName() == null ? "" : value.getManager().getDisplayShortName()));
            view.setTechnicalSupportValidity(lang.technicalSupportValidityNotDefined());
            showContacts(value.getCompanyId());
        }
    }

    private void showContacts(Long companyId) {
        if (policyService.hasPrivilegeFor(En_Privilege.CONTACT_VIEW)) {
            fireEvent(new ContactEvents.ShowConciseTable(view.contactsContainer(), companyId).readOnly());
        }
    }

    private String formatTechnicalSupportValidityOrErrorMsg(ProjectInfo projectInfo) {
        if (projectInfo.getTechnicalSupportValidity() != null) {
            return DateTimeFormat.getFormat("dd.MM.yyyy").format(projectInfo.getTechnicalSupportValidity());
        }

        if (projectInfo.getManager() != null) {
            return lang.technicalSupportValidityNotFound(projectInfo.getManager().getDisplayText());
        }

        return "";
    }

    @Inject
    AbstractPlatformPreviewView view;
    @Inject
    SiteFolderControllerAsync siteFolderController;
    @Inject
    RegionControllerAsync regionService;
    @Inject
    PolicyService policyService;
    @Inject
    Lang lang;


    private Long platformId;
    private AppEvents.InitDetails initDetails;
}
