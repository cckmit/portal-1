package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CaseStateMatrixDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseStateMatrix;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by michael on 19.05.16.
 */
public class CaseStateMatrixDAO_Impl extends PortalBaseJdbcDAO<CaseStateMatrix> implements CaseStateMatrixDAO{

    public Map<Long,Long> getOldToNewStateMap (En_CaseType caseType) {

        Map<Long,Long> oldToNewStateMap = new HashMap<>();

        for (CaseStateMatrix me : getListByCondition("CASE_TYPE=?", caseType.getId())) {
            oldToNewStateMap.put(me.getOldId(), me.getCaseStateId());
        }

        return oldToNewStateMap;
    }
}
