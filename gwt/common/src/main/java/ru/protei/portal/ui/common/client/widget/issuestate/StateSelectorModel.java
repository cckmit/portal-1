package ru.protei.portal.ui.common.client.widget.issuestate;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.struct.CaseStateAndWorkflowList;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.AsyncSelectorModel;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCache;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCacheLoadHandler;
import ru.protei.portal.ui.common.client.service.CaseStateWorkflowControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

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

    private SelectorDataCacheLoadHandler<EntityOption> makeLoadHandler() {
        return new SelectorDataCacheLoadHandler() {
            @Override
            public void loadData(int offset, int limit, AsyncCallback handler) {
                caseStateWorkflowController.getCaseStateAndWorkflowList(new FluentCallback<CaseStateAndWorkflowList>()
                        .withError(throwable -> {
                            fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                        })
                        .withSuccess(options -> {
                            handler.onSuccess(options.getCaseStatesList()
                                    .stream()
                                    .map(caseState -> new EntityOption(caseState.getState(), caseState.getId()))
                                    .collect(Collectors.toList()));
                        }));
            }
        };
    }

    public void clear() {
        cache.clearCache();
    }

    @Inject
    Lang lang;
    @Inject
    CaseStateWorkflowControllerAsync caseStateWorkflowController;

    private SelectorDataCache<EntityOption> cache = new SelectorDataCache<>();
}
