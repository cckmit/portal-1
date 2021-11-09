package ru.protei.portal.ui.delivery.client.activity.pcborder.table;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.PcbOrder;
import ru.protei.portal.core.model.query.PcbOrderQuery;
import ru.protei.portal.core.model.view.EntityOption;
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

import java.util.*;

import static ru.protei.portal.core.model.helper.CollectionUtils.nullIfEmpty;
import static ru.protei.portal.core.model.helper.CollectionUtils.toList;
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
        animation.closeDetails();
        view.clearRecords();
        requestPcbOrder(this.page);
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
        if (policyService.hasPrivilegeFor(En_Privilege.PCB_ORDER_CREATE)) {
            fireEvent(new ActionBarEvents.Add(CREATE_ACTION , null, UiConstants.ActionBarIdentity.PCB_ORDER_CREATE));
        }

        requestPcbOrder(this.page);
    }

    @Event
    public void onCreateClicked(ActionBarEvents.Clicked event) {
        if (!(UiConstants.ActionBarIdentity.PCB_ORDER_CREATE.equals(event.identity)) ) {
            return;
        }

        view.clearSelection();

        fireEvent(new PcbOrderEvents.Create());
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
    public PcbOrderGroupType makeGroup(PcbOrder pcbOrder) {

        if (pcbOrder == null){
            return null;
        }

        En_PcbOrderState state = pcbOrder.getState();

        if (state == null){
            return null;
        }

        switch (state) {
            case RECEIVED:
                return PcbOrderGroupType.COMPLETED;
            case ACCEPTED:
            case SENT:
                return PcbOrderGroupType.ACTIVE;
            default:
                return null;
        }
    }

    @Override
    public String makeGroupName(PcbOrderGroupType type) {

        if (type == null){
            return lang.pcbOrderGroupNotDefined();
        }

        switch (type) {
            case COMPLETED:
                return lang.pcbOrderGroupCompleted();
            case ACTIVE:
                return lang.pcbOrderGroupActive();
            default:
                return type.name();
        }
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
                        view.addRecords(r.getResults(), groupComparator);

                        restoreScroll();
                    }
                }));
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
                .withSuccess(result -> {
                    fireSuccessNotify(lang.pcbOrderRemoved());
                    fireEvent(new PcbOrderEvents.Show(false));
                })
        );
    }

    private PcbOrderQuery getQuery() {
        PcbOrderQuery query = new PcbOrderQuery();
        query.setCardTypeIds(nullIfEmpty(toList(filterView.types().getValue(), EntityOption::getId)));
        query.setTypeIds(nullIfEmpty(toList(filterView.orderType().getValue(), En_PcbOrderType::getId)));
        query.setStateIds(nullIfEmpty(toList(filterView.states().getValue(), En_PcbOrderState::getId)));
        query.setPromptnessIds(nullIfEmpty(toList(filterView.promptness().getValue(), En_PcbOrderPromptness::getId)));
        query.setSortField(filterView.sortField().getValue());
        query.setSortDir(filterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);
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

    Comparator<Map.Entry<PcbOrderGroupType, List<PcbOrder>>> groupComparator = new Comparator<Map.Entry<PcbOrderGroupType, List<PcbOrder>>>() {
        @Override
        public int compare(Map.Entry<PcbOrderGroupType, List<PcbOrder>> o1, Map.Entry<PcbOrderGroupType, List<PcbOrder>> o2) {
            if (Objects.equals(o1.getKey(), o2.getKey())){
                return 0;
            }
            return o1.getKey().isBefore(o2.getKey()) ? 1 : -1;
        }
    };

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
