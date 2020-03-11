package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.ContractSla;

import java.util.List;

public interface ContractSlaDAO extends PortalBaseDAO<ContractSla> {
    List<ContractSla> getSlaByContractId(Long contractId);
}
