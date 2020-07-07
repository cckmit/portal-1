package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.Contractor;

import java.util.List;

public interface ContractorDAO extends PortalBaseDAO<Contractor> {
    List<Contractor> getContractorByRefKey(String refKey);
}