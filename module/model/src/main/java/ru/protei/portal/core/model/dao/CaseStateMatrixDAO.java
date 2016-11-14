package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseStateMatrix;

import java.util.List;
import java.util.Map;

/**
 * Created by michael on 19.05.16.
 */
public interface CaseStateMatrixDAO extends PortalBaseDAO<CaseStateMatrix> {
    Map<Long,Long> getOldToNewStateMap (En_CaseType caseType);

    List<En_CaseState> getStatesByCaseType (En_CaseType caseType);
}
