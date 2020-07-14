package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
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
import ru.protei.portal.schedule.PortalScheduleTasks;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;

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
                .flatMap(this::schedulePauseTime)
                .ifOk(filledEvent -> publisherService.publishEvent(filledEvent));
    }

    @Autowired
    private TaskScheduler taskScheduler;
    private ScheduledFuture<?> schedulerFuture;
    private Result<AssembledProjectEvent> schedulePauseTime( AssembledProjectEvent event ) {
        if(event.isCreateEvent() && event.getNewProjectState()!=null && event.getNewProjectState().getPauseDate()!=null){
            schedulePauseTimeNotification(event.getProjectId(), event.getNewProjectState().getPauseDate());
            return ok(event);
        }

        if(event.isEditEvent() && event.isPauseDateChanged()){
            schedulePauseTimeNotification(event.getProjectId(), event.getNewProjectState().getPauseDate());
            return ok(event);
        }

        return ok(event);
    }

    private void schedulePauseTimeNotification(final Long projectId, final Long pauseDate ) {
        portalScheduleTasks.scheduleProjectPauseTimeNotification(projectId, pauseDate );
    }

    private Result<AssembledProjectEvent> fillInitiator(AssembledProjectEvent event) {
        if (event.getInitiator() != null) {
            return ok(event);
        }

        event.setInitiator(personDAO.get(event.getInitiatorId()));

        return ok(event);
    }

    private Result<AssembledProjectEvent> fillProject(AssembledProjectEvent event) {
        if (event.isProjectFilled()) {
            return ok(event);
        }

        CaseObject caseObject = caseObjectDAO.get(event.getProjectId());
        jdbcManyRelationsHelper.fillAll(caseObject);

        Project project = Project.fromCaseObject(caseObject);
        event.setNewProjectState(project);

        return ok(event);
    }

    private Result<AssembledProjectEvent> fillLinks(AssembledProjectEvent event) {
        if (event.isLinksFilled()) {
            log.info("fillLinks(): projectId={} Links are already filled.", event.getProjectId());
            return ok(event);
        }

        log.info("fillLinks(): projectId={} Try to fill links.", event.getProjectId());
        event.setExistingLinks(caseLinkDAO.getListByQuery(new CaseLinkQuery(event.getProjectId(), false)));
        log.info("fillLinks(): projectId={} Links are successfully filled.", event.getProjectId());

        return ok(event);
    }

    private Result<AssembledProjectEvent> fillComments(AssembledProjectEvent event) {
        if (event.isCaseCommentsFilled()) {
            log.info("fillComments(): projectId={} Comments are already filled.", event.getProjectId());
            return ok(event);
        }

        Date upperBoundDate = addSeconds(new Date(), 1);

        log.info("fillComments(): projectId={} Try to fill comments.", event.getProjectId());
        event.setExistingComments(caseCommentDAO.getCaseComments(new CaseCommentQuery(event.getProjectId(), upperBoundDate)));
        log.info("fillComments(): projectId={} Comments are successfully filled.", event.getProjectId());

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
    PortalScheduleTasks portalScheduleTasks;

    @Autowired
    EventPublisherService publisherService;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    private static final Logger log = LoggerFactory.getLogger(AssemblerProjectServiceImpl.class);
}
