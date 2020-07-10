package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.ent.Contractor;
import ru.protei.portal.core.model.ent.ContractorAPI;
import ru.protei.portal.core.model.ent.ContractorCountryAPI;
import ru.protei.portal.core.model.query.ContractQuery;
import ru.protei.portal.core.model.struct.ContractorPair;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

public interface ContractControllerAsync {

    void getContracts(ContractQuery query, AsyncCallback<SearchResult<Contract>> callback);

    void getContract(Long id, AsyncCallback<Contract> callback);

    void saveContract(Contract Contract, AsyncCallback<Long> callback);

    void getContractorCountryList(String organization, AsyncCallback<List<ContractorCountryAPI>> callback);

    void getContractorList(AsyncCallback<List<Contractor>> callback);

    void findContractors(String organization, String contractorINN, String contractorKPP, AsyncCallback< List<ContractorPair>> callback);

    void createContractor(ContractorAPI contractor, AsyncCallback<Contractor> callback);
}
