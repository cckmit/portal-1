package ru.protei.portal.jira.controller;

import com.atlassian.jira.rest.client.api.domain.Issue;
import org.codehaus.jettison.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.JiraEndpoint;
import ru.protei.portal.core.service.AssemblerService;
import ru.protei.portal.jira.dto.JiraHookEventData;
import ru.protei.portal.jira.service.JiraIntegrationService;
import ru.protei.portal.jira.utils.JiraHookEventParser;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

@RestController
public class JiraEventController {

    private static final Logger logger = LoggerFactory.getLogger( JiraEventController.class);

    @Autowired
    PortalConfig portalConfig;
    @Autowired
    JiraIntegrationService integrationService;
    @Autowired
    AssemblerService assemblerService;

    public JiraEventController() {
        logger.info("Jira webhook controller (handler) installed");
    }

    @PostMapping("/jira/{companyId}/wh")
    public void onIssueEvent(
            @RequestBody String jsonString,
            @PathVariable("companyId") long companyId,
            @RequestHeader(value = "Host", required = false) String fromHost,
            @RequestHeader(value = "X-Real-IP", required = false) String realIP,
            HttpServletRequest request
    ) {

        logger.info("jiraWebhook(): companyId={}, src-ip={}, host={}, query={}", companyId, realIP, fromHost, request.getQueryString());
        logger.trace("jiraWebhook(): companyId={}, data={}", companyId, jsonString);

        if (!portalConfig.data().integrationConfig().isJiraEnabled()) {
            logger.info("jiraWebhook(): companyId={} | jira integration is disabled, no actions taken", companyId);
            return;
        }

        Result<JiraHookEventData> parseResult = parseJson( jsonString );

        if(parseResult.isError()){
            logger.error("jiraWebhook(): Can't process jira event: {}", parseResult);
            return;
        }

        JiraHookEventData eventData = parseResult.getData();
        if (eventData == null) {
            logger.warn( "jiraWebhook(): companyId={} | failed to parse json-data, return", companyId );
            return;
        }

        if (eventData.getIssue() == null) {
            logger.info( "jiraWebhook(): companyId={} | event has no issue field, return", companyId );
            return;
        }

        Issue issue = eventData.getIssue();
        Result<JiraEndpoint> endpoint = integrationService.selectEndpoint( issue, companyId );

        if (endpoint.isError()) {
            logger.warn( "Unable to find end-point record for jira-issue company={}, project={}", companyId, issue.getProject() );
            logger.error( "Event for jira-issue has been dropped! company={}, project={}", companyId, issue.getProject() );
            return;
        }

        Optional.ofNullable(processEvent(endpoint.getData(), eventData))
                .ifPresent(future -> future.thenAccept(assembledCaseEvent -> assemblerService.proceed(assembledCaseEvent))
                            .exceptionally(throwable -> {
                                logger.error("jiraWebhook(): endpoint={}, src-ip={}, host={}, query={}, eventData={} | event dropped",
                                        endpoint, realIP, fromHost, request.getQueryString(), eventData.toDebugString(), throwable);
                                return null;
                            }));

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

    }

    private Result<JiraHookEventData> parseJson( String jsonString )  {
        long timeStart = System.currentTimeMillis();

        try {
           return ok(JiraHookEventParser.parse(jsonString));
        } catch (JSONException e) {
            logger.warn( "parseJson(): ", e );
            return error( En_ResultStatus.INTERNAL_ERROR, "JSONException: " + e.getMessage() );
        } finally {
            long total = System.currentTimeMillis() - timeStart;
            logger.info("parseJson(): parse time={}", total);
        }
    }

    private CompletableFuture<AssembledCaseEvent> processEvent(JiraEndpoint endpoint, JiraHookEventData eventData ) {
        switch (eventData.getEventType()) {
            case ISSUE_CREATED:
                return integrationService.create( endpoint, eventData );
            case ISSUE_UPDATED:
                return integrationService.updateOrCreate( endpoint, eventData );
            case COMMENT_UPDATED:
            case COMMENT_CREATED:
            case ISSUE_LINK_CREATED:
            case ISSUE_LINK_DELETED:
            default:
                logger.info( "No handler for event {}, skip", eventData.getEventType() );
                return null;
        }
    }
}
