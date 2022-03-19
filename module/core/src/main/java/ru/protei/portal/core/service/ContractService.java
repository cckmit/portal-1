package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.ContractApiQuery;
import ru.protei.portal.core.model.query.ContractQuery;
import ru.protei.portal.core.model.struct.ContractorQuery;
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
    Result<List<ContractorCountry>> getContractorCountryList(AuthToken token, String organization);

    @Privileged(En_Privilege.CONTRACT_VIEW)
    Result<List<Contractor>> getContractorList(AuthToken token);

    @Privileged(requireAny = {En_Privilege.CONTRACT_CREATE, En_Privilege.CONTRACT_EDIT})
    Result<List<Contractor>> findContractors(AuthToken token, String organization, ContractorQuery query);

    @Privileged(requireAny = {En_Privilege.CONTRACT_CREATE, En_Privilege.CONTRACT_EDIT})
    Result<Contractor> createContractor(AuthToken token, Contractor contractor);

    @Privileged(En_Privilege.CONTRACT_EDIT)
    Result<Long> removeContractor(AuthToken token, String organization, String refKey);

    @Privileged(En_Privilege.CONTRACT_VIEW)
    Result<List<Contract>> getContractsByApiQuery(AuthToken token, ContractApiQuery apiQuery);

    @Privileged(requireAny = {En_Privilege.CONTRACT_CREATE, En_Privilege.CONTRACT_EDIT})
    Result<List<ContractCalculationType>> getCalculationTypeList(AuthToken token, String organization);

    Result<SelectorsParams> getSelectorsParams(AuthToken token, ContractQuery query);
}
