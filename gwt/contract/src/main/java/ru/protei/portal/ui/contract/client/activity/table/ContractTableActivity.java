package ru.protei.portal.ui.contract.client.activity.table;

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
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.ActionBarEvents;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.ContractEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ContractControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.contract.client.activity.filter.AbstractContractFilterActivity;
import ru.protei.portal.ui.contract.client.activity.filter.AbstractContractFilterView;

import java.util.List;

public abstract class ContractTableActivity implements AbstractContractTableActivity,
        AbstractContractFilterActivity, Activity {

    @PostConstruct
    public void init() {
        view.setActivity(this);
        view.setAnimation(animation);
        filterView.setActivity(this);
        view.getFilterContainer().add(filterView.asWidget());
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails event) {
        this.init = event;
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(ContractEvents.Show event) {
        init.parent.clear();
        init.parent.add(view.asWidget());

        fireEvent(policyService.hasPrivilegeFor(En_Privilege.CONTACT_CREATE) ?
                new ActionBarEvents.Add(lang.buttonCreate(), UiConstants.ActionBarIcons.CREATE, UiConstants.ActionBarIdentity.CONTRACT) :
                new ActionBarEvents.Clear()
        );

        filterView.resetFilter();
        requestContractsCount(makeQuery());
    }

    @Event
    public void onCreateClicked(ActionBarEvents.Clicked event) {
        if (!UiConstants.ActionBarIdentity.EMPLOYEE_REGISTRATION.equals(event.identity)) {
            return;
        }
        fireEvent(new ContractEvents.Edit());
    }

    @Override
    public void onItemClicked(Contract value) {
        showPreview(value);
    }

    @Override
    public void onFilterChanged() {
        requestContractsCount(makeQuery());
    }

    @Override
    public void loadData(int offset, int limit, AsyncCallback<List<Contract>> asyncCallback) {
        ContractQuery query = makeQuery();
        query.setOffset(offset);
        query.setLimit(limit);

        contractService.getContracts(query, new RequestCallback<List<Contract>>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(List<Contract> contracts) {
                asyncCallback.onSuccess(contracts);
            }
        });

    }

    private ContractQuery makeQuery() {
        ContractQuery query = new ContractQuery();
        query.searchString = filterView.searchString().getValue();
        query.setSortDir(filterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);
        query.setSortField(filterView.sortField().getValue());
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

    private void requestContractsCount(ContractQuery query) {
        view.clearRecords();
        animation.closeDetails();

        contractService.getContractCount(query, new RequestCallback<Integer>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(Integer count) {
                view.setRecordCount(count);
            }
        });
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

    private AppEvents.InitDetails init;
}
