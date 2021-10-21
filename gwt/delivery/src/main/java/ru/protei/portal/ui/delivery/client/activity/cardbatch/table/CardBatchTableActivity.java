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
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CardBatchControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.filter.AbstractCardBatchFilterActivity;
import ru.protei.portal.ui.delivery.client.activity.cardbatch.filter.AbstractCardBatchFilterView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.nullIfEmpty;
import static ru.protei.portal.core.model.helper.CollectionUtils.toList;
import static ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType.toDateRange;

public abstract class CardBatchTableActivity implements AbstractCardBatchTableActivity, AbstractPagerActivity,
        AbstractCardBatchFilterActivity, Activity {

    @PostConstruct
    public void onInit() {
        CREATE_ACTION = lang.buttonCreate();
        view.setActivity(this);
        view.setAnimation(animation);

        pagerView.setActivity(this);
        filterView.setActivity(this);
        view.getFilterContainer().add(filterView.asWidget());
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        filterView.resetFilter();
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(CardBatchEvents.Show event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.DELIVERY_VIEW)) {
            fireEvent(new ErrorPageEvents.ShowForbidden(initDetails.parent));
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
        if (policyService.hasPrivilegeFor(En_Privilege.DELIVERY_CREATE)) {
            fireEvent(new ActionBarEvents.Add(CREATE_ACTION , null, UiConstants.ActionBarIdentity.CARD_BATCH_CREATE));
        }

        this.preScroll = event.preScroll;

        loadTable();
    }

    @Event
    public void onCreateClicked(ActionBarEvents.Clicked event) {
        if (!(UiConstants.ActionBarIdentity.CARD_BATCH_CREATE.equals(event.identity)) ) {
            return;
        }

        view.clearSelection();

        fireEvent(new CardBatchEvents.Create());
    }

    @Event
    public void onChangeRow(CardBatchEvents.Change event) {
        cardBatchController.getCardBatch(event.id, new FluentCallback<CardBatch>()
                .withSuccess(cardBatch -> view.updateRow(cardBatch))
        );
    }

    @Override
    public void loadData(int offset, int limit, AsyncCallback<List<CardBatch>> asyncCallback) {
        boolean isFirstChunk = offset == 0;
        CardBatchQuery query = makeQuery();
        query.setOffset(offset);
        query.setLimit(limit);

        cardBatchController.getCardBatchesList(query, new FluentCallback<SearchResult<CardBatch>>()
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

    @Override
    public void onRemoveClicked(CardBatch value) {
        if (value == null) {
            return;
        }

        fireEvent(new ConfirmDialogEvents.Show(lang.cardBatchRemoveConfirmMessage(value.getTypeName(), value.getNumber()),
                removeAction(value)));
    }

    private Runnable removeAction(CardBatch value) {
        return () -> cardBatchController.removeCardBatch(value, new FluentCallback<CardBatch>()
                .withSuccess(result -> {
                    fireEvent(new NotifyEvents.Show(lang.cardBatchRemoved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new CardBatchEvents.Show(false));
                })
        );
    }

    private void loadTable() {
        animation.closeDetails();
        view.clearRecords();
        view.triggerTableLoad();
    }

    @Override
    public void onItemClicked(CardBatch value) {
        persistScroll();
        showPreview(value);
    }

    @Override
    public void onEditClicked(CardBatch value) {
        persistScroll();
        fireEvent(new CardBatchEvents.Edit(value.getId()));
    }

    private void showPreview(CardBatch value) {
        if (value == null || value.getId() == null) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent(new CardBatchEvents.ShowPreview(view.getPreviewContainer(), value.getId()));
        }
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

    private void persistScroll() {
        scrollTo = Window.getScrollTop();
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
    Lang lang;
    @Inject
    AbstractCardBatchTableView view;
    @Inject
    AbstractCardBatchFilterView filterView;
    @Inject
    CardBatchControllerAsync cardBatchController;
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

    private static String CREATE_ACTION;
}
