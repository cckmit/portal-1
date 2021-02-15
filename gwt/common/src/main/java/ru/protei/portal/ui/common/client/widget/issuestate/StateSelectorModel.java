package ru.protei.portal.ui.common.client.widget.issuestate;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.AsyncSelectorModel;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCache;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCacheLoadHandler;
import ru.protei.portal.ui.common.client.service.CaseStateControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;
import java.util.stream.Collectors;

public abstract class StateSelectorModel implements Activity, AsyncSelectorModel<EntityOption> {

    public StateSelectorModel() {
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
    public EntityOption get(int elementIndex, LoadingHandler loadingHandler) {
        return cache.get(elementIndex, loadingHandler);
    }

    private SelectorDataCacheLoadHandler makeLoadHandler() {
        return (offset, limit, handler) ->
                caseStateController.getCaseStatesOmitPrivileges(En_CaseType.CRM_SUPPORT, new FluentCallback<List<CaseState>>()
                        .withError(throwable -> {
                            fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                        })
                        .withSuccess(options ->
                            handler.onSuccess(options
                                    .stream()
                                    .map(caseState -> new EntityOption(caseState.getState(), caseState.getId()))
                                    .collect(Collectors.toList()))));
    }

    public void clear() {
        cache.clearCache();
    }

    @Inject
    Lang lang;
    @Inject
    CaseStateControllerAsync caseStateController;

    private SelectorDataCache<EntityOption> cache = new SelectorDataCache<>();
}
