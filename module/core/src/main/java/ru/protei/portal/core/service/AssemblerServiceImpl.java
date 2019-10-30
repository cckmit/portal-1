package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.query.CaseCommentQuery;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;


public class AssemblerServiceImpl implements AsseblerService {
    @Override
    public void proceed( final AssembledCaseEvent assembledEvent ) {
        CompletableFuture.supplyAsync( () ->
                assembleEvent( assembledEvent ), assebleExecutor ).thenAccept( assembleResult ->
                assembleResult.ifOk( assembledCaseEvent ->
                        publisherService.publishEvent( assembledCaseEvent ) )

        );
    }

    private Result<AssembledCaseEvent> assembleEvent( AssembledCaseEvent fromEvent ) {
        log.warn( "assembleEvent(): Not implemented." );//TODO NotImplemented
        if (fromEvent == null) return Result.error( En_ResultStatus.INCORRECT_PARAMS );

        Result<AssembledCaseEvent> result = ok( fromEvent );

        result.flatMap( e -> {

            if (e.isCaseObjectFilled()) {
                log.info( "assembleEvent(): caseObject is filled" );
                return ok( e );
            }

            log.info( "assembleEvent(): Try to fill caseObject" );
            return caseService.getCaseObject( at, e.getCaseNumber() ).map( co -> {
                e.setInitialCaseObject( co );
                return e;
            } ).ifOk( r-> log.info( "assembleEvent(): CaseObject is filled." ) );

        } ).flatMap( e -> {

            CaseObject caseObject = e.getCaseObject();

            if (e.isCaseCommentsFilled()) {
                log.info( "assembleEvent(): Comments are filled" );
                return ok( e );
            }

            log.info( "assembleEvent(): Try to fill comments" );
            Date upperBoundDate = makeCommentUpperBoundDate( e );
            return caseCommentService.getCaseCommentList( at, En_CaseType.CRM_SUPPORT,
                    new CaseCommentQuery( caseObject.getId(), upperBoundDate ) )
                    .map( caseComments -> {
                        e.setInitialCaseComments( caseComments );
                        return e;
                    } ).ifOk( r-> log.info( "assembleEvent(): Comments are filled." ) );

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
    CaseService caseService;

    AuthToken at = new AuthToken("", "");//TODO AuthToken for assemble event


    private final ExecutorService assebleExecutor = Executors.newCachedThreadPool( new ThreadFactory() {//tofo fixetThreadPool
        @Override
        public Thread newThread( Runnable r ) {
            return new Thread( r, "assemble-event-pool-" + threadNumber.getAndIncrement() );
        }
    } );
    private final AtomicInteger threadNumber = new AtomicInteger( 1 );
    private static final Logger log = LoggerFactory.getLogger( AssemblerServiceImpl.class );
}
