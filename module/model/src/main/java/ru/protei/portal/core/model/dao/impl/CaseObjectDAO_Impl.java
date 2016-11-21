package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CaseQuery;
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
        StringBuilder conditions = new StringBuilder( "1=1" );

        ArrayList args = new ArrayList();

        if ( query.getType() != null ) {
            conditions.append( " and case_type=?" );
            args.add( query.getType().getId() );
        }

        if ( query.getCompanyId() != null ) {
            conditions.append( " and initiator_company=?" );
            args.add( query.getCompanyId() );
        }

        if ( query.getProductId() != null ) {
            conditions.append( " and product_id=?" );
            args.add( query.getProductId() );
        }

        if ( query.getStateId() != null ) {
            conditions.append( " and state=?" );
            args.add( query.getStateId() );
        }

        if ( query.getImportanceId() != null ) {
            conditions.append( " and importance=?" );
            args.add( query.getImportanceId() );
        }

        if (query.getSearchString() != null && !query.getSearchString().trim().isEmpty()) {
            conditions.append( " and ( case_name like ? or case_object.info like ? )" );
            String likeArg = HelperFunc.makeLikeArg(query.getSearchString(), true);
            args.add(likeArg);
            args.add(likeArg);
        }

        return getListByCondition( conditions.toString(), args, query.offset, query.limit, TypeConverters.createSort( query ) ).getResults();
    }
}
