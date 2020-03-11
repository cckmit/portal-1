package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.ContractSlaDAO;
import ru.protei.portal.core.model.ent.ContractSla;

import java.util.Collections;
import java.util.List;

public class ContractSlaDAO_Impl extends PortalBaseJdbcDAO<ContractSla> implements ContractSlaDAO {
    @Override
    public List<ContractSla> getSlaByContractId(Long contractId) {
        return getListByCondition("contract_id = ?", Collections.singletonList(contractId));
    }
}
