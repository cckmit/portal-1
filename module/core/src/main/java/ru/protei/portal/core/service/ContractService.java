package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Organization;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.ent.Contractor;
import ru.protei.portal.core.model.ent.ContractorAPI;
import ru.protei.portal.core.model.query.ContractQuery;
import ru.protei.portal.core.model.struct.ContractorPair;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

public interface ContractService {

    @Privileged({ En_Privilege.CONTRACT_VIEW })
    Result<SearchResult<Contract>> getContracts( AuthToken token, ContractQuery query);

    @Privileged(En_Privilege.CONTRACT_VIEW)
    Result<Contract> getContract( AuthToken token, Long id);

    @Privileged(requireAny = En_Privilege.CONTRACT_CREATE)
    @Auditable(En_AuditType.CONTRACT_CREATE)
    Result<Long> createContract( AuthToken token, Contract contract);

    @Privileged(requireAny = En_Privilege.CONTRACT_EDIT)
    @Auditable(En_AuditType.CONTRACT_MODIFY)
    Result<Long> updateContract( AuthToken token, Contract contract);

    @Privileged(requireAny = {En_Privilege.CONTRACT_CREATE, En_Privilege.CONTRACT_EDIT})
    Result<List<String>> getContractorCountryList(AuthToken token);

    @Privileged(En_Privilege.CONTRACT_VIEW)
    Result<List<Contractor>> getContractorList(AuthToken token);

    @Privileged(requireAny = {En_Privilege.CONTRACT_CREATE, En_Privilege.CONTRACT_EDIT})
    Result<List<ContractorPair>> findContractors(AuthToken token, En_Organization organization, String contractorINN, String contractorKPP);

    @Privileged(requireAny = {En_Privilege.CONTRACT_CREATE, En_Privilege.CONTRACT_EDIT})
    Result<Contractor> createContractor(AuthToken token, ContractorAPI contractor);
}
