package ru.protei.portal.core.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.CaseShortViewDAO;
import ru.protei.portal.core.model.dao.CaseStateMatrixDAO;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseShortView;

import java.util.Calendar;
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
    CaseShortViewDAO caseShortViewDAO;

    @Autowired
    CaseStateMatrixDAO caseStateMatrixDAO;

    @Autowired
    CaseCommentDAO caseCommentDAO;

    @Override
    public CoreResponse<List<CaseShortView>> caseObjectList( CaseQuery query) {
        List<CaseShortView> list = caseShortViewDAO.getCases( query );

        if ( list == null )
            return new CoreResponse<List<CaseShortView>>().error(En_ResultStatus.GET_DATA_ERROR);

        return new CoreResponse<List<CaseShortView>>().success(list);
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
    public CoreResponse<List<CaseComment>> getCaseCommentList( long caseId ) {
        List<CaseComment> list = caseCommentDAO.getCaseComments( caseId );

        if ( list == null )
            return new CoreResponse<List<CaseComment>>().error(En_ResultStatus.GET_DATA_ERROR);

        return new CoreResponse<List<CaseComment>>().success(list);
    }

    @Override
    @Transactional
    public CoreResponse<CaseComment> addCaseComment( CaseComment caseComment ) {
        if ( caseComment == null )
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);

        Date now = new Date();
        caseComment.setCreated(now);

        Long commentId = caseCommentDAO.persist(caseComment);

        if (commentId == null)
            return new CoreResponse().error(En_ResultStatus.NOT_CREATED);

        boolean isCaseChanged = updateCaseModified ( caseComment.getCaseId(), caseComment.getCreated() );

        if (!isCaseChanged)
            throw new RuntimeException( "failed to update case modifiedDate " );

        CaseComment result = caseCommentDAO.get( commentId );

        return new CoreResponse<CaseComment>().success( result );
    }

    @Override
    @Transactional
    public CoreResponse<CaseComment> updateCaseComment ( CaseComment caseComment, Long personId ) {
        if (caseComment == null || caseComment.getId() == null || personId == null)
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);

        if (!personId.equals(caseComment.getAuthorId()) || !isChangeAvailable ( caseComment.getCreated() ))
            return new CoreResponse().error( En_ResultStatus.NOT_UPDATED );

        boolean isUpdated = caseCommentDAO.merge(caseComment);

        if (!isUpdated)
            return new CoreResponse().error( En_ResultStatus.NOT_UPDATED );

        boolean isCaseChanged = updateCaseModified ( caseComment.getCaseId(), new Date() );

        if (!isCaseChanged)
            throw new RuntimeException( "failed to update case modifiedDate " );

        return new CoreResponse<CaseComment>().success( caseComment );
    }


    @Override
    @Transactional
    public CoreResponse removeCaseComment( CaseComment caseComment, Long personId ) {
        if (caseComment == null || caseComment.getId() == null || personId == null)
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);

        if (!personId.equals(caseComment.getAuthorId()) || !isChangeAvailable ( caseComment.getCreated() ))
            return new CoreResponse().error(En_ResultStatus.NOT_REMOVED);

        long caseId = caseComment.getCaseId();

        boolean isRemoved = caseCommentDAO.remove(caseComment);

        if (!isRemoved)
            return new CoreResponse().error( En_ResultStatus.NOT_REMOVED );

        boolean isCaseChanged = updateCaseModified ( caseId, new Date() );

        if (!isCaseChanged)
            throw new RuntimeException( "failed to update case modifiedDate " );

        return new CoreResponse<Boolean>().success(isRemoved);
    }



    @Override
    public CoreResponse<Long> count(CaseQuery query) {
        Long count = caseObjectDAO.count(query);

        if (count == null)
            return new CoreResponse<Long>().error(En_ResultStatus.GET_DATA_ERROR);

        return new CoreResponse<Long>().success(count);
    }

    private boolean updateCaseModified ( Long caseId,  Date modified ) {
        if (caseId == null)
            return false;

        CaseObject caseObject = caseObjectDAO.get( caseId );
        if (caseObject == null)
            return false;

        caseObject.setModified(modified);
        boolean isUpdated = caseObjectDAO.merge(caseObject);

        return isUpdated;
    }

    private boolean isChangeAvailable ( Date date ) {
        Calendar c = Calendar.getInstance();
        long current = c.getTimeInMillis();
        c.setTime( date );
        long checked = c.getTimeInMillis();

        return current - checked < CHANGE_LIMIT_TIME;
    }

    static final long CHANGE_LIMIT_TIME = 300000;  // 5 минут  (в мсек)
}
