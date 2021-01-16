package ru.protei.portal.ui.common.client.widget.selector.region;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.AsyncSelectorModel;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCache;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCacheLoadHandler;
import ru.protei.portal.ui.common.client.service.CaseStateControllerAsync;

import java.util.List;

/**
 * Модель состояния проекта
 */
public abstract class RegionStateModel implements AsyncSelectorModel<CaseState>, Activity {

    public RegionStateModel() {
        cache.setLoadHandler(makeLoadHandler());
    }

    @Event
    public void onInit(AuthEvents.Success event) {
        cache.clearCache();
    }

    @Event
    public void onStateListChanged(IssueEvents.ChangeStateModel event) {
        cache.clearCache();
    }

    @Override
    public CaseState get(int elementIndex, LoadingHandler loadingHandler) {
        return cache.get(elementIndex, loadingHandler);
    }

    private SelectorDataCacheLoadHandler<CaseState> makeLoadHandler() {
        return new SelectorDataCacheLoadHandler() {
            @Override
            public void loadData(int offset, int limit, AsyncCallback handler) {
                caseStateService.getCaseStates(En_CaseType.PROJECT, new AsyncCallback<List<CaseState>>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                    }

                    @Override
                    public void onSuccess(List<CaseState> caseStates) {
                        handler.onSuccess(caseStates);
                    }
                });
            }
        };
    }

    public void clear() {
        cache.clearCache();
    }

    @Inject
    Lang lang;
    @Inject
    CaseStateControllerAsync caseStateService;

    private final SelectorDataCache<CaseState> cache = new SelectorDataCache<>();
}
