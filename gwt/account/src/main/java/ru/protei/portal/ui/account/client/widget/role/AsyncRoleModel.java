package ru.protei.portal.ui.account.client.widget.role;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.query.UserRoleQuery;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.AsyncSearchSelectorModel;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCache;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCacheLoadHandler;
import ru.protei.portal.ui.common.client.service.RoleControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

public abstract class AsyncRoleModel implements AsyncSearchSelectorModel<UserRole>, Activity {
    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        cache.clearCache();
        cache.setLoadHandler(makeLoadHandler(makeQuery(null)));
    }

    @Override
    public UserRole get(int elementIndex, LoadingHandler selector) {
        return cache.get(elementIndex, selector);
    }

    @Override
    public void setSearchString(String searchString) {
        cache.clearCache();
        cache.setLoadHandler(makeLoadHandler(makeQuery(searchString)));
    }

    private SelectorDataCacheLoadHandler<UserRole> makeLoadHandler(UserRoleQuery query) {
        return (offset, limit, handler) -> {
            query.setOffset(offset);
            query.setLimit(limit);
            roleService.getRoles(query, new FluentCallback<List<UserRole>>()
                    .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR)))
                    .withSuccess(handler::onSuccess));
        };
    }

    private UserRoleQuery makeQuery(String searchString) {
        UserRoleQuery userRoleQuery = new UserRoleQuery();
        userRoleQuery.setSearchString(searchString);

        return userRoleQuery;
    }

    @Inject
    RoleControllerAsync roleService;
    @Inject
    Lang lang;

    private SelectorDataCache<UserRole> cache = new SelectorDataCache<>();
}
