package ru.protei.portal.ui.sitefolder.client.view.platform.widget.selector;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.query.PlatformQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.SiteFolderPlatformEvents;
import ru.protei.portal.ui.common.client.service.SiteFolderControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.List;

public abstract class PlatformModel implements Activity {

    @Event
    public void onInit(AuthEvents.Success event) {
        refreshOptions();
    }

    @Event
    public void onPlatformListChanged(SiteFolderPlatformEvents.ChangeModel event) {
        refreshOptions();
    }

    public void subscribe( SelectorWithModel<EntityOption> selector) {
        subscribers.add(selector);
        selector.fillOptions(list);
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
        refreshOptions();
    }

    private void notifySubscribers() {
        for (SelectorWithModel<EntityOption> selector : subscribers) {
            selector.fillOptions(list);
            selector.refreshValue();
        }
    }

    private void refreshOptions() {
        PlatformQuery query = new PlatformQuery();
        query.setCompanyId(companyId);
        siteFolderController.getPlatformsOptionList(query, new RequestCallback<List<EntityOption>>() {
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

    private Long companyId = null;
    private List<EntityOption> list = new ArrayList<>();
    private List<SelectorWithModel<EntityOption>> subscribers = new ArrayList<>();
}
