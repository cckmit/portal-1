package ru.protei.portal.ui.sitefolder.client.widget.selector.servergroup;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.ent.ServerGroup;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.AsyncSelectorModel;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCache;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCacheLoadHandler;
import ru.protei.portal.ui.common.client.service.SiteFolderControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.sitefolder.client.events.ServerGroupEvents;

import java.util.ArrayList;
import java.util.List;

public abstract class ServerGroupModel implements Activity, AsyncSelectorModel<ServerGroup> {
    @Event
    public void onAuthSuccess( AuthEvents.Success event ) {
        cache.clearCache();
        cache.setLoadHandler(makeLoadHandler(null));
    }

    @Override
    public ServerGroup get(int elementIndex, LoadingHandler loadingHandler) {
        return cache.get( elementIndex, loadingHandler );
    }

    public void setPlatformId(Long platformId) {
        cache.clearCache();
        cache.setLoadHandler(makeLoadHandler(platformId));
    }

    public void clearCache() {
        cache.clearCache();
    }

    private SelectorDataCacheLoadHandler<ServerGroup> makeLoadHandler(Long platformId) {
        return (offset, limit, handler) -> {
            if (platformId == null) {
                handler.onSuccess(new ArrayList<>());
                return;
            }

            serverGroupService.getServerGroups(platformId, limit, offset, new FluentCallback<List<ServerGroup>>()
                    .withError(throwable -> {
                        fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) );
                        handler.onFailure( throwable );
                    })
                    .withSuccess(handler::onSuccess)
            );
        };
    }

    @Inject
    Lang lang;

    @Inject
    SiteFolderControllerAsync serverGroupService;

    private SelectorDataCache<ServerGroup> cache = new SelectorDataCache<>();
}
