package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.event.AssembledProjectEvent;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.dao.CaseLinkDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.query.CaseLinkQuery;
import ru.protei.portal.core.model.struct.Project;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.Calendar;
import java.util.Date;

import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.config.MainConfiguration.BACKGROUND_TASKS;

public class AssemblerProjectServiceImpl implements AssemblerProjectService {
    @Async(BACKGROUND_TASKS)
    @Override
    public void proceed(final AssembledProjectEvent sourceEvent) {
        log.info("proceed(): {}", sourceEvent);
        if (sourceEvent == null) {
            return;
        }

        fillInitiator(sourceEvent)
                .flatMap(this::fillProject)
                .flatMap(this::fillComments)
                .flatMap(this::fillLinks)
                .ifOk(filledEvent -> publisherService.publishEvent(filledEvent));
    }

    private Result<AssembledProjectEvent> fillInitiator(AssembledProjectEvent event) {
        if (event.getInitiator() != null) {
            return ok(event);
        }

        event.setInitiator(personDAO.get(event.getInitiatorId()));

        return ok(event);
    }

    private Result<AssembledProjectEvent> fillProject(AssembledProjectEvent event) {
        CaseObject caseObject = caseObjectDAO.get(event.getProjectId());
        jdbcManyRelationsHelper.fillAll(caseObject);

        Project project = Project.fromCaseObject(caseObject);
        event.setNewProjectState(project);

        if (event.getOldProjectState() == null) {
            event.setOldProjectState(project);
        }

        return ok(event);
    }

    private Result<AssembledProjectEvent> fillLinks(AssembledProjectEvent event) {
        if (event.isLinksFilled()) {
            log.info("fillLinks(): CaseObjectID={} Links are already filled.", event.getProjectId());
            return ok(event);
        }

        log.info("fillLinks(): CaseObjectID={} Try to fill links.", event.getProjectId());
        event.setExistingLinks(caseLinkDAO.getListByQuery(new CaseLinkQuery(event.getProjectId(), false)));
        log.info("fillLinks(): CaseObjectID={} Links are successfully filled.", event.getProjectId());

        return ok(event);
    }

    private Result<AssembledProjectEvent> fillComments(AssembledProjectEvent event) {
        if (event.isCaseCommentsFilled()) {
            return ok(event);
        }

        Date upperBoundDate = addSeconds(new Date(), 1);

        event.setExistingComments(caseCommentDAO.getCaseComments(new CaseCommentQuery(event.getProjectId(), upperBoundDate)));

        return ok(event);
    }

    private Date addSeconds(Date date, int sec) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, sec);
        return calendar.getTime();
    }

    @Autowired
    PersonDAO personDAO;
    @Autowired
    CaseObjectDAO caseObjectDAO;
    @Autowired
    CaseCommentDAO caseCommentDAO;
    @Autowired
    CaseLinkDAO caseLinkDAO;

    @Autowired
    EventPublisherService publisherService;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    private static final Logger log = LoggerFactory.getLogger(AssemblerProjectServiceImpl.class);
}
