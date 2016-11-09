package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.utils.TypeConverters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by michael on 19.05.16.
 */
public class CaseObjectDAO_Impl extends PortalBaseJdbcDAO<CaseObject> implements CaseObjectDAO {

    public Map<Long,Long> getNumberToIdMap (En_CaseType caseType) {

        Map<Long, Long> numberToIdMap = new HashMap<>();

        for (CaseObject o : this.getListByCondition("CASE_TYPE=?", caseType.getId())) {
            numberToIdMap.put(o.getCaseNumber(), o.getId());
        }

        return numberToIdMap;
    }

    @Override
    public List< CaseObject > getCases( CaseQuery query ) {
        return getListByCondition( "1=1", null, query.offset, query.limit, TypeConverters.createSort( query ) ).getResults();
    }
}
