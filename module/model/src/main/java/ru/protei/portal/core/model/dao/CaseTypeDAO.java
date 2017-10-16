package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseType;

/**
 * DAO для типов case
 */
public interface CaseTypeDAO extends PortalBaseDAO<CaseType> {

    Long generateNextId (En_CaseType type);
}
