package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.query.ContractQuery;

import java.util.List;

public interface ContractService {

    @Privileged(En_Privilege.CONTRACT_VIEW)
    CoreResponse<Integer> count(AuthToken token, ContractQuery query);

    @Privileged(En_Privilege.CONTRACT_VIEW)
    CoreResponse<List<Contract>> contractList(AuthToken token, ContractQuery query);

    @Privileged(En_Privilege.CONTRACT_VIEW)
    CoreResponse<Contract> getContract(AuthToken token, Long id);

    @Privileged(requireAny = En_Privilege.CONTRACT_CREATE)
    CoreResponse<Long> createContract(AuthToken token, Contract contract);

    @Privileged(requireAny = En_Privilege.CONTRACT_EDIT)
    CoreResponse<Long> updateContract(AuthToken token, Contract contract);
}
