package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.ent.Contractor;
import ru.protei.portal.core.model.ent.ContractorCountry;
import ru.protei.portal.core.model.query.ContractQuery;
import ru.protei.portal.core.model.struct.ContractorQuery;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

public interface ContractControllerAsync {

    void getContracts(ContractQuery query, AsyncCallback<SearchResult<Contract>> callback);

    void getContract(Long id, AsyncCallback<Contract> callback);

    void saveContract(Contract Contract, AsyncCallback<Long> callback);

    void getContractorCountryList(String organization, AsyncCallback<List<ContractorCountry>> callback);

    void getContractorList(AsyncCallback<List<Contractor>> callback);

    void findContractors(String organization, ContractorQuery query, AsyncCallback< List<Contractor>> callback);

    void createContractor(Contractor contractor, AsyncCallback<Contractor> callback);

    void removeContractor(Long contractorId, AsyncCallback<Contractor> async);
}
