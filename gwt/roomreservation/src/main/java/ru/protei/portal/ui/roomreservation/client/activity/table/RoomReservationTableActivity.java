package ru.protei.portal.ui.roomreservation.client.activity.table;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.RoomReservation;
import ru.protei.portal.core.model.query.RoomReservationQuery;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.RoomReservationControllerAsync;
import ru.protei.portal.ui.common.client.util.DateUtils;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.Date;
import java.util.stream.Collectors;

import static ru.protei.portal.ui.common.client.util.PaginationUtils.PAGE_SIZE;
import static ru.protei.portal.ui.common.client.util.PaginationUtils.getTotalPages;
import static ru.protei.portal.ui.roomreservation.client.util.AccessUtil.hasAccessToRoomView;

public abstract class RoomReservationTableActivity implements AbstractRoomReservationTableActivity, Activity,
        AbstractPagerActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        pagerView.setActivity( this );
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        if (!hasAccessToRoomView(policyService)) {
            return;
        }

        view.getFilterWidget().resetFilter();
    }

    @Event
    public void onInit(AppEvents.InitDetails event) {
        this.initDetails = event;
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(RoomReservationEvents.ShowTable event) {
        if (!hasAccessToRoomView(policyService)) {
            fireEvent(new ErrorPageEvents.ShowForbidden(initDetails.parent));
            return;
        }
        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());

        view.getPagerContainer().add( pagerView.asWidget() );

        requestRoomReservation(this.page);
    }

    @Event
    public void onUpdate(RoomReservationEvents.Reload event) {
        if (view.asWidget().isAttached()) {
            reloadTable(page);
        }
    }

    @Override
    public void onItemClicked(RoomReservation value) {
        fireEvent(new RoomReservationEvents.Edit(value.getId()));
    }

    @Override
    public void onEditClicked(RoomReservation value) {
        fireEvent(new RoomReservationEvents.Edit(value.getId()));
    }

    @Override
    public void onRemoveClicked(RoomReservation value) {
        fireEvent(new ConfirmDialogEvents.Show(lang.absenceRemoveConfirmMessage(), removeAction(value)));
    }

    private void requestRoomReservation( int page ) {
        view.clearRecords();

        boolean isFirstChunk = page == 0;
        marker = new Date().getTime();

        query = getQuery();
        query.setOffset( page*PAGE_SIZE );
        query.setLimit( PAGE_SIZE );

        controller.getReservations( query, new FluentCallback< SearchResult<RoomReservation> >()
                .withMarkedSuccess( marker, ( m, r ) -> {
                    if ( marker == m ) {
                        if ( isFirstChunk ) {
                            pagerView.setTotalCount( r.getTotalCount() );
                            pagerView.setTotalPages( getTotalPages( r.getTotalCount() ) );
                        }
                        pagerView.setCurrentPage( page );
                        r.getResults().stream()
                                .collect(Collectors.groupingBy((RoomReservation roomReservation) -> DateUtils.resetTime(roomReservation.getDateFrom())))
                                .forEach((separatorDate, roomReservations) -> {
                                    view.addSeparator(DateFormatter.formatDateOnly(separatorDate));
                                    view.addRecords(roomReservations);
                                });

                        restoreScroll();
                    }
                }));
    }

    @Override
    public void onFilterChange() {
        reloadTable(0);
    }

    @Override
    public void onPageSelected(int page) {
        this.page = page;
        requestRoomReservation(this.page);
    }

    private Runnable removeAction(RoomReservation value) {
        return () -> controller.removeReservation(value.getId(), new FluentCallback<Long>()
                .withSuccess(result -> fireSuccessNotify(lang.roomReservationRemoved()))
        );
    }

    private RoomReservationQuery getQuery() {
        return view.getFilterWidget().getQuery();
    }

    private void reloadTable(int page) {
        view.clearRecords();
        requestRoomReservation(page);
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

    @Inject
    PolicyService policyService;

    private AppEvents.InitDetails initDetails;
    private RoomReservationQuery query = null;
    private long marker;
    private int page = 0;
}
