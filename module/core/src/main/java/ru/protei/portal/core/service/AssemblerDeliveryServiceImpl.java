package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.event.AssembledDeliveryEvent;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.portal.schedule.PortalScheduleTasks;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.config.MainConfiguration.BACKGROUND_TASKS;

public class AssemblerDeliveryServiceImpl implements AssemblerDeliveryService {
    private static final Logger log = LoggerFactory.getLogger(AssemblerDeliveryServiceImpl.class);

    @Autowired
    PersonDAO personDAO;
    @Autowired
    DeliveryDAO deliveryDAO;
    @Autowired
    CaseObjectDAO caseObjectDAO;
    @Autowired
    CaseCommentDAO caseCommentDAO;
    @Autowired
    CaseLinkDAO caseLinkDAO;
    @Autowired
    ProjectDAO projectDAO;
    @Autowired
    DevUnitDAO devUnitDAO;
    @Autowired
    AttachmentDAO attachmentDAO;
    @Autowired
    PortalScheduleTasks scheduledTasksService;
    @Autowired
    EventPublisherService publisherService;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Async(BACKGROUND_TASKS)
    @Override
    public void proceed(final AssembledDeliveryEvent sourceEvent) {
        log.info("proceed(): {}", sourceEvent);
        if (sourceEvent == null) {
            return;
        }

        fillDelivery(sourceEvent)
                .flatMap(this::fillInitiator)
                .flatMap(this::fillComments)
                .flatMap(this::fillAttachments)
                .ifOk(filledEvent -> publisherService.publishEvent(filledEvent));
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

    private Result<AssembledDeliveryEvent> fillDelivery(AssembledDeliveryEvent event) {
        if (event.isDeliveryFilled()) {
            return ok(event);
        }

        //проверить что заполнен менеджер проекта, инициатор "контактное лицо" и ответственный project.caseObject.manager
        Delivery delivery = deliveryDAO.get(event.getDeliveryId());
        jdbcManyRelationsHelper.fillAll(delivery);
//        delivery.setProject(projectDAO.get(delivery.getProjectId()));

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
