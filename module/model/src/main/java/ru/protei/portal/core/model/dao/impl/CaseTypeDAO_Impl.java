package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CaseTypeDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseType;

/**
 * DAO для типов case
 */
public class CaseTypeDAO_Impl extends PortalBaseJdbcDAO<CaseType > implements CaseTypeDAO {

    @Override
    public Long generateNextId(En_CaseType type) {

        CaseType record = partialGetWithLock((long)type.getId(), "NEXT_ID");

        Long rez = record.getNextId();

        record.setNextId(rez + 1L);

        partialMerge(record, "NEXT_ID");

        return rez;
    }
}
