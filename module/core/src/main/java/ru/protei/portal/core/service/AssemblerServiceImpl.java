package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.dao.AttachmentDAO;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.dao.CaseLinkDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.query.CaseLinkQuery;
import ru.protei.portal.core.model.struct.CaseNameAndDescriptionChangeRequest;

import java.util.*;

import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.config.MainConfiguration.BACKGROUND_TASKS;
import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;


public class AssemblerServiceImpl implements AssemblerService {

    @Async(BACKGROUND_TASKS)
    @Override
    public void proceed( final AssembledCaseEvent sourceEvent ) {
        if (sourceEvent == null) return;

        fillCaseObject( sourceEvent ).flatMap(
                this::fillCaseNameAndDescription ).flatMap(
                this::fillComments ).flatMap(
                this::fillAttachments ).flatMap(
                this::fillLinks ).ifOk( filledEvent ->
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

    private Result<AssembledCaseEvent> fillCaseNameAndDescription(AssembledCaseEvent e) {
        if (e.isCaseNameAndDescriptionFilled()) {
            log.info("fillCaseNameAndDescription(): CaseObjectID={} case's Name and Description is already filled.", e.getCaseObjectId());
            return ok(e);
        }

        log.info("fillCaseNameAndDescription(): CaseObjectID={} Try to fill case's Name and Description.", e.getCaseObjectId());

        e.getNameAndDescription().setNewState(new CaseNameAndDescriptionChangeRequest(caseObjectDAO.get(e.getCaseObjectId())));

        log.info("fillCaseNameAndDescription(): CaseObjectID={} case's Name and Description is successfully filled.", e.getCaseObjectId());
        return ok(e);
    }

    private Result<AssembledCaseEvent> fillAttachments( AssembledCaseEvent e ) {//TODO
        if (e.isAttachmentsFilled()) {
            log.info( "fillAttachments(): CaseObjectID={} Attachments are already filled.", e.getCaseObjectId() );
            return ok( e );
        }
        log.info( "fillAttachments(): CaseObjectID={} Try to fill attachments.", e.getCaseObjectId() );
        e.setExistingAttachments(  attachmentDAO.getListByCaseId( e.getCaseObjectId() ));
        log.info( "fillAttachments(): CaseObjectID={} Attachments are successfully filled.", e.getCaseObjectId() );

        return ok( e );
    }

    private Result<AssembledCaseEvent> fillLinks( AssembledCaseEvent e ) {
        if (e.isLinksFilled()) {
            log.info( "fillLinks(): CaseObjectID={} Links are already filled.", e.getCaseObjectId() );
            return ok( e );
        }

        log.info( "fillLinks(): CaseObjectID={} Try to fill links.", e.getCaseObjectId() );
        e.setExistingLinks( caseLinkDAO.getListByQuery( new CaseLinkQuery( e.getCaseObjectId(), false ) ) );
        log.info( "fillLinks(): CaseObjectID={} Links are successfully filled.", e.getCaseObjectId() );

        return ok( e );
    }

    private Result<AssembledCaseEvent> fillComments( AssembledCaseEvent e ) {
        if (e.isCaseCommentsFilled()) {
            log.info( "fillComments(): CaseObjectID={} Comments are already filled.", e.getCaseObjectId() );
            return ok( e );
        }

        log.info( "fillComments(): CaseObjectID={} Try to fill comments.", e.getCaseObjectId() );
        Date upperBoundDate = makeCommentUpperBoundDate( e );

        e.setExistingCaseComments( caseCommentDAO.getCaseComments( new CaseCommentQuery( e.getCaseObjectId(), upperBoundDate ) ) );

        log.info( "fillComments(): CaseObjectID={} Comments are successfully filled.", e.getCaseObjectId() );
        return ok( e );
    }

    private Date makeCommentUpperBoundDate( AssembledCaseEvent event ) {
        List<CaseComment> allComments = event.getAllComments();

        Date upperBoundDate;
        if (isEmpty( allComments )) {
            upperBoundDate = new Date( event.getLastUpdated() );
        } else {
            Optional<CaseComment> first = allComments.stream()
                    .sorted( Comparator.comparing( CaseComment::getCreated, Date::compareTo ).reversed() ).findFirst();
            upperBoundDate = first.get().getCreated();
        }

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

    @Autowired
    CaseLinkDAO caseLinkDAO;

    @Autowired
    AttachmentDAO attachmentDAO;

    private static final Logger log = LoggerFactory.getLogger( AssemblerServiceImpl.class );
}
