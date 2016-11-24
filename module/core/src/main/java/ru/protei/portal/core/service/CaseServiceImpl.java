package ru.protei.portal.core.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.CaseStateMatrixDAO;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.query.CaseQuery;

import java.util.Date;
import java.util.List;

/**
 * Реализация сервиса управления обращениями
 */
public class CaseServiceImpl implements CaseService {

    private static Logger log = LoggerFactory.getLogger(CaseServiceImpl.class);

    @Autowired
    CaseObjectDAO caseObjectDAO;

    @Autowired
    CaseStateMatrixDAO caseStateMatrixDAO;

    @Override
    public CoreResponse<List<CaseObject>> caseObjectList(CaseQuery query) {
        List<CaseObject> list = caseObjectDAO.getCases( query );

        if ( list == null )
            return new CoreResponse<List<CaseObject>>().error(En_ResultStatus.GET_DATA_ERROR);

        return new CoreResponse<List<CaseObject>>().success(list);
    }

    @Override
    public CoreResponse<CaseObject> getCaseObject(long id) {
        CaseObject caseObject = caseObjectDAO.get( id );

        return caseObject != null ? new CoreResponse<CaseObject>().success(caseObject)
                : new CoreResponse<CaseObject>().error(En_ResultStatus.NOT_FOUND);
    }

    @Override
    public CoreResponse< CaseObject > saveCaseObject( CaseObject caseObject ) {
        if (caseObject == null)
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);

        Date now = new Date();
        caseObject.setCreated(now);
        caseObject.setModified(now);

        Long caseId = caseObjectDAO.insertCase(caseObject);

        if (caseId == null)
            return new CoreResponse().error(En_ResultStatus.NOT_CREATED);

        return new CoreResponse<CaseObject>().success( caseObject );
    }

    @Override
    public CoreResponse< CaseObject > updateCaseObject( CaseObject caseObject ) {
        if (caseObject == null)
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);

        caseObject.setModified(new Date());
        boolean isUpdated = caseObjectDAO.merge(caseObject);

        if (!isUpdated)
            return new CoreResponse().error(En_ResultStatus.NOT_UPDATED);

        return new CoreResponse<CaseObject>().success( caseObject );
    }

    @Override
    public CoreResponse<List<En_CaseState>> stateList( En_CaseType caseType ) {
        List<En_CaseState> states = caseStateMatrixDAO.getStatesByCaseType(caseType);

        if (states == null)
            return new CoreResponse<List<En_CaseState>>().error(En_ResultStatus.GET_DATA_ERROR);

        return new CoreResponse<List<En_CaseState>>().success(states);
    }


    @Override
    public CoreResponse<Long> count(CaseQuery query) {
        Long count = caseObjectDAO.count(query);

        if (count == null)
            return new CoreResponse<Long>().error(En_ResultStatus.GET_DATA_ERROR);

        return new CoreResponse<Long>().success(count);
    }
}
