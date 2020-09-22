package ru.protei.portal.ui.common.client.widget.selector.project;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.AsyncSearchSelectorModel;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCache;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCacheLoadHandler;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

public abstract class AsyncProjectModel implements AsyncSearchSelectorModel<EntityOption>, Activity {
    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        cache.clearCache();
        cache.setLoadHandler(makeLoadHandler(makeQuery(null)));
    }

    @Override
    public EntityOption get(int elementIndex, LoadingHandler selector) {
        return cache.get(elementIndex, selector);
    }

    @Override
    public void setSearchString(String searchString) {
        cache.clearCache();
        cache.setLoadHandler(makeLoadHandler(makeQuery(searchString)));
    }

    private SelectorDataCacheLoadHandler<EntityOption> makeLoadHandler(ProjectQuery query) {
        return (offset, limit, handler) -> {
            query.setOffset(offset);
            query.setLimit(limit);
            regionService.getProjectOptionList(query, new FluentCallback<List<EntityOption>>()
                    .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR)))
                    .withSuccess(handler::onSuccess)
            );
        };
    }

    private ProjectQuery makeQuery(String searchString) {
        ProjectQuery projectQuery = new ProjectQuery();
        projectQuery.setSearchString(searchString);

        return projectQuery;
    }

    @Inject
    RegionControllerAsync regionService;
    @Inject
    Lang lang;

    private SelectorDataCache<EntityOption> cache = new SelectorDataCache<>();
}
