package ru.protei.portal.ui.sitefolder.client.activity.plaform.preview;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.ContactEvents;
import ru.protei.portal.ui.common.client.events.SiteFolderPlatformEvents;
import ru.protei.portal.ui.common.client.events.SiteFolderServerEvents;
import ru.protei.portal.ui.common.client.service.SiteFolderControllerAsync;
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

        request(event.platform.getId(), this::fillView);
        view.showFullScreen(false);
    }

    @Event
    public void onShow(SiteFolderPlatformEvents.ShowFullScreen event) {
        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());

        request(event.platformId, this::fillView);
        view.showFullScreen(true);
    }

    @Event
    public void onInit( AppEvents.InitDetails event ) {
        this.initDetails = event;
    }

    private void request(Long platformId, Consumer<Platform> consumer) {
        siteFolderController.getPlatform(platformId, new FluentCallback<Platform>().withSuccess(consumer));
    }

    private void fillView( Platform value ) {
        if (value == null) {
            return;
        }
        view.setName(value.getName() == null ? "" : value.getName());
        view.setCompany(value.getCompany() == null ? "" : (value.getCompany().getCname() == null ? "" : value.getCompany().getCname()));
        view.setManager(value.getManager() == null ? "" : (value.getManager().getDisplayShortName() == null ? "" : value.getManager().getDisplayShortName()));
        view.setParameters(value.getParams() == null ? "" : value.getParams());
        view.setComment(value.getComment() == null ? "" : value.getComment());

        view.attachmentsContainer().clear();
        view.attachmentsContainer().add(value.getAttachments());

        fireEvent(new ContactEvents.ShowConciseTable(view.contactsContainer(), value.getCompanyId()).readOnly());
        fireEvent(new SiteFolderServerEvents.ShowDetailedList(view.serversContainer(), value.getId()));
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

    @Inject
    AbstractPlatformPreviewView view;
    @Inject
    SiteFolderControllerAsync siteFolderController;

    private Long platformId;
    private AppEvents.InitDetails initDetails;
}
