package ru.protei.portal.jira.controller;

import org.codehaus.jettison.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.jira.service.JiraIntegrationQueueService;
import ru.protei.portal.jira.utils.JiraHookEventData;

import javax.servlet.http.HttpServletRequest;

@RestController
public class JiraEventHandlerImpl {

    private static final Logger logger = LoggerFactory.getLogger(JiraEventHandlerImpl.class);

    @Autowired
    PortalConfig portalConfig;
    @Autowired
    JiraIntegrationQueueService jiraIntegrationQueueService;

    public JiraEventHandlerImpl() {
        logger.info("Jira webhook handler installed");
    }

    @PostMapping("/jira/{companyId}/wh")
    public void onIssueEvent(
            @RequestBody String jsonString,
            @PathVariable("companyId") long companyId,
            @RequestHeader(value = "Host", required = false) String fromHost,
            @RequestHeader(value = "X-Real-IP", required = false) String realIP,
            HttpServletRequest request
    ) {

        logger.info("Got request from JIRA, companyId={}, src-ip={}, host={}, query={}", companyId, realIP, fromHost, request.getQueryString());
        logger.debug("Got request from JIRA, data: {}", jsonString);

        if (!portalConfig.data().integrationConfig().isJiraEnabled()) {
            logger.info("Jira integration is disabled, no actions taken");
            return;
        }

        long timeStart = System.currentTimeMillis();

        try {

            JiraHookEventData eventData = JiraHookEventData.parse(jsonString);
            if (eventData == null || eventData.getIssue() == null) {
                logger.warn("Failed to parse data, return");
                if (!logger.isDebugEnabled()) {
                    logger.warn("Data: {}", jsonString);
                }
                return;
            }

            if (!jiraIntegrationQueueService.enqueue(companyId, eventData)) {
                logger.error("Event dropped: companyId={}, src-ip={}, host={}, query={}, eventData={}",
                        companyId, realIP, fromHost, request.getQueryString(), eventData.toDebugString());
            }

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

        } catch (JSONException e) {
            logger.error("Failed to parse json-data", e);
            if (!logger.isDebugEnabled()) {
                logger.error("Data: {}", jsonString);
            }
        } finally {
            long total = System.currentTimeMillis() - timeStart;
            logger.info("Request from JIRA completed, companyId={}, src-ip={}, host={}, query={}, time={}ms", companyId, realIP, fromHost, request.getQueryString(), total);
        }
    }
}
