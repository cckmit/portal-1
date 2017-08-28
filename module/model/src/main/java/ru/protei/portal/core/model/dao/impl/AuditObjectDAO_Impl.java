package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.AuditObjectDAO;
import ru.protei.portal.core.model.struct.AuditObject;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.AuditQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import java.util.List;

/**
 * Audit object DAO Impl
 */
public class AuditObjectDAO_Impl extends PortalBaseJdbcDAO<AuditObject> implements AuditObjectDAO {
    @Override
    public List< AuditObject > getAuditObjectList( AuditQuery query ) {
        return listByQuery(query);
    }

    @Override
    public Long insertAudit( AuditObject object ) {
        return persist(object);
    }

    @SqlConditionBuilder
    public SqlCondition auditQueryCondition( AuditQuery query ) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("1=1");

            if ( query.getId() != null ) {
                condition.append( " and audit.id=?" );
                args.add( query.getId() );
            }

            if ( query.getType() != null ) {
                condition.append( " and type=?" );
                args.add( query.getType().getId() );
            }

            if ( query.getCreatorId() != null ) {
                condition.append( " and creator=?" );
                args.add( query.getCreatorId() );
            }

            if ( query.getFrom() != null ) {
                condition.append( " and case_object.created >= ?" );
                args.add( query.getFrom() );
            }

            if ( query.getTo() != null ) {
                condition.append( " and case_object.created < ?" );
                args.add( query.getTo() );
            }

            //TODO CRM-16: какие параметры кроме логина и хоста создателя могут быть в строке поиска?
            if (query.getSearchString() != null && !query.getSearchString().trim().isEmpty()) {
                condition.append( " and ( creator_shortname like ? or creator_ip like ?)" );
                String likeArg = HelperFunc.makeLikeArg(query.getSearchString(), true);
                args.add(likeArg);
                args.add(likeArg);
            }

        });
    }
}
