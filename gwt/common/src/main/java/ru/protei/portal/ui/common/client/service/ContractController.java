package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.query.ContractQuery;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

@RemoteServiceRelativePath("springGwtServices/ContractController")
public interface ContractController extends RemoteService {
    List<Contract> getContracts(ContractQuery query) throws RequestFailedException;

    Integer getContractCount(ContractQuery query) throws RequestFailedException;

    Contract getContract(Long id) throws RequestFailedException;

    Long saveContract(Contract Contract) throws RequestFailedException;
}