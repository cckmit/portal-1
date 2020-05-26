package ru.protei.portal.ui.sitefolder.client.activity.server.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Server;
import ru.protei.portal.ui.common.client.events.SiteFolderAppEvents;
import ru.protei.portal.ui.common.client.events.SiteFolderServerEvents;

public abstract class ServerPreviewActivity implements Activity, AbstractServerPreviewActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow(SiteFolderServerEvents.ShowPreview event) {
        event.parent.clear();
        event.parent.add(view.asWidget());

        serverId = event.server.getId();

        fillView(event.server);
    }

    private void fillView(Server value) {
        if (value == null) {
            return;
        }
        view.setName(value.getName() == null ? "" : value.getName());
        view.setPlatform(value.getPlatform() == null ? "" : (value.getPlatform().getName() == null ? "" : value.getPlatform().getName()));
        view.setIp(value.getIp() == null ? "" : value.getIp());
        view.setParameters(value.getParams() == null ? "" : value.getParams());
        view.setComment(value.getComment() == null ? "" : value.getComment());
    }

    @Override
    public void onOpenAppsClicked() {
        if (serverId != null) {
            fireEvent(new SiteFolderAppEvents.Show(serverId, false));
        }
    }

    @Inject
    AbstractServerPreviewView view;

    private Long serverId;
}
