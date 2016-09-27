package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseStateMatrix;

import java.util.Map;

/**
 * Created by michael on 19.05.16.
 */
public interface CaseStateMatrixDAO extends PortalBaseDAO<CaseStateMatrix> {
    public Map<Long,Long> getOldToNewStateMap (En_CaseType caseType);
}
