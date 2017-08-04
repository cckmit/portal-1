package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.AuditObjectDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.AuditQuery;
import java.util.List;

/**
 * Реализация сервиса управления аудитом
 */
public class AuditServiceImpl implements AuditService {

    private static Logger log = LoggerFactory.getLogger( AuditServiceImpl.class );

    @Autowired
    AuditObjectDAO auditObjectDAO;

    @Override
    public CoreResponse< AuditObject > getAuditObject( AuthToken token, long id ) {

        AuditObject auditObject = auditObjectDAO.get( id );

        if ( auditObject == null )
            return new CoreResponse().error( En_ResultStatus.NOT_FOUND );

        return new CoreResponse< AuditObject >().success( auditObject );
    }

    @Override
    public CoreResponse< List< AuditObject > > auditObjectList( AuthToken token, AuditQuery query ) {

        List< AuditObject > list = auditObjectDAO.getAuditObjectList( query );

        if ( list == null )
            return new CoreResponse< List< AuditObject > >().error( En_ResultStatus.GET_DATA_ERROR );

        return new CoreResponse< List< AuditObject > >().success( list );
    }

    @Override
    public CoreResponse< AuditObject > saveAuditObject( AuthToken token, AuditObject auditObject ) {

        if (auditObject == null)
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);

        Long auditId = auditObjectDAO.insertAudit(auditObject);

        if (auditId == null)
            return new CoreResponse().error(En_ResultStatus.NOT_CREATED);

        return new CoreResponse<AuditObject>().success( auditObject );
    }
}
