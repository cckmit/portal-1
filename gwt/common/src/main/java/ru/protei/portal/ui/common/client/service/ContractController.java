package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.ContractQuery;
import ru.protei.portal.core.model.struct.ContractorQuery;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

@RemoteServiceRelativePath("springGwtServices/ContractController")
public interface ContractController extends RemoteService {

    SearchResult<Contract> getContracts(ContractQuery query) throws RequestFailedException;

    Contract getContract(Long id) throws RequestFailedException;

    Long saveContract(Contract Contract) throws RequestFailedException;

    List<ContractorCountry> getContractorCountryList(String organization) throws RequestFailedException;

    List<Contractor> getContractorList() throws RequestFailedException;

    List<Contractor> findContractors(String organization, ContractorQuery query) throws RequestFailedException;

    Contractor createContractor(Contractor contractor) throws RequestFailedException;

    Long removeContractor(String organization, String refKey) throws RequestFailedException;

    SelectorsParams getSelectorsParams(ContractQuery query) throws RequestFailedException;

    List<CalculationType> getCalculationTypeList(String organization) throws RequestFailedException;
}
