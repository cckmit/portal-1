package ru.protei.portal.ui.sitefolder.client.view.platform.widget.btnselector;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.core.model.query.PlatformQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.service.SiteFolderControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class PlatformModel implements Activity {

    @Event
    public void onInit(AuthEvents.Success event) {
        refreshOptions();
    }

    public void subscribe(ModelSelector<EntityOption> selector) {
        subscribers.add(selector);
        selector.fillOptions(list);
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
        refreshOptions();
    }

    private void notifySubscribers() {
        for (ModelSelector<EntityOption> selector : subscribers) {
            selector.fillOptions(list);
            selector.refreshValue();
        }
    }

    private void refreshOptions() {
        PlatformQuery query = new PlatformQuery();
        query.setCompanyId(companyId);
        siteFolderController.getPlatforms(query, new RequestCallback<List<Platform>>() {
            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onSuccess(List<Platform> result) {
                list.clear();
                list.addAll(result.stream()
                        .map(p -> new EntityOption(p.getName(), p.getId()))
                        .collect(Collectors.toList())
                );
                notifySubscribers();
            }
        });
    }

    @Inject
    SiteFolderControllerAsync siteFolderController;

    private Long companyId = null;
    private List<EntityOption> list = new ArrayList<>();
    private List<ModelSelector<EntityOption>> subscribers = new ArrayList<>();
}
