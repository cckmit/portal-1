package ru.protei.portal.ui.contract.client.activity.table;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_ContractState;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dto.ProductDirectionInfo;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.query.ContractQuery;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.ui.common.client.activity.contractfilter.AbstractContractFilterActivity;
import ru.protei.portal.ui.common.client.activity.contractfilter.AbstractContractFilterView;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerActivity;
import ru.protei.portal.ui.common.client.activity.pager.AbstractPagerView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.ConfigStorage;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ContractControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.HashSet;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType.toDateRange;

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
        filterView.initCuratorsSelector(configStorage.getConfigData().contractCuratorsDepartmentsIds);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails event) {
        this.init = event;
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(ContractEvents.Show event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.CONTRACT_VIEW)) {
            fireEvent(new ErrorPageEvents.ShowForbidden(init.parent));
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
        persistScroll();
        showPreview(value);
    }

    @Override
    public void onEditClicked(Contract value) {
        persistScroll();
        fireEvent(new ContractEvents.Edit(value.getId()));
    }

    @Override
    public void onFilterChanged() {
        loadTable();
    }

    private List<En_ContractState> getStates(List<En_ContractState> states) {
        if (isEmpty(states)){
            states = En_ContractState.contractStatesByDefault();
            filterView.states().setValue(new HashSet<>(states));
        }

        return states;
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
    public void onPageChanged(int page) {
        pagerView.setCurrentPage(page);
    }

    @Override
    public void onPageSelected(int page) {
        view.scrollTo(page);
    }

    @Override
    public void onCopyClicked(Contract value) {
        fireEvent(new ContractEvents.Edit(value.getId(), true));
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
        query.setContractorIds(collectIds(filterView.contractors().getValue()));
        query.setCuratorIds(collectIds(filterView.curators().getValue()));
        query.setOrganizationIds(collectIds(filterView.organizations().getValue()));
        query.setManagerIds(collectIds(filterView.managers().getValue()));
        query.setTypes(nullIfEmpty(listOfOrNull(filterView.types().getValue())));
        query.setCaseTagsIds(nullIfEmpty(toList(filterView.tags().getValue(), caseTag -> caseTag == null ? CrmConstants.CaseTag.NOT_SPECIFIED : caseTag.getId())));
        query.setStates(getStates(listOfOrNull(filterView.states().getValue())));
        ProductDirectionInfo value = filterView.direction().getValue();
        query.setDirectionId(value == null ? null : value.id);
        query.setKind(filterView.kind().getValue());
        query.setDateSigningRange(toDateRange(filterView.dateSigningRange().getValue()));
        query.setDateValidRange(toDateRange(filterView.dateValidRange().getValue()));
        query.setDeliveryNumber(filterView.deliveryNumber().getValue());
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
    private AbstractPagerView pagerView;
    @Inject
    ConfigStorage configStorage;

    private Integer scrollTo = 0;
    private Boolean preScroll = false;
    private AppEvents.InitDetails init;
}
