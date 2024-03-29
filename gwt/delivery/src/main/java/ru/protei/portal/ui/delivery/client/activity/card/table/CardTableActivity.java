package ru.protei.portal.ui.delivery.client.activity.card.table;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.ent.Card;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.query.CardQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CardControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.delivery.client.activity.card.filter.AbstractCardFilterActivity;
import ru.protei.portal.ui.delivery.client.activity.card.filter.AbstractCardFilterView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.nullIfEmpty;
import static ru.protei.portal.core.model.helper.CollectionUtils.toList;

public abstract class CardTableActivity implements AbstractCardTableActivity, AbstractPagerActivity,
        AbstractCardFilterActivity, Activity {

    @PostConstruct
    public void onInit() {
        CREATE_ACTION = lang.buttonCreate();

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
    public void onShow( CardEvents.Show event ) {
        if (!policyService.hasPrivilegeFor(En_Privilege.CARD_VIEW)) {
            fireEvent(new ErrorPageEvents.ShowForbidden(initDetails.parent));
            return;
        }

        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );
        view.getPagerContainer().add( pagerView.asWidget() );
        view.getFilterContainer().add( filterView.asWidget() );

        fireEvent(new ActionBarEvents.Clear());
        if (policyService.hasPrivilegeFor(En_Privilege.CARD_CREATE)) {
            fireEvent(new ActionBarEvents.Add(CREATE_ACTION , null, UiConstants.ActionBarIdentity.CARD_CREATE));
        }
        fireEvent(new ActionBarEvents.Add(lang.cardGroupModify(), null, UiConstants.ActionBarIdentity.CARD_GROUP_MODIFY));
        view.clearSelectedRows();

        this.preScroll = event.preScroll;

        loadTable();
    }

    @Event
    public void onCreateClicked(ActionBarEvents.Clicked event) {
        if (!(UiConstants.ActionBarIdentity.CARD_CREATE.equals(event.identity)) ) {
            return;
        }

        view.clearSelection();

        fireEvent(new CardEvents.Create(null));
    }

    @Event
    public void onGroupModifyClicked(ActionBarEvents.Clicked event) {
        if (!(UiConstants.ActionBarIdentity.CARD_GROUP_MODIFY.equals(event.identity)) ) {
            return;
        }

        fireEvent(new CardEvents.GroupEdit(view.getSelectedCards()));
    }


    @Event
    public void onGroupUpdate(CardEvents.GroupChanged event) {
        loadTable();
    }

    @Event
    public void onUpdate(CardEvents.Change event) {
        if (view.asWidget().isAttached()) {
            view.updateRow(event.card);
        }
    }

    @Override
    public void onItemClicked(Card value) {
        persistScroll();
        showPreview(value);
    }

    @Override
    public void onEditClicked(Card value) {
        persistScroll();
        fireEvent(new CardEvents.Edit(value.getId()));
    }

    @Override
    public void loadData(int offset, int limit, AsyncCallback<List<Card>> asyncCallback) {
        boolean isFirstChunk = offset == 0;
        CardQuery query = makeQuery();
        query.setOffset(offset);
        query.setLimit(limit);

        cardController.getCards(query, new FluentCallback<SearchResult<Card>>()
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
                        view.clearSelectedRows();
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
    public void onRemoveClicked(Card value) {
        if (value == null) {
            return;
        }

        fireEvent(new ConfirmDialogEvents.Show(lang.cardRemoveConfirmMessage(value.getSerialNumber()),
                removeAction(value)));

    }

    @Override
    public void setGroupButtonEnabled(boolean isEnabled) {

    }

    @Override
    public void onCheckCardClicked() {
        fireEvent(new ActionBarEvents.SetButtonEnabled( UiConstants.ActionBarIdentity.CARD_GROUP_MODIFY,
                !view.getSelectedCards().isEmpty() ));
    }

    private void showPreview(Card value) {
        if (value == null || value.getId() == null) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent(new CardEvents.ShowPreview(view.getPreviewContainer(), value.getId()));
        }
    }

    private Runnable removeAction(Card card) {
        return () -> cardController.removeCard(card, new FluentCallback<Card>()
                .withSuccess(result -> {
                    fireEvent(new NotifyEvents.Show(lang.cardRemoveSucceeded(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new CardEvents.Show(false));
                })
        );
    }

    private void loadTable() {
        animation.closeDetails();
        view.clearRecords();
        view.triggerTableLoad();
        fireEvent(new ActionBarEvents.SetButtonEnabled( UiConstants.ActionBarIdentity.CARD_GROUP_MODIFY, false));
    }

    private CardQuery makeQuery() {
        CardQuery query = new CardQuery();
        query.setSearchString(filterView.search().getValue());
        query.setTypeIds(nullIfEmpty(toList(filterView.types().getValue(), EntityOption::getId)));
        query.setStateIds(nullIfEmpty(toList(filterView.states().getValue(), CaseState::getId)));
        query.setManagerIds(nullIfEmpty(toList(filterView.managers().getValue(), PersonShortView::getId)));
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
    AbstractCardTableView view;
    @Inject
    AbstractCardFilterView filterView;
    @Inject
    CardControllerAsync cardController;
    @Inject
    TableAnimation animation;
    @Inject
    PolicyService policyService;
    @Inject
    DefaultErrorHandler errorHandler;
    @Inject
    AbstractPagerView pagerView;

    private AppEvents.InitDetails initDetails;
    private Integer scrollTo = 0;
    private Boolean preScroll = false;

    private static String CREATE_ACTION;
}
