package ru.protei.portal.ui.sitefolder.client.view.server.widget.selector;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.query.ServerQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.AsyncSearchSelectorModel;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCache;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCacheLoadHandler;
import ru.protei.portal.ui.common.client.service.SiteFolderControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

public abstract class AsyncServerModel implements AsyncSearchSelectorModel<EntityOption>, Activity {
    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        cache.clearCache();
        cache.setLoadHandler(makeLoadHandler(makeQuery(null)));
    }

    @Override
    public void setSearchString(String searchString) {
        cache.clearCache();
        cache.setLoadHandler(makeLoadHandler(makeQuery(searchString)));
    }

    @Override
    public EntityOption get(int elementIndex, LoadingHandler selector) {
        return cache.get(elementIndex, selector);
    }

    private SelectorDataCacheLoadHandler<EntityOption> makeLoadHandler(ServerQuery query) {
        return (offset, limit, handler) -> {
            query.setOffset(offset);
            query.setLimit(limit);
            siteFolderController.getServersOptionList(query, new FluentCallback<List<EntityOption>>()
                    .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR)))
                    .withSuccess(handler::onSuccess));
        };
    }

    private ServerQuery makeQuery(String searchString) {
        ServerQuery serverQuery = new ServerQuery();
        serverQuery.setSearchString(searchString);

        return serverQuery;
    }

    @Inject
    SiteFolderControllerAsync siteFolderController;
    @Inject
    Lang lang;

    private SelectorDataCache<EntityOption> cache = new SelectorDataCache<>();
}
