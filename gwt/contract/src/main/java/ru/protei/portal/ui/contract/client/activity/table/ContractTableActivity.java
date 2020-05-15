package ru.protei.portal.ui.contract.client.activity.table;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.query.ContractQuery;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ContractControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.contract.client.activity.filter.AbstractContractFilterActivity;
import ru.protei.portal.ui.contract.client.activity.filter.AbstractContractFilterView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public abstract class ContractTableActivity implements AbstractContractTableActivity,
        AbstractContractFilterActivity, AbstractPagerActivity, Activity {

    @PostConstruct
    public void init() {
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
    public void onInitDetails(AppEvents.InitDetails event) {
        this.init = event;
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(ContractEvents.Show event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.CONTRACT_VIEW)) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }

        init.parent.clear();
        init.parent.add(view.asWidget());
        view.getPagerContainer().add(pagerView.asWidget());

        fireEvent(policyService.hasPrivilegeFor(En_Privilege.CONTRACT_CREATE) ?
                new ActionBarEvents.Add(lang.buttonCreate(), null, UiConstants.ActionBarIdentity.CONTRACT) :
                new ActionBarEvents.Clear()
        );

        this.preScroll = event.preScroll;

        loadTable();
    }

    @Event
    public void onCreateClicked(ActionBarEvents.Clicked event) {
        if (!UiConstants.ActionBarIdentity.CONTRACT.equals(event.identity)) {
            return;
        }

        view.clearSelection();

        fireEvent(new ContractEvents.Edit());
    }

    @Override
    public void onItemClicked(Contract value) {
        persistScrollPosition();
        showPreview(value);
    }

    @Override
    public void onEditClicked(Contract value) {
        persistScrollPosition();
        fireEvent(new ContractEvents.Edit(value.getId()));
    }

    @Override
    public void onFilterChanged() {
        loadTable();
    }

    @Override
    public void loadData(int offset, int limit, AsyncCallback<List<Contract>> asyncCallback) {
        boolean isFirstChunk = offset == 0;
        ContractQuery query = makeQuery();
        query.setOffset(offset);
        query.setLimit(limit);
        contractService.getContracts(query, new FluentCallback<SearchResult<Contract>>()
                .withError(throwable -> {
                    errorHandler.accept(throwable);
                    asyncCallback.onFailure(throwable);
                })
                .withSuccess(sr -> {
                    asyncCallback.onSuccess(sr.getResults());
                    if (isFirstChunk) {
                        view.setTotalRecords(sr.getTotalCount());
                        pagerView.setTotalPages(view.getPageCount());
                        pagerView.setTotalCount(sr.getTotalCount());
                        restoreScroll();
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

    private void loadTable() {
        animation.closeDetails();
        view.clearRecords();
        view.triggerTableLoad();
    }

    private ContractQuery makeQuery() {
        ContractQuery query = new ContractQuery();
        query.searchString = filterView.searchString().getValue();
        query.setSortDir(filterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);
        query.setSortField(filterView.sortField().getValue());
        query.setContragentIds(getCompaniesIdList(filterView.contragents().getValue()));
        query.setOrganizationIds(getCompaniesIdList(filterView.organizations().getValue()));
        query.setManagerIds(getManagersIdList(filterView.managers().getValue()));
        query.setType(filterView.type().getValue());
        query.setState(filterView.state().getValue());
        ProductDirectionInfo value = filterView.direction().getValue();
        query.setDirectionId(value == null ? null : value.id);
        return query;
    }

    private void showPreview(Contract value) {
        if (value == null || value.getId() == null) {
            animation.closeDetails();
        } else {
            animation.showDetails();
            fireEvent(new ContractEvents.ShowPreview(view.getPreviewContainer(), value.getId()));
        }
    }

    private void persistScrollPosition() {
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

    public static List< Long > getCompaniesIdList( Set<EntityOption> companySet ) {

        if ( companySet == null || companySet.isEmpty() ) {
            return null;
        }
        return companySet
                .stream()
                .map( EntityOption::getId )
                .collect( Collectors.toList() );
    }

    public static List< Long > getManagersIdList( Set<PersonShortView> personSet ) {

        if ( personSet == null || personSet.isEmpty() ) {
            return null;
        }

        return personSet
                .stream()
                .map( PersonShortView::getId )
                .collect( Collectors.toList() );
    }

    @Inject
    private Lang lang;
    @Inject
    private TableAnimation animation;
    @Inject
    private PolicyService policyService;
    @Inject
    private AbstractContractTableView view;
    @Inject
    private AbstractContractFilterView filterView;
    @Inject
    private ContractControllerAsync contractService;
    @Inject
    private DefaultErrorHandler errorHandler;
    @Inject
    AbstractPagerView pagerView;

    private Integer scrollTo = 0;
    private Boolean preScroll = false;
    private AppEvents.InitDetails init;
}
