package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.query.ContractQuery;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.Date;

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

    Result<Date> getContractValidDateByProjectId(AuthToken token, Long id);
}
