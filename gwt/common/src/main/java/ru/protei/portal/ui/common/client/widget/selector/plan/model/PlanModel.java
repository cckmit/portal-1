package ru.protei.portal.ui.common.client.widget.selector.plan.model;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.query.PlanQuery;
import ru.protei.portal.core.model.view.PlanOption;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.PlanEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.AsyncSearchSelectorModel;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCache;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCacheLoadHandler;
import ru.protei.portal.ui.common.client.service.PlanControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.List;

public abstract class PlanModel implements Activity, AsyncSearchSelectorModel<PlanOption> {
    public PlanModel() {
        this.query = makeQuery(null);
        cache.setLoadHandler(makeLoadHandler(query));
    }

    @Event
    public void onInit(AuthEvents.Success event) {
        cache.clearCache();
    }

    @Event
    public void onPlanChanged(PlanEvents.ChangeModel event) {
        cache.clearCache();
    }

    @Override
    public PlanOption get(int elementIndex, LoadingHandler loadingHandler) {
        return cache.get(elementIndex, loadingHandler);
    }

    @Override
    public void setSearchString(String searchString) {
        cache.clearCache();
        cache.setLoadHandler(makeLoadHandler(makeQuery(searchString)));
    }

    public void setCreatorId(Long creatorId) {
        cache.clearCache();
        query.setCreatorId(creatorId);
    }

    private SelectorDataCacheLoadHandler<PlanOption> makeLoadHandler(final PlanQuery query) {
        return (offset, limit, asyncCallback) -> {
            query.setOffset(offset);
            query.setLimit(limit);
            query.setSortField(En_SortField.start_date);
            query.setSortDir(En_SortDir.DESC);

            planService.getPlanOptionList(query, new RequestCallback<List<PlanOption>>() {
                @Override
                public void onError(Throwable throwable) {
                    fireEvent(new NotifyEvents.Show(lang.errGetConcreteList(lang.plans()), NotifyEvents.NotifyType.ERROR));
                }

                @Override
                public void onSuccess(List<PlanOption> plans) {
                    asyncCallback.onSuccess(plans);
                }
            });
        };
    }

    private PlanQuery makeQuery(String searchString) {
        PlanQuery planQuery = new PlanQuery();
        planQuery.setSearchString(searchString);

        return planQuery;
    }

    @Inject
    private Lang lang;

    @Inject
    private PlanControllerAsync planService;

    private SelectorDataCache<PlanOption> cache = new SelectorDataCache<>();
    private PlanQuery query = new PlanQuery();
}
