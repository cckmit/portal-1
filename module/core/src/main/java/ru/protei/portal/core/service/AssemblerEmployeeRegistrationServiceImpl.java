package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.event.AssembledEmployeeRegistrationEvent;
import ru.protei.portal.core.model.dao.AttachmentDAO;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.dao.EmployeeRegistrationDAO;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.EmployeeRegistration;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.service.events.EventPublisherService;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.config.MainConfiguration.BACKGROUND_TASKS;

public class AssemblerEmployeeRegistrationServiceImpl implements AssemblerEmployeeRegistrationService {
    private static final Logger log = LoggerFactory.getLogger(AssemblerEmployeeRegistrationServiceImpl.class);

    @Autowired
    CaseCommentDAO caseCommentDAO;
    @Autowired
    EmployeeRegistrationDAO employeeRegistrationDAO;
    @Autowired
    EventPublisherService publisherService;
    @Autowired
    AttachmentDAO attachmentDAO;

    @Async(BACKGROUND_TASKS)
    @Override
    public void proceed(final AssembledEmployeeRegistrationEvent sourceEvent) {
        log.info("proceed(): {}", sourceEvent);
        if (sourceEvent == null) {
            return;
        }

        fillEmployeeRegistration(sourceEvent)
                .flatMap(this::fillComments)
                .flatMap(this::fillAttachments)
                .ifOk(filledEvent -> publisherService.publishEvent(filledEvent));
    }

    private Result<AssembledEmployeeRegistrationEvent> fillEmployeeRegistration(AssembledEmployeeRegistrationEvent event) {
        if (event.isEmployeeRegistrationFilled()) {
            return ok(event);
        }

        EmployeeRegistration employeeRegistration = employeeRegistrationDAO.get(event.getEmployeeRegistrationId());
        event.setNewEmployeeRegistrationState(employeeRegistration);

        return ok(event);
    }

    private Result<AssembledEmployeeRegistrationEvent> fillComments(AssembledEmployeeRegistrationEvent event) {
        if (event.isCaseCommentsFilled()) {
            log.info("fillComments(): employeeRegistrationId={} Comments are already filled.", event.getEmployeeRegistrationId());
            return ok(event);
        }

        Date upperBoundDate = addSeconds(new Date(), 1);

        log.info("fillComments(): employeeRegistrationId={} Try to fill comments.", event.getEmployeeRegistrationId());
        List<CaseComment> caseComments = caseCommentDAO.getCaseComments(new CaseCommentQuery(event.getEmployeeRegistrationId(), upperBoundDate));

        event.setExistingComments(caseComments);
        log.info("fillComments(): employeeRegistrationId={} Comments are successfully filled.", event.getEmployeeRegistrationId());

        return ok(event);
    }

    private Result<AssembledEmployeeRegistrationEvent> fillAttachments(AssembledEmployeeRegistrationEvent event) {
        if (event.isAttachmentsFilled()) {
            log.info("fillAttachments(): employeeRegistrationId={} Attachments are already filled.", event.getEmployeeRegistrationId());
            return ok(event);
        }
        log.info("fillAttachments(): CaseObjectID={} Try to fill attachments.", event.getEmployeeRegistrationId());
        event.setExistingAttachments(attachmentDAO.getAttachmentsByCaseId(event.getEmployeeRegistrationId()));
        log.info("fillAttachments(): CaseObjectID={} Attachments are successfully filled.", event.getEmployeeRegistrationId());

        return ok(event);
    }

    private Date addSeconds(Date date, int sec) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, sec);
        return calendar.getTime();
    }
}
