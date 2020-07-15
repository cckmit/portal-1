package ru.protei.portal.ui.common.client.widget.selector.person;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.AsyncSelectorModel;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCache;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCacheLoadHandler;
import ru.protei.portal.ui.common.client.service.EmployeeControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

public abstract class EmployeeCustomModel implements Activity, AsyncSelectorModel<PersonShortView> {

    @Inject
    public void init() {
        setLoadHandler(getQuery());
    }

    @Override
    public PersonShortView get(int elementIndex, LoadingHandler loadingHandler) {
        return cache.get(elementIndex, loadingHandler);
    }

    public void setEmployeeQuery(EmployeeQuery query) {
        query.setSortField(En_SortField.person_full_name);
        query.setSortDir(En_SortDir.ASC);
        this.query = query;
        setLoadHandler(getQuery());
    }

    public void clear() {
        cache.clearCache();
    }

    private void setLoadHandler(EmployeeQuery query) {
        cache.clearCache();
        cache.setLoadHandler(makeLoadHandler(query));
    }

    private SelectorDataCacheLoadHandler<PersonShortView> makeLoadHandler(EmployeeQuery query) {
        return (offset, limit, handler) -> {
            query.setOffset(offset);
            query.setLimit(limit);
            employeeController.getEmployeeViewList(query, new FluentCallback<List<PersonShortView>>()
                    .withErrorMessage(lang.errGetList(), NotifyEvents.NotifyType.ERROR)
                    .withSuccess(handler::onSuccess));
        };
    }

    @Inject
    EmployeeControllerAsync employeeController;
    @Inject
    Lang lang;

    private EmployeeQuery getQuery() {
        if (query == null) {
            query = new EmployeeQuery(null, false, true, En_SortField.person_full_name, En_SortDir.ASC);
        }
        return query;
    }

    private EmployeeQuery query;
    private SelectorDataCache<PersonShortView> cache = new SelectorDataCache<>();
}
