package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.Contractor;

public interface ContractorDAO extends PortalBaseDAO<Contractor> {
    Contractor getContractorByRefKey(String refKey);
}