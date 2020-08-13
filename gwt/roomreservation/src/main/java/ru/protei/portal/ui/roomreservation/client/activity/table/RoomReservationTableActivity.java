package ru.protei.portal.ui.roomreservation.client.activity.table;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.RoomReservation;
import ru.protei.portal.core.model.query.RoomReservationQuery;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.ConfirmDialogEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.RoomReservationEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.RoomReservationControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

public abstract class RoomReservationTableActivity implements AbstractRoomReservationTableActivity, Activity,
        AbstractPagerActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        pagerView.setActivity( this );
    }

    @Event
    public void onInit(AppEvents.InitDetails event) {
        this.initDetails = event;
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(RoomReservationEvents.ShowTable event) {
        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());

        view.getPagerContainer().add( pagerView.asWidget() );

        loadTable();
    }

    @Event
    public void onUpdate(RoomReservationEvents.Reload event) {
        if (view.asWidget().isAttached()) {
            loadTable();
        }
    }

    @Override
    public void onItemClicked(RoomReservation value) {
        // do nothing
    }

    @Override
    public void onEditClicked(RoomReservation value) {
        fireEvent(new RoomReservationEvents.Edit(value.getId()));
    }

    @Override
    public void onRemoveClicked(RoomReservation value) {
        fireEvent(new ConfirmDialogEvents.Show(lang.absenceRemoveConfirmMessage(), removeAction(value)));
    }

    @Override
    public void loadData(int offset, int limit, AsyncCallback<List<RoomReservation>> asyncCallback) {
        boolean isFirstChunk = offset == 0;
        query = getQuery();
        query.setOffset(offset);
        query.setLimit(limit);
        controller.getReservations(query, new FluentCallback<SearchResult<RoomReservation>>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                    asyncCallback.onFailure(throwable);
                })
                .withSuccess(sr -> {
                    if (!query.equals(getQuery())) {
                        loadData(offset, limit, asyncCallback);
                    }
                    else {
                        if (isFirstChunk) {
                            view.setTotalRecords(sr.getTotalCount());
                            pagerView.setTotalPages(view.getPageCount());
                            pagerView.setTotalCount(sr.getTotalCount());
                            restoreScroll();
                        }

                        asyncCallback.onSuccess(sr.getResults());
                    }
                }));
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
    public void onFilterChange() {
        loadTable();
    }

    private Runnable removeAction(RoomReservation value) {
        return () -> controller.removeReservation(value.getId(), new FluentCallback<RoomReservation>()
                .withSuccess(result -> fireSuccessNotify(lang.roomReservationRemoved())));
    }

    private RoomReservationQuery getQuery() {
        return view.getFilterParam().getQuery();
    }

    private void loadTable() {
        view.clearRecords();
        view.triggerTableLoad();
    }

    private void restoreScroll() {
        Window.scrollTo(0, 0);
    }

    private void fireSuccessNotify(String message) {
        fireEvent(new NotifyEvents.Show(message, NotifyEvents.NotifyType.SUCCESS));
        fireEvent(new RoomReservationEvents.Reload());
    }

    @Inject
    AbstractRoomReservationTableView view;

    @Inject
    AbstractPagerView pagerView;

    @Inject
    RoomReservationControllerAsync controller;

    @Inject
    Lang lang;

    private AppEvents.InitDetails initDetails;
    private RoomReservationQuery query = null;
}