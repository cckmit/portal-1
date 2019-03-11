package ru.protei.portal.jira.service;

import com.atlassian.jira.rest.client.api.domain.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dao.JiraEndpointDAO;
import ru.protei.portal.core.model.ent.JiraEndpoint;
import ru.protei.portal.jira.utils.JiraHookEventData;
import ru.protei.portal.jira.utils.JiraHookEventType;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class JiraEventHandlerImpl {
    private static final Logger logger = LoggerFactory.getLogger(JiraEventHandlerImpl.class);

    interface JiraEventHandler {
        void handle (JiraEndpoint endpoint, JiraHookEventData event);
    }

    @Autowired
    JiraIntegrationService integrationService;

    @Autowired
    JiraEndpointDAO jiraEndpointDAO;

    @Autowired
    PortalConfig portalConfig;


    Map<JiraHookEventType, JiraEventHandler> handlersMap;
    JiraEventHandler defaultHandler;

    public JiraEventHandlerImpl() {
        logger.debug("jira webhook handler installed");
    }


    @PostConstruct
    private void init () {
        handlersMap = new HashMap<>();
        handlersMap.put(JiraHookEventType.ISSUE_CREATED, (ep, event) -> integrationService.create(ep, event));
        handlersMap.put(JiraHookEventType.ISSUE_UPDATED, (ep, event) -> integrationService.updateOrCreate(ep, event));
        handlersMap.put(JiraHookEventType.COMMENT_CREATED, (ep, event) -> logger.debug("skip comment-created event"));
        defaultHandler = (ep, evt) -> logger.debug("has no handler for event {}, skip", evt.getEventType());
    }


    @PostMapping("/jira/{companyId}/wh")
    public void onIssueEvent(@RequestBody String jsonString,
                             @PathVariable("companyId") long companyId,
                             @RequestHeader(value = "Host",required = false) String fromHost,
                             @RequestHeader(value = "X-Real-IP", required = false)String realIP,
                             HttpServletRequest request
                             ) {
        if (!portalConfig.data().integrationConfig().isJiraEnabled()) {
            logger.debug("Jira integration is disabled, nothing happens");
            return;
        }

        logger.info("got request from JIRA, companyId={}, src-ip={}, host={}, query={}", companyId, realIP, fromHost, request.getQueryString());
        logger.debug("data: {}", jsonString);

        try {
            JiraHookEventData eventData = JiraHookEventData.parse(jsonString);

            if (eventData == null || eventData.getIssue() == null) {
                logger.warn("no valid data, return");
                return;
            }

            final Issue issue = eventData.getIssue();
            final JiraEndpoint endpoint = jiraEndpointDAO.getByProjectId(companyId, issue.getProject().getId());

            if (endpoint == null) {
                logger.warn("unable to find end-point record for jira-issue project {}, company={}", issue.getProject(), companyId);
                return;
            }

            logger.debug("parsed data: {}", eventData.toDebugString());

            /**
             * Важное замечание: в каждом событии приходит довольно много информации, но зато она очень подробная и полная
             * Событие update или create всегда содержат в объекте Issue все комментарии и описание всех вложений,
             * что на самом деле довольно круто.
             * Т.е. фактически можно делать обработку только для событий created/updated для Issue,
             * при этом иметь всю информацию для синхронизации нашей модели.
             * Вложения (attachments) требуют отдельной обработки, поскольку они естественно представлены
             * в виде описания (откуда брать).
             * Еще есть идея добавить в комментарий или сам case-object (ext-app-data) структуру для
             * хранения всех ID комментариев, которые мы получали от них, а также attachment's,
             * чтобы была возможность при каждом событии проводить полную синхронизацию объекта
             * и добавлять недостающие данные.
             *
             * JIRA позволяет редактировать комментарий, но думаю, что мы пока это не будем учитывать в нашей системе.
             * Еще, кстати, получается нам нужно фильтровать наши комментарии, которые мы им сами отправляем,
             * чтобы не получать петли, когда наш комментарий опять свалится к нам и мы его еще раз добавим.
             */

            handlersMap.getOrDefault(eventData.getEventType(), defaultHandler).handle(endpoint, eventData);
        }
        catch (Exception e) {
            logger.error("unable to parse json-data", e);
        }
    }
}
