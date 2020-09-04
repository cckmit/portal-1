package ru.protei.portal.ui.common.client.widget.selector.login;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.dict.En_AdminState;
import ru.protei.portal.core.model.ent.UserLoginShortView;
import ru.protei.portal.core.model.query.UserLoginShortViewQuery;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.AsyncSearchSelectorModel;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCacheLoadHandler;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCacheWithFirstElements;
import ru.protei.portal.ui.common.client.service.AccountControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public abstract class UserLoginModel implements AsyncSearchSelectorModel<UserLoginShortView>, Activity {
    @Inject
    public void init() {
        cache.clearCache();
        cache.setLoadHandler(makeLoadHandler(makeQuery(null)));
    }

    @Override
    public UserLoginShortView get(int elementIndex, LoadingHandler handler) {
        return cache.get(elementIndex, handler);
    }

    @Override
    public void setSearchString(String searchString) {
        cache.clearCache();
        cache.setLoadHandler(makeLoadHandler(makeQuery(searchString)));
    }

    public void setPersonFirstId(Long personId) {
        if (personId == null) {
            return;
        }

        UserLoginShortViewQuery accountQuery = new UserLoginShortViewQuery();
        accountQuery.setAdminState(En_AdminState.UNLOCKED);
        accountQuery.setPersonIds(new HashSet<>(Collections.singleton(personId)));

        accountService.getUserLoginShortViewList(accountQuery, new FluentCallback<List<UserLoginShortView>>()
                .withSuccess(result -> cache.setFirstElements(result))
        );
    }

    private SelectorDataCacheLoadHandler<UserLoginShortView> makeLoadHandler(UserLoginShortViewQuery query) {
        return (offset, limit, handler) -> {
            query.setOffset(offset);
            query.setLimit(limit);
            accountService.getUserLoginShortViewList(query, new FluentCallback<List<UserLoginShortView>>()
                    .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR)))
                    .withSuccess(handler::onSuccess)
            );
        };
    }

    private UserLoginShortViewQuery makeQuery(String searchString) {
        UserLoginShortViewQuery accountQuery = new UserLoginShortViewQuery();
        accountQuery.setSearchString(searchString);
        accountQuery.setAdminState(En_AdminState.UNLOCKED);

        return accountQuery;
    }

    @Inject
    AccountControllerAsync accountService;

    @Inject
    Lang lang;

    private SelectorDataCacheWithFirstElements<UserLoginShortView> cache = new SelectorDataCacheWithFirstElements<>();
}
