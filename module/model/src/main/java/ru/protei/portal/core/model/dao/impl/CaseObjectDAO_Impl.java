package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.CaseTypeDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by michael on 19.05.16.
 */
public class CaseObjectDAO_Impl extends PortalBaseJdbcDAO<CaseObject> implements CaseObjectDAO {

    @Autowired
    CaseTypeDAO caseTypeDAO;

    public Map<Long,Long> getNumberToIdMap (En_CaseType caseType) {
        Map<Long, Long> numberToIdMap = new HashMap<>();

        partialGetListByCondition("CASE_TYPE=?", Collections.singletonList(caseType.getId()),"id","CASENO")
                .forEach(o -> numberToIdMap.put(o.getCaseNumber(), o.getId()));

        return numberToIdMap;
    }

    @Override
    public Long getCaseId(En_CaseType caseType, long number) {
        CaseObject obj = partialGetByCondition("case_type=? and caseno=?", Arrays.asList(caseType.getId(), number), getIdColumnName());
        return obj != null ? obj.getId() : null;
    }

    @Override
    public CaseObject getCase(En_CaseType caseType, long number) {
        return getByCondition("case_type=? and caseno=?", caseType.getId(), number);
    }

    @Override
    public Long insertCase(CaseObject object) {

        En_CaseType type = object.getCaseType();

        Long caseNumber = caseTypeDAO.generateNextId(type);//HelperFunc.nvlt(getMaxValue("CASENO", Long.class, "case_type=?", type.getId()),0L) + 1;

        object.setCaseNumber(caseNumber);
        object.setExtId(type.makeGUID(caseNumber));

        return persist(object);
    }




    @Override
    public List< CaseObject > getCases( CaseQuery query ) {
//        SqlCondition condition = caseQueryCondition( query );
//        return partialGetListByCondition( condition.condition, condition.args, query.offset, query.limit, TypeConverters.createSort( query ),
//                "id", "CASENO", "IMPORTANCE", "STATE", "CREATED", "INFO", "InitiatorName" ).getResults();

        return listByQuery(query);
    }

    @SqlConditionBuilder
    public SqlCondition caseQueryCondition ( CaseQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("1=1");

            if ( query.getId() != null ) {
                condition.append( " and case_object.id=?" );
                args.add( query.getId() );
            }

            if ( !query.isPrivateAccess() ) {
                condition.append( " and private_flag=?" );
                args.add( 0 );
            }

            if ( query.getType() != null ) {
                condition.append( " and case_type=?" );
                args.add( query.getType().getId() );
            }

            if ( query.getCaseNo() != null ) {
                condition.append( " and caseno=?" );
                args.add( query.getCaseNo() );
            }

            if ( query.getCompanyId() != null ) {
                condition.append( " and initiator_company=?" );
                args.add( query.getCompanyId() );
            }

            if ( query.getProductId() != null ) {
                condition.append( " and product_id=?" );
                args.add( query.getProductId() );
            }

            if ( query.getManagerId() != null) {
                if(query.getManagerId() > 0) {
                    condition.append(" and manager=?");
                    args.add(query.getManagerId());
                }else{
                    condition.append( " and manager is null" );
                }
            }

            if ( query.getStateIds() != null && !query.getStateIds().isEmpty() ) {
                condition.append(" and state in (" + query.getStateIds().stream().map(Object::toString).collect( Collectors.joining(",")) + ")");
            }

            if ( query.getImportanceIds() != null && !query.getImportanceIds().isEmpty() ) {
                condition.append(" and importance in (" + query.getImportanceIds().stream().map(Object::toString).collect( Collectors.joining(",")) + ")");
            }

            if ( query.getFrom() != null ) {
                condition.append( " and case_object.created >= ?" );
                args.add( query.getFrom() );
            }

            if ( query.getTo() != null ) {
                condition.append( " and case_object.created < ?" );
                args.add( query.getTo() );
            }

            if (query.getSearchString() != null && !query.getSearchString().trim().isEmpty()) {
                condition.append( " and ( case_name like ? or case_object.info like ?)" );
                String likeArg = HelperFunc.makeLikeArg(query.getSearchString(), true);
                args.add(likeArg);
                args.add(likeArg);
            }

        });
    }
}
