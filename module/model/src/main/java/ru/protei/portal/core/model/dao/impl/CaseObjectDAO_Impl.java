package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.utils.TypeConverters;

import java.util.ArrayList;
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
    public Long insertCase(CaseObject object) {

        En_CaseType type = object.getCaseType();

        Long caseNumber = getMaxValue("CASENO", Long.class, "case_type=?", type.getId()) + 1;

        object.setCaseNumber(caseNumber);
        object.setExtId(type.makeGUID(caseNumber));

        return persist(object);
    }

    @Override
    public List< CaseObject > getCases( CaseQuery query ) {
        return listByQuery(query);
    }


    @SqlConditionBuilder
    public SqlCondition caseQueryCondition (CaseQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("1=1");

            if ( query.getType() != null ) {
                condition.append( " and case_type=?" );
                args.add( query.getType().getId() );
            }

            if ( query.getCompanyId() != null ) {
                condition.append( " and initiator_company=?" );
                args.add( query.getCompanyId() );
            }
        });
    }

}
