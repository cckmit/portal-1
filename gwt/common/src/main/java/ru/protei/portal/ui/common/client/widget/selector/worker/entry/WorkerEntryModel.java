package ru.protei.portal.ui.common.client.widget.selector.worker.entry;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.view.WorkerEntryShortView;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.selector.AsyncSelectorModel;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCache;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCacheLoadHandler;
import ru.protei.portal.ui.common.client.service.EmployeeControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

public abstract class WorkerEntryModel implements Activity, AsyncSelectorModel<WorkerEntryShortView> {

    @Event
    public void onInit(AuthEvents.Success event) {
        cache.setLoadHandler(makeLoadHandler());
        cache.clearCache();
    }

    @Override
    public WorkerEntryShortView get(int elementIndex, LoadingHandler selector) {
        return cache.get(elementIndex, selector);
    }

    public void clear() {
        cache.clearCache();
    }

    private SelectorDataCacheLoadHandler<WorkerEntryShortView> makeLoadHandler() {
        return (offset, limit, handler) -> employeeController.getWorkerEntryList(offset, limit, new FluentCallback<List<WorkerEntryShortView>>()
                .withSuccess(handler::onSuccess));
    }

    @Inject
    EmployeeControllerAsync employeeController;

    private SelectorDataCache<WorkerEntryShortView> cache = new SelectorDataCache<>();
}
