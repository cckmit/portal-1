package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CaseStateMatrixDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.CaseStateMatrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by michael on 19.05.16.
 */
public class CaseStateMatrixDAO_Impl extends PortalBaseJdbcDAO<CaseStateMatrix> implements CaseStateMatrixDAO{

    public Map<Long,Long> getOldToNewStateMap (En_CaseType caseType) {

        Map<Long,Long> oldToNewStateMap = new HashMap<>();

        for (CaseStateMatrix me : getListByCondition("CASE_TYPE=? and OLD_ID is not null", caseType.getId())) {
            oldToNewStateMap.put(me.getOldId(), me.getCaseStateId());
        }

        return oldToNewStateMap;
    }

    @Override
    public List<CaseState> getStatesByCaseType(En_CaseType caseType) {
        List<CaseStateMatrix> caseStateMatrix = getListByCondition("CASE_TYPE=? ORDER BY VIEW_ORDER", caseType.getId());

        List<CaseState> caseStateList = new ArrayList<>(caseStateMatrix.size());
        caseStateMatrix.forEach(csm ->
            caseStateList.add(csm.getCaseState().withViewOrder(csm.getViewOrder()))
        );

        return caseStateList;
    }

}
