package ru.protei.portal.jira.controller;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueField;
import org.codehaus.jettison.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.JiraCompanyGroupDAO;
import ru.protei.portal.core.model.dao.JiraEndpointDAO;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.JiraCompanyGroup;
import ru.protei.portal.core.model.ent.JiraEndpoint;
import ru.protei.portal.core.service.AssemblerService;
import ru.protei.portal.core.utils.EntityCache;
import ru.protei.portal.jira.dict.JiraHookEventType;
import ru.protei.portal.jira.dto.JiraHookEventData;
import ru.protei.portal.jira.service.JiraIntegrationQueueService;
import ru.protei.portal.jira.service.JiraIntegrationQueueServiceImpl;
import ru.protei.portal.jira.service.JiraIntegrationService;
import ru.protei.portal.jira.utils.CommonUtils;
import ru.protei.portal.jira.utils.CustomJiraIssueParser;
import ru.protei.portal.jira.utils.JiraHookEventParser;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RestController
public class JiraEventController {

    private static final Logger logger = LoggerFactory.getLogger( JiraEventController.class);

    @Autowired
    PortalConfig portalConfig;
    @Autowired
    JiraIntegrationQueueService jiraIntegrationQueueService;
    @Autowired
    JiraCompanyGroupDAO jiraCompanyGroupDAO;
    @Autowired
    JiraEndpointDAO jiraEndpointDAO;
    @Autowired
    private CaseObjectDAO caseObjectDAO;
    @Autowired
    JiraIntegrationService integrationService;
    @Autowired
    AssemblerService assemblerService;

    private EntityCache<JiraEndpoint> jiraEndpointCache;
    private EntityCache<JiraEndpoint> jiraEndpointCache() {
        if (jiraEndpointCache == null) {
            jiraEndpointCache = new EntityCache<>(jiraEndpointDAO, TimeUnit.MINUTES.toMillis(10));
        }
        return jiraEndpointCache;
    }

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
        logger.trace("jiraWebhook(): data={}", jsonString);

        if (!portalConfig.data().integrationConfig().isJiraEnabled()) {
            logger.info("jiraWebhook(): companyId={} | jira integration is disabled, no actions taken", companyId);
            return;
        }

        long timeStart = System.currentTimeMillis();

        try {

            JiraHookEventData eventData = JiraHookEventParser.parse(jsonString);
            if (eventData == null || eventData.getIssue() == null) {
                logger.warn("jiraWebhook(): companyId={} | failed to parse json-data, return", companyId);
                if (!logger.isDebugEnabled()) logger.warn("jiraWebhook(): companyId={}, data={}", companyId, jsonString);
                return;
            }

            Issue issue = eventData.getIssue();
            JiraEndpoint endpoint = selectEndpoint(issue, companyId);

            if (endpoint == null) {
                logger.warn("Unable to find end-point record for jira-issue company={}, project={}", companyId, issue.getProject());
                logger.error("Event for jira-issue has been dropped! company={}, project={}", companyId, issue.getProject());
                return;
            }

            proceed( endpoint, eventData )
                    .thenAccept( caseEvent -> assemblerService.proceed( caseEvent ) ).
                    exceptionally( throwable -> {
                        logger.error( "jiraWebhook(): endpoint={}, src-ip={}, host={}, query={}, eventData={} | event dropped",
                                endpoint, realIP, fromHost, request.getQueryString(), eventData.toDebugString(), throwable );
                        return null;
                    } );

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
            logger.error(String.format("jiraWebhook(): companyId=%s | failed to parse json-data", companyId), e);
            if (!logger.isDebugEnabled()) logger.warn("jiraWebhook(): companyId={}, data={}", companyId, jsonString);
        } finally {
            long total = System.currentTimeMillis() - timeStart;
            logger.info("jiraWebhook(): companyId={}, src-ip={}, host={}, query={}, time={}ms | request completed",
                    companyId, realIP, fromHost, request.getQueryString(), total);
        }
    }

    private JiraEndpoint selectEndpoint(Issue issue, Long originalCompanyId) {
        JiraEndpoint endpoint = jiraEndpointCache().findFirst(ep ->
                Objects.equals(ep.getCompanyId(), originalCompanyId) &&
                Objects.equals(ep.getProjectId(), String.valueOf(issue.getProject().getId()))
        );

        if (endpoint == null) return null;

        CaseObject caseObject = caseObjectDAO.getByExternalAppCaseId(CommonUtils.makeExternalIssueID(endpoint.getId(), issue));
        if (caseObject != null) return endpoint;

        Long endpointCompanyId = selectEndpointCompanyId(issue, originalCompanyId);
        logger.info("jiraWebhook() map company id and issue field 'companygroup' in endpoint companyId: endpointCompanyId={}", endpointCompanyId);
        return jiraEndpointCache().findFirst(ep ->
                Objects.equals(ep.getCompanyId(), endpointCompanyId) &&
                        Objects.equals(ep.getProjectId(), String.valueOf(issue.getProject().getId())));
    }

    private Long selectEndpointCompanyId(Issue issue, Long originalCompanyId) {
        return Optional.ofNullable(issue.getFieldByName(CustomJiraIssueParser.COMPANY_GROUP_CODE_NAME))
                .map(IssueField::getValue)
                .map(Object::toString)
                .map(jiraCompanyGroupDAO::getByName)
                .map(JiraCompanyGroup::getCompany)
                .map(Company::getId)
                .orElse(originalCompanyId);
    }

    private CompletableFuture<AssembledCaseEvent> proceed( JiraEndpoint endpoint, JiraHookEventData eventData ) {
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
