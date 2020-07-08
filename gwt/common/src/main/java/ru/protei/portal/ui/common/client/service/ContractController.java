package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.dict.En_Organization;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.ent.Contractor;
import ru.protei.portal.core.model.ent.ContractorAPI;
import ru.protei.portal.core.model.query.ContractQuery;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

@RemoteServiceRelativePath("springGwtServices/ContractController")
public interface ContractController extends RemoteService {

    SearchResult<Contract> getContracts(ContractQuery query) throws RequestFailedException;

    Contract getContract(Long id) throws RequestFailedException;

    Long saveContract(Contract Contract) throws RequestFailedException;

    List<String> getContractorCountryList() throws RequestFailedException;

    List<Contractor> getContractorList() throws RequestFailedException;

    List<Contractor> findContractors(En_Organization organization, String contractorINN, String contractorKPP) throws RequestFailedException;

    Contractor createContractor(ContractorAPI contractorApi) throws RequestFailedException;
}
