package ru.protei.portal.jira.service;

import com.atlassian.jira.rest.client.api.domain.Issue;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.dao.JiraEndpointDAO;
import ru.protei.portal.core.model.ent.JiraEndpoint;
import ru.protei.portal.core.service.EventPublisherService;
import ru.protei.portal.jira.utils.JiraHookEventData;
import ru.protei.portal.jira.utils.JiraHookEventType;
import ru.protei.winter.core.utils.Pair;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class JiraIntegrationQueueServiceImpl implements JiraIntegrationQueueService {

    @PostConstruct
    public void init() {

        handlersMap.put(JiraHookEventType.ISSUE_CREATED, (ep, event) -> integrationService.create(ep, event));
        handlersMap.put(JiraHookEventType.ISSUE_UPDATED, (ep, event) -> integrationService.updateOrCreate(ep, event));
        handlersMap.put(JiraHookEventType.COMMENT_CREATED, (ep, event) -> null);
        handlersMap.put(JiraHookEventType.COMMENT_UPDATED, (ep, event) -> null);

        defaultHandler = (ep, evt) -> {
            log.info("has no handler for event {}, skip", evt.getEventType());
            return null;
        };
    }

    @Override
    public boolean enqueue(long companyId, JiraHookEventData eventData) {

        int queueLimit = config.data().jiraConfig().getQueueLimit();
        int queueSize = queue.size();
        if (queueSize > queueLimit) {
            log.error("Event has not been enqueued, reached queue limit {}/{}, companyId={}, eventData={}",
                    queueSize, queueLimit, companyId, eventData.toFullString());
            return false;
        }

        boolean isEnqueued = queue.offer(new Pair<>(companyId, eventData));
        if (isEnqueued) {
            int threadLimit = config.data().jiraConfig().getThreadLimit();
            int threadActive = executor.getActiveCount();
            if (threadActive <= threadLimit) {
                executor.execute(this::handleQueue);
            }
        } else {
            log.error("Event has not been enqueued, companyId={}, eventData={}",
                    companyId, eventData.toFullString());
        }

        return isEnqueued;
    }

    private void handleQueue() {

        for (;;) {

            Pair<Long, JiraHookEventData> event = queue.poll();
            if (event == null) {
                break;
            }

            Long companyId = event.getA();
            JiraHookEventData eventData = event.getB();

            log.info("Event for company={} contains data={}", companyId, eventData.toDebugString());

            Issue issue = eventData.getIssue();
            JiraEndpoint endpoint = jiraEndpointDAO.getByProjectId(companyId, issue.getProject().getId());

            if (endpoint == null) {
                log.warn("Unable to find end-point record for jira-issue company={}, project={}", companyId, issue.getProject());
                continue;
            }

            JiraEventHandler handler = handlersMap.getOrDefault(eventData.getEventType(), defaultHandler);
            AssembledCaseEvent caseEvent = handler.handle(endpoint, eventData);
            if (caseEvent == null) {
                continue;
            }

            /*
             * Задержка добавлена из-за того, что в рассылке писем отсутствуют новые комменты, которые выгребаются из бд.
             * Возможно, транзакция не успевает закончиться к моменту выгреба комментов. (время по логам - 4 тысячных секунды
             * между #sendEvent и выгребом комментов в MailNotificationProcessor)
             * https://youtrack.protei.ru/issue/PORTAL-571#focus=streamItem-85-143880-0-0
             */
            log.info("Schedule send assembled event {}", caseEvent.getCaseObject().defGUID());
            try {
                Date execTime = DateUtils.addSeconds(new Date(), EVENT_SEND_DELAY_SEC);
                scheduler.schedule(() -> sendEvent(caseEvent), execTime);
            } catch (Exception e) {
                log.info("Failed to schedule send assembled event " + caseEvent.getCaseObject().defGUID() + ", sending now", e);
                sendEvent(caseEvent);
            }
        }
    }

    private void sendEvent(AssembledCaseEvent event) {
        log.debug("Send assembled event {}", event.getCaseObject().defGUID());
        eventPublisherService.publishEvent(event);
    }

    @Autowired
    PortalConfig config;
    @Autowired
    ThreadPoolTaskScheduler scheduler;
    @Autowired
    JiraEndpointDAO jiraEndpointDAO;
    @Autowired
    EventPublisherService eventPublisherService;
    @Autowired
    JiraIntegrationService integrationService;

    interface JiraEventHandler {
        AssembledCaseEvent handle(JiraEndpoint endpoint, JiraHookEventData event);
    }

    private JiraEventHandler defaultHandler;
    private Map<JiraHookEventType, JiraEventHandler> handlersMap = new HashMap<>();
    private final Queue<Pair<Long, JiraHookEventData>> queue = new ConcurrentLinkedDeque<>();
    private final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    private static final int EVENT_SEND_DELAY_SEC = 3;
    private static final Logger log = LoggerFactory.getLogger(JiraIntegrationQueueServiceImpl.class);
}
