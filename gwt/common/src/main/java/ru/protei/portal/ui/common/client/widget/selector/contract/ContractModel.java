package ru.protei.portal.ui.common.client.widget.selector.contract;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.query.ContractQuery;
import ru.protei.portal.core.model.struct.ContractInfo;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.ContractEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.model.BaseSelectorModel;
import ru.protei.portal.ui.common.client.service.CaseStateControllerAsync;
import ru.protei.portal.ui.common.client.service.ContractControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.person.Refreshable;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;
import java.util.stream.Collectors;

public abstract class ContractModel extends BaseSelectorModel<ContractInfo> implements Activity {

    @Event
    public void onInit(AuthEvents.Success event) {
        clean();
    }

    @Event
    public void onContractListChanged(ContractEvents.ChangeModel event) {
        clean();
    }

    public void updateProject(Refreshable selector, Long projectId) {
        this.refreshable = selector;
        query.setProjectId( projectId );
        clean();
    }

    @Override
    protected void requestData(LoadingHandler selector, String searchText ) {
        contractController.getContracts(query, new FluentCallback<SearchResult<Contract>>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess(contracts -> {
                    List<ContractInfo> options = contracts.getResults().stream()
                            .map(Contract::toContactInfo)
                            .collect(Collectors.toList());
                    updateElements(options, selector);
                    if(refreshable!=null){
                        refreshable.refresh();
                    }
                })
        );

//        caseStateController.getCaseStates(En_CaseType.CONTRACT, new FluentCallback<List<CaseState>>()
//                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR)))
//                .withSuccess(caseStates -> updateElements(caseStates, selector)));
    }

    private ContractQuery query = new ContractQuery();
    private Refreshable refreshable;

    @Inject
    Lang lang;
    @Inject
    ContractControllerAsync contractController;
    @Inject
    CaseStateControllerAsync caseStateController;

}
