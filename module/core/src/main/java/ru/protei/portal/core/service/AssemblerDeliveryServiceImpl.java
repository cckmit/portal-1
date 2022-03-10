package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.event.AssembledDeliveryEvent;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.config.MainConfiguration.BACKGROUND_TASKS;
import static ru.protei.portal.core.model.ent.CaseObject.Columns.*;
import static ru.protei.portal.core.model.ent.Delivery.Fields.KITS;

public class AssemblerDeliveryServiceImpl implements AssemblerDeliveryService {
    private static final Logger log = LoggerFactory.getLogger(AssemblerDeliveryServiceImpl.class);

    @Autowired
    PersonDAO personDAO;
    @Autowired
    CaseObjectDAO caseObjectDAO;
    @Autowired
    DeliveryDAO deliveryDAO;
    @Autowired
    CaseCommentDAO caseCommentDAO;
    @Autowired
    ProjectDAO projectDAO;
    @Autowired
    AttachmentDAO attachmentDAO;
    @Autowired
    EventPublisherService publisherService;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;
    @Autowired
    DevUnitDAO devUnitDAO;

    @Async(BACKGROUND_TASKS)
    @Override
    public void proceed(final AssembledDeliveryEvent sourceEvent) {
        log.info("proceed(): {}", sourceEvent);
        if (sourceEvent == null) {
            return;
        }

        fillDelivery(sourceEvent)
                .flatMap(this::fillDeliveryNameAndDescription)
                .flatMap(this::fillKits)
                .flatMap(this::fillInitiator)
                .flatMap(this::fillComments)
                .flatMap(this::fillAttachments)
                .ifOk(filledEvent -> publisherService.publishEvent(filledEvent));
    }

    private Result<AssembledDeliveryEvent> fillDeliveryNameAndDescription(AssembledDeliveryEvent e) {
        if (e.isDeliveryNameFilled() && e.isDeliveryInfoFilled()) {
            log.info("fillDeliveryNameAndDescription(): DeliveryId={} Delivery Name and Description is already filled.", e.getDeliveryId());
            return ok(e);
        }

        log.info("fillDeliveryNameAndDescription(): DeliveryId={} Try to fill Delivery Name and Description.", e.getDeliveryId());

        CaseObject caseObject = caseObjectDAO.partialGet(e.getDeliveryId(), CASE_NAME, INFO);

        e.getName().setNewState(caseObject.getName());
        e.getInfo().setNewState(caseObject.getInfo());

        log.info("fillDeliveryNameAndDescription(): DeliveryId={} Delivery Name and Description is successfully filled.", e.getDeliveryId());
        return ok(e);
    }

    //контактное лицо
    private Result<AssembledDeliveryEvent> fillInitiator(AssembledDeliveryEvent event) {
        if (event.getInitiator() != null) {
            return ok(event);
        }

        Person initiator = personDAO.get(event.getInitiatorId());
        jdbcManyRelationsHelper.fill(initiator, Person.Fields.CONTACT_ITEMS);
        event.setInitiator(initiator);

        return ok(event);
    }

    private Result<AssembledDeliveryEvent> fillKits(AssembledDeliveryEvent event) {

        jdbcManyRelationsHelper.fill( event.getNewDeliveryState(), KITS );
        return ok(event);
    }

    private Result<AssembledDeliveryEvent> fillDelivery(AssembledDeliveryEvent event) {
        if (event.isDeliveryFilled()) {
            return ok(event);
        }

        Delivery delivery = deliveryDAO.get(event.getDeliveryId());
        jdbcManyRelationsHelper.fillAll(delivery);
        delivery.getProject().setProducts(new HashSet<>(devUnitDAO.getProjectProducts(delivery.getProject().getId())));

        event.setNewDeliveryState(delivery);

        return ok(event);
    }

    private Result<AssembledDeliveryEvent> fillComments(AssembledDeliveryEvent event) {
        if (event.isCaseCommentsFilled()) {
            log.info("fillComments(): deliveryId={} Comments are already filled.", event.getDeliveryId());
            return ok(event);
        }

        Date upperBoundDate = addSeconds(new Date(), 1);

        log.info("fillComments(): deliveryId={} Try to fill comments.", event.getDeliveryId());
        List<CaseComment> caseComments = caseCommentDAO.getCaseComments(new CaseCommentQuery(event.getDeliveryId(), upperBoundDate));
        jdbcManyRelationsHelper.fill(CollectionUtils.emptyIfNull(caseComments), "caseAttachments");

        event.setExistingComments(caseComments);
        log.info("fillComments(): deliveryId={} Comments are successfully filled.", event.getDeliveryId());

        return ok(event);
    }

    private Result<AssembledDeliveryEvent> fillAttachments(AssembledDeliveryEvent event) {
        if (event.isAttachmentsFilled()) {
            log.info("fillAttachments(): deliveryId={} Attachments are already filled.", event.getDeliveryId());
            return ok(event);
        }
        log.info("fillAttachments(): CaseObjectID={} Try to fill attachments.", event.getDeliveryId());
        event.setExistingAttachments(attachmentDAO.getAttachmentsByCaseId(event.getDeliveryId()));
        log.info("fillAttachments(): CaseObjectID={} Attachments are successfully filled.", event.getDeliveryId());

        return ok(event);
    }

    private Date addSeconds(Date date, int sec) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, sec);
        return calendar.getTime();
    }
}
