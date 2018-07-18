package ru.protei.portal.ui.sitefolder.client.view.server.widget.selector;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.query.ServerQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.SiteFolderEvents;
import ru.protei.portal.ui.common.client.service.SiteFolderControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.List;

public abstract class ServerModel implements Activity {

    @Event
    public void onInit(AuthEvents.Success event) {
        refreshOptions();
    }

    @Event
    public void onServerListChanged(SiteFolderEvents.Server.ChangeModel event) {
        refreshOptions();
    }

    public void subscribe(ModelSelector<EntityOption> selector) {
        subscribers.add(selector);
        selector.fillOptions(list);
    }

    public void setPlatformId(Long platformId) {
        this.platformId = platformId;
        refreshOptions();
    }

    private void notifySubscribers() {
        for (ModelSelector<EntityOption> selector : subscribers) {
            selector.fillOptions(list);
            selector.refreshValue();
        }
    }

    private void refreshOptions() {
        ServerQuery query = new ServerQuery();
        query.setPlatformId(platformId);
        siteFolderController.getServersOptionList(query, new RequestCallback<List<EntityOption>>() {
            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onSuccess(List<EntityOption> result) {
                list.clear();
                list.addAll(result);
                notifySubscribers();
            }
        });
    }

    @Inject
    SiteFolderControllerAsync siteFolderController;

    private Long platformId = null;
    private List<EntityOption> list = new ArrayList<>();
    private List<ModelSelector<EntityOption>> subscribers = new ArrayList<>();
}
