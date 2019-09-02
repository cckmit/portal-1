package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.query.AuditQuery;
import ru.protei.portal.core.model.struct.AuditObject;

import java.util.List;

/**
 * Сервис управления аудитом
 */
public interface AuditService {

    Result<AuditObject > getAuditObject( long id );

    Result<List<AuditObject> > auditObjectList( AuditQuery query );

    Result<AuditObject> saveAuditObject( AuditObject auditObject );
}
