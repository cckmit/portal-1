package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseStateMatrix;
import ru.protei.winter.jdbc.JdbcDAO;

import java.util.Map;

/**
 * Created by michael on 19.05.16.
 */
public interface CaseStateMatrixDAO extends JdbcDAO<Long,CaseStateMatrix> {
    public Map<Long,Long> getOldToNewStateMap (En_CaseType caseType);
}
