package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.AuditObject;
import ru.protei.portal.core.model.query.AuditQuery;

import java.util.List;

/**
 * Сервис управления аудитом
 */
public interface AuditService {

    CoreResponse<AuditObject > getAuditObject( long id );

    CoreResponse<List<AuditObject> > auditObjectList( AuditQuery query );

    CoreResponse<AuditObject> saveAuditObject( AuditObject auditObject );
}
