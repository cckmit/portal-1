package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.AuditObjectDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.query.AuditQuery;
import ru.protei.portal.core.model.struct.AuditObject;

import java.util.List;

import static ru.protei.portal.api.struct.CoreResponse.error;
import static ru.protei.portal.api.struct.CoreResponse.ok;

/**
 * Реализация сервиса управления аудитом
 */
public class AuditServiceImpl implements AuditService {

    private static Logger log = LoggerFactory.getLogger( AuditServiceImpl.class );

    @Autowired
    AuditObjectDAO auditObjectDAO;

    @Override
    public CoreResponse< AuditObject > getAuditObject( long id ) {

        AuditObject auditObject = auditObjectDAO.get( id );

        if ( auditObject == null )
            return error( En_ResultStatus.NOT_FOUND );

        return ok( auditObject );
    }

    @Override
    public CoreResponse< List< AuditObject > > auditObjectList( AuditQuery query ) {

        List< AuditObject > list = auditObjectDAO.getAuditObjectList( query );

        if ( list == null )
            return error( En_ResultStatus.GET_DATA_ERROR );

        return ok( list );
    }

    @Override
    public CoreResponse< AuditObject > saveAuditObject( AuditObject auditObject ) {

        if (auditObject == null)
            return error( En_ResultStatus.INCORRECT_PARAMS);

        Long auditId = auditObjectDAO.insertAudit(auditObject);

        if (auditId == null)
            return error( En_ResultStatus.NOT_CREATED);

        log.debug( "Audit object with id = {} successfully created", auditId );
        return ok( auditObject );
    }
}
