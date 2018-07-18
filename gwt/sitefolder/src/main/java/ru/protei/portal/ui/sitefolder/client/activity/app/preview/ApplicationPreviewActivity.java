package ru.protei.portal.ui.sitefolder.client.activity.app.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Application;
import ru.protei.portal.ui.common.client.events.SiteFolderEvents;

public abstract class ApplicationPreviewActivity implements Activity, AbstractApplicationPreviewActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow(SiteFolderEvents.App.ShowPreview event) {
        event.parent.clear();
        event.parent.add(view.asWidget());

        fillView(event.app);
    }

    private void fillView(Application value) {
        if (value == null) {
            return;
        }
        view.setName(value.getName() == null ? "" : value.getName());
        view.setServer(value.getServer() == null ? "" : (value.getServer().getName() == null ? "" : value.getServer().getName()));
        view.setComment(value.getComment() == null ? "" : value.getComment());
        view.setPaths(value.getPaths());
    }

    @Inject
    AbstractApplicationPreviewView view;
}

