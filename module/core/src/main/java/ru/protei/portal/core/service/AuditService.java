package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.AuditObject;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.query.AuditQuery;

import java.util.List;

/**
 * Сервис управления аудитом
 */
public interface AuditService {

    CoreResponse<AuditObject > getAuditObject( AuthToken token, long id );

    CoreResponse<List<AuditObject> > auditObjectList( AuthToken token, AuditQuery query );

    CoreResponse<AuditObject> saveAuditObject( AuthToken token, AuditObject auditObject );
}
