package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.query.CaseCommentQuery;

import java.util.Calendar;
import java.util.Date;

import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.config.MainConfiguration.BACKGROUND_BLOCKED_TASKS;


public class AssemblerServiceImpl implements AsseblerService {

    @Async(BACKGROUND_BLOCKED_TASKS)
    @Override
    public void proceed( final AssembledCaseEvent assembledEvent ) {
        assembleEvent( assembledEvent ).ifOk( assembledCaseEvent ->
                publisherService.publishEvent( assembledCaseEvent ) );
    }

    private Result<AssembledCaseEvent> assembleEvent( AssembledCaseEvent fromEvent ) {
        if (fromEvent == null) return Result.error( En_ResultStatus.INCORRECT_PARAMS );

        Result<AssembledCaseEvent> result = ok( fromEvent );

        result.flatMap( e -> {

            if (e.isCaseObjectFilled()) {
                log.info( "assembleEvent(): CaseObjectID={} caseObject is filled.", e.getCaseObjectId() );
                return ok( e );
            }

            log.info( "assembleEvent(): CaseObjectID={} Try to fill caseObject." );
            e.setLastCaseObject( caseObjectDAO.get( e.getCaseObjectId() ) );
//            return caseService.getCaseObjectById( at, e.getCaseObjectId() ).map( co -> {//TODO проблемы авторизации и проверки прав hasAccessForCaseObject(...)
//                e.setInitialCaseObject( co );
//                return e;
//            } ).ifOk( r-> log.info( "assembleEvent(): CaseObjectID={} CaseObject is filled.", e.getCaseObjectId() ) );
            log.info( "assembleEvent(): CaseObjectID={} CaseObject is filled.", e.getCaseObjectId() );
            return ok(e);

        } ).flatMap( e -> {

            CaseObject caseObject = e.getCaseObject();

            if (e.isCaseCommentsFilled()) {
                log.info( "assembleEvent(): CaseObjectID={} Comments are filled.", e.getCaseObjectId() );
                return ok( e );
            }

            log.info( "assembleEvent(): CaseObjectID={} Try to fill comments.", e.getCaseObjectId() );
            Date upperBoundDate = makeCommentUpperBoundDate( e );
            return caseCommentService.getCaseCommentList( at, En_CaseType.CRM_SUPPORT, new CaseCommentQuery( caseObject.getId(), upperBoundDate ) )
                    .map( caseComments -> {
                        e.setInitialCaseComments( caseComments );
                        return e;
                    } ).ifOk( r-> log.info( "assembleEvent(): CaseObjectID={} Comments are filled.", e.getCaseObjectId() ) );

        } )
        ;

        return result;
    }

    private Date makeCommentUpperBoundDate( AssembledCaseEvent event ) {
        Date upperBoundDate = event.getCaseComment() == null || event.getRemovedComment() != null ?
                new Date( event.getLastUpdated() ) :
                event.getCaseComment().getCreated();
        return addSeconds( upperBoundDate, 1 );
    }

    private Date addSeconds( Date date, int sec ) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( date );
        calendar.add( Calendar.SECOND, sec );
        return calendar.getTime();
    }

    @Autowired
    EventPublisherService publisherService;

    @Autowired
    CaseCommentService caseCommentService;

    @Autowired
//    CaseService caseService;
    CaseObjectDAO caseObjectDAO;

    AuthToken at = null;//TODO AuthToken for assemble event


    private static final Logger log = LoggerFactory.getLogger( AssemblerServiceImpl.class );
}
