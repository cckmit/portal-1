package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.AuditObject;
import ru.protei.portal.core.model.query.AuditQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import java.util.List;

/**
 * Audit object DAO
 */
public interface AuditObjectDAO extends PortalBaseDAO<AuditObject> {

    List<AuditObject> getAuditObjectList( AuditQuery query );

    Long insertAudit (AuditObject object);

    @SqlConditionBuilder
    SqlCondition auditQueryCondition ( AuditQuery query);
}
