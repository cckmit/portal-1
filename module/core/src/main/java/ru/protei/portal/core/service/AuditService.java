package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.struct.AuditObject;
import ru.protei.portal.core.model.query.AuditQuery;
import ru.protei.portal.core.model.struct.AuditableObject;

import java.util.List;

/**
 * Сервис управления аудитом
 */
public interface AuditService {

    CoreResponse publishAuditObject(AuthToken token, En_AuditType auditType, AuditableObject auditableObject);

    CoreResponse<AuditObject > getAuditObject( long id );

    CoreResponse<List<AuditObject> > auditObjectList( AuditQuery query );

    CoreResponse<AuditObject> saveAuditObject( AuditObject auditObject );
}
