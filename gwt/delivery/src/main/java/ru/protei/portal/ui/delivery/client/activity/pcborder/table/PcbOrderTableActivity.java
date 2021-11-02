package ru.protei.portal.ui.delivery.client.activity.pcborder.table;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_PcbOrderState;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.PcbOrder;
import ru.protei.portal.core.model.query.PcbOrderQuery;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.PcbOrderControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.delivery.client.activity.pcborder.filter.AbstractPcbOrderFilterActivity;
import ru.protei.portal.ui.delivery.client.activity.pcborder.filter.AbstractPcbOrderFilterView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.Date;

import static ru.protei.portal.ui.common.client.util.PaginationUtils.PAGE_SIZE;
import static ru.protei.portal.ui.common.client.util.PaginationUtils.getTotalPages;

public abstract class PcbOrderTableActivity implements AbstractPcbOrderTableActivity, Activity,
        AbstractPagerActivity, AbstractPcbOrderFilterActivity {

    @PostConstruct
    public void onInit() {
        CREATE_ACTION = lang.buttonCreate();

        view.setActivity(this);
        pagerView.setActivity( this );

        view.setAnimation( animation );
        filterView.setActivity( this );
        view.getFilterContainer().add( filterView.asWidget() );
    }

    @Override
    public void onFilterChanged() {
        loadTable();
    }

    private void loadTable() {
//        animation.closeDetails();
//        view.clearRecords();
//        view.triggerTableLoad();
//        fireEvent(new ActionBarEvents.SetButtonEnabled( UiConstants.ActionBarIdentity.CARD_GROUP_MODIFY, false));
    }

    @Event
    public void onAuthSuccess (AuthEvents.Success event) {
        filterView.resetFilter();
    }

    @Event
    public void onInit(AppEvents.InitDetails event) {
        this.initDetails = event;
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(PcbOrderEvents.Show event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.PCB_ORDER_VIEW)) {
            fireEvent(new ErrorPageEvents.ShowForbidden(initDetails.parent));
            return;
        }

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
        view.getPagerContainer().add( pagerView.asWidget() );
        view.getFilterContainer().add( filterView.asWidget() );

        fireEvent(new ActionBarEvents.Clear());
        if (policyService.hasPrivilegeFor(En_Privilege.CARD_CREATE)) {
            fireEvent(new ActionBarEvents.Add(CREATE_ACTION , null, UiConstants.ActionBarIdentity.CARD_CREATE));
        }

        requestPcbOrder(this.page);
    }

    @Override
    public void onItemClicked(PcbOrder value) {
        showPreview(value);
    }

    @Override
    public void onEditClicked(PcbOrder value) {
        fireEvent(new PcbOrderEvents.Edit(value.getId()));
    }

    @Override
    public void onRemoveClicked(PcbOrder value) {
        fireEvent(new ConfirmDialogEvents.Show(lang.pcbOrderRemoveConfirmMessage(), removeAction(value)));
    }

    @Override
    public En_PcbOrderState makeGroup(PcbOrder pcbOrder) {
        return pcbOrder.getState();
    }

    @Override
    public String makeGroupName(En_PcbOrderState state) {

        if (state == null){
            return "";
        }

        switch (state) {
            case SENT:
                return "завершенные";
            case ACCEPTED:
            case RECEIVED:
                return "активные";
            default:
                return "";
        }
    }

    private void requestPcbOrder(int page ) {
        view.clearRecords();

        boolean isFirstChunk = page == 0;
        marker = new Date().getTime();

        PcbOrderQuery query = getQuery();
        query.setOffset( page*PAGE_SIZE );
        query.setLimit( PAGE_SIZE );

        service.getPcbOrderList( query, new FluentCallback< SearchResult<PcbOrder> >()
                .withMarkedSuccess( marker, ( m, r ) -> {
                    if ( marker == m ) {
                        if ( isFirstChunk ) {
                            pagerView.setTotalCount( r.getTotalCount() );
                            pagerView.setTotalPages( getTotalPages( r.getTotalCount() ) );
                        }
                        pagerView.setCurrentPage( page );
                        view.addRecords(r.getResults());

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
        requestPcbOrder(this.page);
    }

    private void showPreview(PcbOrder value) {
        if (value == null || value.getId() == null) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent(new PcbOrderEvents.ShowPreview(view.getPreviewContainer(), value.getId()));
        }
    }

    private Runnable removeAction(PcbOrder value) {
        return () -> service.removePcbOrder(value, new FluentCallback<PcbOrder>()
                .withSuccess(result -> fireSuccessNotify(lang.pcbOrderRemoved()))
        );
    }

    private PcbOrderQuery getQuery() {
        //TODO fill query
        PcbOrderQuery query = new PcbOrderQuery();
        query.setSearchString(filterView.search().getValue());
        return query;
    }

    private void reloadTable(int page) {
        view.clearRecords();
        requestPcbOrder(page);
    }

    private void restoreScroll() {
        Window.scrollTo(0, 0);
    }

    private void fireSuccessNotify(String message) {
        fireEvent(new NotifyEvents.Show(message, NotifyEvents.NotifyType.SUCCESS));
    }

    @Inject
    AbstractPcbOrderTableView view;

    @Inject
    AbstractPagerView pagerView;

    @Inject
    AbstractPcbOrderFilterView filterView;

    @Inject
    PcbOrderControllerAsync service;

    @Inject
    TableAnimation animation;

    @Inject
    Lang lang;

    @Inject
    PolicyService policyService;

    private AppEvents.InitDetails initDetails;
    private long marker;
    private int page = 0;

    private static String CREATE_ACTION;
}
