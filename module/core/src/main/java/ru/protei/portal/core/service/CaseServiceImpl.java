package ru.protei.portal.core.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.CaseQuery;

import java.util.Date;
import java.util.List;

/**
 * Реализация сервиса управления контактами
 */
public class CaseServiceImpl implements CaseService {

    private static Logger log = LoggerFactory.getLogger(CaseServiceImpl.class);

    @Autowired
    CaseObjectDAO caseObjectDAO;

    @Override
    public CoreResponse<List<CaseObject>> caseObjectList(CaseQuery query) {
        List<CaseObject> list = caseObjectDAO.getCases( query );

        if ( list == null )
            new CoreResponse<List<CaseObject>>().error(En_ResultStatus.GET_DATA_ERROR);

        return new CoreResponse<List<CaseObject>>().success(list);
    }

    @Override
    public CoreResponse<CaseObject> getCaseObject(long id) {
        CaseObject caseObject = caseObjectDAO.get( id );

        return caseObject != null ? new CoreResponse<CaseObject>().success(caseObject)
                : new CoreResponse<CaseObject>().error(En_ResultStatus.NOT_FOUND);
    }

    @Override
    public CoreResponse< CaseObject > saveCaseObject( CaseObject p ) {
        return new CoreResponse<CaseObject>().success( p );
    }
}
