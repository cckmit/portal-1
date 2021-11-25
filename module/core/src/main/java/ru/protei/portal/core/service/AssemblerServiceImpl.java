package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.CaseObjectMeta;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.query.CaseLinkQuery;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;

import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.config.MainConfiguration.BACKGROUND_TASKS;
import static ru.protei.portal.core.model.ent.CaseObject.Columns.CASE_NAME;
import static ru.protei.portal.core.model.ent.CaseObject.Columns.INFO;
import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;


public class AssemblerServiceImpl implements AssemblerService {
    private static final Logger log = LoggerFactory.getLogger( AssemblerServiceImpl.class );

    @Autowired
    EventPublisherService publisherService;
    @Autowired
    CaseCommentDAO caseCommentDAO;
    @Autowired
    CaseObjectDAO caseObjectDAO;
    @Autowired
    CaseObjectMetaDAO caseObjectMetaDAO;
    @Autowired
    CaseLinkDAO caseLinkDAO;
    @Autowired
    AttachmentDAO attachmentDAO;
    @Autowired
    PersonDAO personDAO;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Async(BACKGROUND_TASKS)
    @Override
    public void proceed( final AssembledCaseEvent sourceEvent ) {
        log.info( "proceed(): {}", sourceEvent );
        if (sourceEvent == null) return;

        fillInitiator(sourceEvent).flatMap(
                this::fillCaseObject).flatMap(
                this::fillCaseNameAndDescription).flatMap(
                this::fillCaseMeta).flatMap(
                this::fillManager).flatMap(
                this::fillComments).flatMap(
                this::fillAttachments).flatMap(
                this::fillLinks).map(
                this::fillEmails).
                ifOk( filledEvent -> publisherService.publishEvent( filledEvent ) );
    }

    private Result<AssembledCaseEvent> fillInitiator( AssembledCaseEvent e ) {
        if (e.getInitiator() != null) {
            log.info("fillInitiator(): CaseObjectID={} initiator is already filled.", e.getCaseObjectId());
            return ok(e);
        }

        log.info("fillInitiator(): CaseObjectID={} Try to fill initiator.", e.getCaseObjectId());
        Person initiator = personDAO.get(e.getInitiatorId());
        jdbcManyRelationsHelper.fill(initiator, Person.Fields.CONTACT_ITEMS);
        e.setInitiator(initiator);
        log.info("fillInitiator(): CaseObjectID={} initiator is successfully filled.", e.getCaseObjectId());

        return ok(e);
    }

    private Result<AssembledCaseEvent> fillManager( AssembledCaseEvent e ) {
        if (e.getManager() != null) {
            log.info("fillManager(): CaseObjectID={} manager is already filled.", e.getCaseObjectId());
            return ok(e);
        }
        if (e.getCaseMeta().getManager() == null) {
            log.info("fillManager(): CaseObjectID={} No manager is set, ignore filling manager.", e.getCaseObjectId());
            return ok(e);
        }

        log.info("fillManager(): CaseObjectID={} Try to fill manager.", e.getCaseObjectId());
        Person manager = personDAO.get(e.getCaseMeta().getManager().getId());
        jdbcManyRelationsHelper.fill(manager, Person.Fields.CONTACT_ITEMS);
        e.setManager(manager);
        log.info("fillManager(): CaseObjectID={} manager is successfully filled.", e.getCaseObjectId());


        if (e.getInitCaseMeta() != null && e.getInitCaseMeta().getManager() != null) {
            log.info("fillPreviousManager(): CaseObjectID={} Try to fill previous manager.", e.getCaseObjectId());
            Person previousManager = personDAO.get(e.getInitCaseMeta().getManager().getId());
            jdbcManyRelationsHelper.fill(previousManager, Person.Fields.CONTACT_ITEMS);
            e.setPreviousManager(previousManager);
            log.info("fillPreviousManager(): CaseObjectID={} previous manager is successfully filled.", e.getCaseObjectId());
        }

        return ok(e);
    }

    private Result<AssembledCaseEvent> fillCaseObject( AssembledCaseEvent e ) {
        if (e.isCaseObjectFilled()) {
            log.info( "fillCaseObject(): CaseObjectID={} caseObject is already filled.", e.getCaseObjectId() );
            return ok( e );
        }

        log.info( "fillCaseObject(): CaseObjectID={} Try to fill caseObject.", e.getCaseObjectId() );

        CaseObject caseObject = caseObjectDAO.get(e.getCaseObjectId());
        e.setLastCaseObject(caseObject);

        log.info( "fillCaseObject(): CaseObjectID={} CaseObject is successfully filled.", e.getCaseObjectId() );
        return ok( e );
    }

    private Result<AssembledCaseEvent> fillCaseNameAndDescription(AssembledCaseEvent e) {
        if (e.isCaseNameFilled() && e.isCaseInfoFilled()) {
            log.info("fillCaseNameAndDescription(): CaseObjectID={} case's Name and Description is already filled.", e.getCaseObjectId());
            return ok(e);
        }

        log.info("fillCaseNameAndDescription(): CaseObjectID={} Try to fill case's Name and Description.", e.getCaseObjectId());

        CaseObject caseObject = caseObjectDAO.partialGet(e.getCaseObjectId(), CASE_NAME, INFO);

        e.getName().setNewState(caseObject.getName());
        e.getInfo().setNewState(caseObject.getInfo());

        log.info("fillCaseNameAndDescription(): CaseObjectID={} case's Name and Description is successfully filled.", e.getCaseObjectId());
        return ok(e);
    }

    private Result<AssembledCaseEvent> fillCaseMeta( AssembledCaseEvent e ) {
        if (e.isCaseMetaFilled()) {
            log.info("fillCaseMeta(): CaseObjectID={} caseMeta is already filled.", e.getCaseObjectId());
            return ok(e);
        }

        log.info("fillCaseMeta(): CaseObjectID={} Try to fill caseMeta.", e.getCaseObjectId());
        CaseObjectMeta caseMeta = caseObjectMetaDAO.get(e.getCaseObjectId());
        e.setLastCaseMeta(caseMeta);
        log.info("fillCaseMeta(): CaseObjectID={} caseMeta is successfully filled.", e.getCaseObjectId());

        return ok(e);
    }

    private Result<AssembledCaseEvent> fillAttachments( AssembledCaseEvent e ) {
        if (e.isAttachmentsFilled()) {
            log.info( "fillAttachments(): CaseObjectID={} Attachments are already filled.", e.getCaseObjectId() );
            return ok( e );
        }
        log.info( "fillAttachments(): CaseObjectID={} Try to fill attachments.", e.getCaseObjectId() );
        e.setExistingAttachments(  attachmentDAO.getAttachmentsByCaseId( e.getCaseObjectId() ) );
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

    private AssembledCaseEvent fillEmails( AssembledCaseEvent e ) {
        if (e.getCreator() != null) jdbcManyRelationsHelper.fill( e.getCreator(), Person.Fields.CONTACT_ITEMS);
        if (e.getInitiator() != null) jdbcManyRelationsHelper.fill( e.getInitiator(), Person.Fields.CONTACT_ITEMS);
        return e;
    }
}
