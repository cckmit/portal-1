package ru.protei.portal.ui.sitefolder.client.activity.plaform.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.core.model.struct.Project;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.client.service.SiteFolderControllerAsync;
import ru.protei.portal.ui.common.client.util.ProjectUtils;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.function.Consumer;

public abstract class PlatformPreviewActivity implements Activity, AbstractPlatformPreviewActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow(SiteFolderPlatformEvents.ShowPreview event) {
        event.parent.clear();
        event.parent.add(view.asWidget());

        platformId = event.platform.getId();
        projectId = event.platform.getProjectId();

        platformRequest(event.platform.getId(), this::fillView);
        view.footerContainerVisibility().setVisible(false);
    }

    @Event
    public void onShow(SiteFolderPlatformEvents.ShowFullScreen event) {
        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());

        platformRequest(event.platformId, this::fillView);
        view.footerContainerVisibility().setVisible(true);
    }

    @Event
    public void onInit( AppEvents.InitDetails event ) {
        this.initDetails = event;
    }

    private void platformRequest(Long platformId, Consumer<Platform> consumer) {
        siteFolderController.getPlatform(platformId, new FluentCallback<Platform>().withSuccess(consumer));
    }

    private void projectRequest(Long projectId, Consumer<Project> consumer) {
        regionService.getProjectInfo(projectId, new FluentCallback<Project>().withSuccess(consumer));
    }

    private void fillProjectSpecificFields (Project project){
        view.setCompany(project.getContragent() == null ? "" : project.getContragent().getDisplayText());
        view.setManager(project.getManager() == null ? null : project.getManager().getDisplayText());
        if (project.getContragent() != null) fireEvent(new ContactEvents.ShowConciseTable(view.contactsContainer(), project.getContragent().getId()).readOnly());
        else fireEvent(new ContactEvents.ShowConciseTable(view.contactsContainer(), null));
    }


    private void fillView( Platform value ) {
        if (value == null) {
            return;
        }
        view.setName(value.getName() == null ? "" : value.getName());
        view.setParameters(value.getParams() == null ? "" : value.getParams());
        view.setProject(value.getProjectName() == null ? "" : value.getProjectName(), ProjectUtils.makeLink(value.getProjectId()));
        view.setComment(value.getComment() == null ? "" : value.getComment());

        view.attachmentsContainer().clear();
        view.attachmentsContainer().add(value.getAttachments());

        fireEvent(new SiteFolderServerEvents.ShowDetailedList(view.serversContainer(), value.getId()));
        if (value.getProjectId() != null){
            projectRequest(value.getProjectId(), this::fillProjectSpecificFields);
        }
        else {
            view.setCompany(value.getCompany() == null ? "" : (value.getCompany().getCname() == null ? "" : value.getCompany().getCname()));
            view.setManager(value.getManager() == null ? "" : (value.getManager().getDisplayShortName() == null ? "" : value.getManager().getDisplayShortName()));
            fireEvent(new ContactEvents.ShowConciseTable(view.contactsContainer(), value.getCompanyId()).readOnly());
        }
    }

    @Override
    public void onOpenServersClicked() {
        if (platformId != null) {
            fireEvent(new SiteFolderServerEvents.Show(platformId));
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
        fireEvent(new SiteFolderPlatformEvents.Show());
    }

    @Override
    public void onProjectClicked() {
        if (projectId != null) {
            fireEvent(new ProjectEvents.ShowFullScreen(projectId));
        }
    }

    @Inject
    AbstractPlatformPreviewView view;
    @Inject
    SiteFolderControllerAsync siteFolderController;
    @Inject
    RegionControllerAsync regionService;


    private Long platformId;
    private Long projectId;
    private AppEvents.InitDetails initDetails;
}
