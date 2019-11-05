package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.service.auth.AuthService;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.config.MainConfiguration.BACKGROUND_TASKS;


public class AssemblerServiceImpl implements AsseblerService {

    @Async(BACKGROUND_TASKS)
    @Override
    public void proceed( final AssembledCaseEvent sourceEvent ) {
        if (sourceEvent == null) return;

        fillCaseObject( sourceEvent ).flatMap(
                this::fillComments ).ifOk( filledEvent ->
                publisherService.publishEvent( filledEvent ) );
    }

    private Result<AssembledCaseEvent> fillCaseObject( AssembledCaseEvent e ) {
        if (e.isCaseObjectFilled()) {
            log.info( "fillCaseObject(): CaseObjectID={} caseObject is already filled.", e.getCaseObjectId() );
            return ok( e );
        }

       log.info( "fillCaseObject(): CaseObjectID={} Try to fill caseObject.", e.getCaseObjectId() );

        e.setLastCaseObject( caseObjectDAO.get( e.getCaseObjectId() ) );

        log.info( "fillCaseObject(): CaseObjectID={} CaseObject is successfully filled.", e.getCaseObjectId() );
        return ok( e );
    }

    private Result<AssembledCaseEvent> fillComments( AssembledCaseEvent e ) {
        if (e.isCaseCommentsFilled()) {
            log.info( "fillComments(): CaseObjectID={} Comments are already filled.", e.getCaseObjectId() );
            return ok( e );
        }

        log.info( "fillComments(): CaseObjectID={} Try to fill comments.", e.getCaseObjectId() );
        Date upperBoundDate = makeCommentUpperBoundDate( e );

        e.setInitialCaseComments( caseCommentDAO.getCaseComments(new CaseCommentQuery( e.getCaseObjectId(), upperBoundDate )) );

        log.info( "fillComments(): CaseObjectID={} Comments are successfully filled.", e.getCaseObjectId() );
        return ok( e );
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
    CaseCommentDAO caseCommentDAO;

    @Autowired
    CaseObjectDAO caseObjectDAO;


    private static final Logger log = LoggerFactory.getLogger( AssemblerServiceImpl.class );
}
