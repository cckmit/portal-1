package ru.protei.portal.ui.common.client.widget.selector.contract;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.query.ContractQuery;
import ru.protei.portal.core.model.struct.ContractInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.ContractEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.AsyncSelectorModel;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCache;
import ru.protei.portal.ui.common.client.selector.cache.SelectorDataCacheLoadHandler;
import ru.protei.portal.ui.common.client.selector.pageable.SelectorItemRenderer;
import ru.protei.portal.ui.common.client.service.ContractControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.stream.Collectors;

public abstract class ContractModel implements Activity, AsyncSelectorModel<ContractInfo>, SelectorItemRenderer<ContractInfo> {

    @Event
    public void onInit(AuthEvents.Success event) {
        cache.clearCache();
    }

    @Event
    public void onContractListChanged(ContractEvents.ChangeModel event) {
        cache.clearCache();
    }

    public ContractModel() {
        query = makeQuery();
        cache.setLoadHandler(makeLoadHandler(query));
    }

    @Override
    public ContractInfo get(int elementIndex, LoadingHandler loadingHandler) {
        return cache.get(elementIndex, loadingHandler);
    }

    @Override
    public String getElementName(ContractInfo value) {
        return value == null ? "" : value.getNumber();
    }

    public void setProject(Long projectId) {
        cache.clearCache();
        query.setProjectId(projectId);
    }

    private ContractQuery makeQuery() {
        return new ContractQuery();
    }

    private SelectorDataCacheLoadHandler<ContractInfo> makeLoadHandler(final ContractQuery query) {
        return (offset, limit, handler) -> {
            query.setOffset(offset);
            query.setLimit(limit);
            contractController.getContracts(query, new FluentCallback<SearchResult<Contract>>()
                    .withError(throwable -> {
                        fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                        handler.onFailure( throwable );
                    })
                    .withSuccess(contracts -> {
                        handler.onSuccess(contracts.getResults().stream()
                                .map(Contract::toContactInfo)
                                .collect(Collectors.toList()));
                    })
            );
        };
    }

    @Inject
    Lang lang;
    @Inject
    ContractControllerAsync contractController;

    private ContractQuery query;

    private SelectorDataCache<ContractInfo> cache = new SelectorDataCache<>();
}
