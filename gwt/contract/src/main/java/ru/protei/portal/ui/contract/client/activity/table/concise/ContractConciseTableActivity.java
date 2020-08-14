package ru.protei.portal.ui.contract.client.activity.table.concise;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.query.ContractQuery;
import ru.protei.portal.ui.common.client.events.ContractEvents;
import ru.protei.portal.ui.common.client.service.ContractControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;
import java.util.function.Consumer;

import static ru.protei.portal.core.model.helper.CollectionUtils.listOf;

public abstract class ContractConciseTableActivity implements AbstractContractConciseTableActivity, Activity  {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow(ContractEvents.ShowConciseTable event) {
        show(event.parent);
        load(makeQuery(event.parentContractId), this::display);
    }

    @Override
    public void onItemClicked(Contract value) {
        fireEvent(new ContractEvents.Edit(value.getId()));
    }

    @Override
    public void onEditClicked(Contract value) {
        fireEvent(new ContractEvents.Edit(value.getId()));
    }

    private void show(HasWidgets parent) {
        parent.clear();
        view.clearRecords();
        parent.add(view.asWidget());
    }

    private void load(ContractQuery query, Consumer<List<Contract>> onLoaded) {
        view.clearRecords();
        contractController.getContracts(query, new FluentCallback<SearchResult<Contract>>()
                .withSuccess(sr -> onLoaded.accept(sr.getResults())));
    }

    private void display(List<Contract> contracts) {
        view.setData(contracts);
    }

    private ContractQuery makeQuery(Long parentContractId) {
        ContractQuery query = new ContractQuery();
        query.setParentContractIds(listOf(parentContractId));
        return query;
    }

    @Inject
    AbstractContractConciseTableView view;
    @Inject
    ContractControllerAsync contractController;
}
