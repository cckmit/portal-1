package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.query.ContractQuery;

import java.util.List;

public interface ContractControllerAsync {
    void getContracts(ContractQuery query, AsyncCallback<List<Contract>> callback);

    void getContractCount(ContractQuery query, AsyncCallback<Integer> callback);

    void getContract(Long id, AsyncCallback<Contract> callback);

    void saveContract(Contract Contract, AsyncCallback<Long> callback);
}
