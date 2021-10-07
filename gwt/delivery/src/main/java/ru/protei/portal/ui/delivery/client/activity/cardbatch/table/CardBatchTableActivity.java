package ru.protei.portal.ui.delivery.client.activity.cardbatch.table;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.query.CardBatchQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.ConfigStorage;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.service.CardBatchControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.filter.AbstractCardBatchFilterActivity;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.filter.AbstractCardBatchFilterView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType.toDateRange;

public abstract class CardBatchTableActivity implements AbstractCardBatchTableActivity, AbstractPagerActivity,
        AbstractCardBatchFilterActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
        view.setAnimation( animation );

        pagerView.setActivity( this );
        filterView.setActivity( this );
        view.getFilterContainer().add( filterView.asWidget() );
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        filterView.resetFilter();
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Event(Type.FILL_CONTENT)
    public void onShow( CardBatchEvents.Show event ) {
        if (!policyService.hasPrivilegeFor(En_Privilege.DELIVERY_VIEW)) {
            fireEvent(new ErrorPageEvents.ShowForbidden());
            return;
        }

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());

        if(configStorage.getConfigData().cardbatchCompanyPartnerId != null) {
            filterView.setContractorFilter(configStorage.getConfigData().cardbatchCompanyPartnerId);
        }
        view.getFilterContainer().add(filterView.asWidget());
        view.getPagerContainer().add(pagerView.asWidget());

        fireEvent(new ActionBarEvents.Clear());

        this.preScroll = event.preScroll;

        loadTable();
    }

    @Override
    public void loadData(int offset, int limit, AsyncCallback<List<CardBatch>> asyncCallback) {
        boolean isFirstChunk = offset == 0;
        CardBatchQuery query = makeQuery();
        query.setOffset(offset);
        query.setLimit(limit);

        cardController.getCardBatchesList(query, new FluentCallback<SearchResult<CardBatch>>()
                .withError(throwable -> {
                    errorHandler.accept(throwable);
                    asyncCallback.onFailure(throwable);
                })
                .withSuccess(sr -> {
                    if (isFirstChunk) {
                        view.setTotalRecords(sr.getTotalCount());
                        pagerView.setTotalPages(view.getPageCount());
                        pagerView.setTotalCount(sr.getTotalCount());
                        restoreScroll();
                    }
                    asyncCallback.onSuccess(sr.getResults());
                }));
    }

    @Override
    public void onFilterChanged() {
        loadTable();
    }

    @Override
    public void onPageChanged(int page) {
        pagerView.setCurrentPage(page);
    }

    @Override
    public void onPageSelected(int page) {
        view.scrollTo(page);
    }

    private void loadTable() {
        animation.closeDetails();
        view.clearRecords();
        view.triggerTableLoad();
    }

    @Override
    public void onItemClicked(CardBatch value) {
        fireEvent(new CardBatchEvents.Edit(value.getId()));
    }

    private CardBatchQuery makeQuery() {
        CardBatchQuery query = new CardBatchQuery();
        query.setSearchString(filterView.search().getValue());
        query.setTypeIds(nullIfEmpty(toList(filterView.cardTypes().getValue(), EntityOption::getId)));
        query.setStateIds(nullIfEmpty(toList(filterView.states().getValue(), CaseState::getId)));
        query.setImportanceIds(nullIfEmpty(filterView.importance().getValue()));
        query.setContractors(nullIfEmpty(filterView.contractors().getValue()
                                                                 .stream().map(person -> person.getId())
                                                                 .collect(Collectors.toList())));

        query.setDeadline(toDateRange(filterView.deadline().getValue()));
        query.setSortDir(filterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);
        query.setSortField(filterView.sortField().getValue());
        return query;
    }

    private void restoreScroll() {
        if (!preScroll) {
            view.clearSelection();
            return;
        }

        Window.scrollTo(0, scrollTo);
        preScroll = false;
        scrollTo = 0;
    }

    @Inject
    AbstractCardBatchTableView view;
    @Inject
    AbstractCardBatchFilterView filterView;
    @Inject
    CardBatchControllerAsync cardController;
    @Inject
    TableAnimation animation;
    @Inject
    PolicyService policyService;
    @Inject
    DefaultErrorHandler errorHandler;
    @Inject
    AbstractPagerView pagerView;
    @Inject
    ConfigStorage configStorage;

    private AppEvents.InitDetails initDetails;
    private Integer scrollTo = 0;
    private Boolean preScroll = false;
}
