package ru.protei.portal.ui.common.client.widget.issueimportance;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.ent.ImportanceLevel;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.AsyncSelectorModel;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCache;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCacheLoadHandler;
import ru.protei.portal.ui.common.client.service.ImportanceLevelControllerAsync;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ImportanceModel implements AsyncSelectorModel<ImportanceLevel>, Activity {

    public ImportanceModel() {
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
    public ImportanceLevel get(int elementIndex, LoadingHandler loadingHandler) {
        return cache.get(elementIndex, loadingHandler);
    }

    public void fillOptions(List<ImportanceLevel> options) {
        clear();
        cache.setLoadHandler(makeLoadHandler(options));
    }

    public SelectorDataCacheLoadHandler<ImportanceLevel> makeLoadHandler(List<ImportanceLevel> options) {
        return (SelectorDataCacheLoadHandler) (offset, limit, handler) -> handler.onSuccess(Collections.singletonList(options));
    }

    private SelectorDataCacheLoadHandler<ImportanceLevel> makeLoadHandler() {
        return new SelectorDataCacheLoadHandler() {
            @Override
            public void loadData(int offset, int limit, AsyncCallback handler) {
                importanceService.getImportanceLevels(new AsyncCallback<List<ImportanceLevel>>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                    }

                    @Override
                    public void onSuccess(List<ImportanceLevel> allImportanceLevels) {
                        List<ImportanceLevel> issueImportanceLevels = allImportanceLevels.stream()
                                                                                         .filter(level -> level.getId() < 5)
                                                                                         .collect(Collectors.toList());
                        handler.onSuccess(issueImportanceLevels);
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
    ImportanceLevelControllerAsync importanceService;

    private final SelectorDataCache<ImportanceLevel> cache = new SelectorDataCache<>();
}
