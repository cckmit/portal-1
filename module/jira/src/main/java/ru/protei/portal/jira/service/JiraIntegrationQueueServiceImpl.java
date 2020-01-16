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
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.portal.core.utils.EntityCache;
import ru.protei.portal.jira.dto.JiraHookEventData;
import ru.protei.portal.jira.dict.JiraHookEventType;
import ru.protei.winter.core.utils.Pair;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.*;

public class JiraIntegrationQueueServiceImpl implements JiraIntegrationQueueService {

    @PostConstruct
    public void init() {

        if (!config.data().integrationConfig().isJiraEnabled()) {
            log.info("Jira integration is disabled, queue service not started");
            return;
        }

        initHandlers();

        if (startWorker()) {
            log.info("Jira queue service has started");
        } else {
            log.error("Jira queue service failed to start");
        }
    }

    @PreDestroy
    private void destroy() {
        log.info("Jira queue service is about to be stopped");
        isBeanDestroyed = true;
        stopWorker();
        stopQueue();
        log.info("Jira queue service has stopped");
    }

    @Override
    public boolean enqueue(long companyId, JiraHookEventData eventData) {

        int queueLimit = config.data().jiraConfig().getQueueLimit();
        int queueSize = queue.size();
        if (queueLimit > 0) {
            if (queueSize > queueLimit) {
                log.error("Event has not been enqueued, reached queue limit {}/{}, companyId={}, eventData={}",
                        queueSize, queueLimit, companyId, eventData.toFullString());
                return false;
            }

            int queueAlarmThreshold = (queueLimit / 100) * QUEUE_ALARM_THRESHOLD_PERCENT;
            if (queueSize > queueAlarmThreshold) {
                log.warn("Queue threshold alarm! Reached {}% of queue ({}/{})",
                        (queueSize / queueLimit) * 100, queueSize, queueLimit);
            }
        } else {
            log.debug("Queue size is {}", queueSize);
        }

        boolean isEnqueued = queue.offer(new Pair<>(companyId, eventData));
        if (!isEnqueued) {
            log.error("Event has not been enqueued, companyId={}, eventData={}",
                    companyId, eventData.toFullString());
        }

        return isEnqueued;
    }

    private void initHandlers() {
        handlersMap.put(JiraHookEventType.ISSUE_CREATED, (ep, event) -> integrationService.create(ep, event));
        handlersMap.put(JiraHookEventType.ISSUE_UPDATED, (ep, event) -> integrationService.updateOrCreate(ep, event));
        handlersMap.put(JiraHookEventType.COMMENT_CREATED, (ep, event) -> null);
        handlersMap.put(JiraHookEventType.COMMENT_UPDATED, (ep, event) -> null);
        defaultHandler = (ep, evt) -> {
            log.info("No handler for event {}, skip", evt.getEventType());
            return null;
        };
    }

    private boolean startWorker() {
        try {
            executor.submit(new JiraIntegrationQueueWorker());
            return true;
        } catch (RejectedExecutionException e) {
            log.error("Executor rejected to submit worker", e);
            return false;
        }
    }

    private void stopWorker() {
        try {
            if (!executor.awaitTermination(TIMEOUT_TERMINATE, TIMEOUT_TIME_UNIT)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }

    private void stopQueue() {
        if (queue.isEmpty()) {
            return;
        }
        for (int i = 0; i < queue.size(); i++) {
            Pair<Long, JiraHookEventData> event = queue.peek();
            if (event == null) {
                continue;
            }
            Long companyId = event.getA();
            JiraHookEventData eventData = event.getB();
            log.error("Event for jira-issue has been dropped! company={}, eventData={}", companyId, eventData.toDebugString());
        }
        queue.clear();
    }

    private void sendEvent(AssembledCaseEvent event) {
        log.info("Send assembled event {}", event.getCaseObject().defGUID());
        eventPublisherService.publishEvent(event);
    }

    private EntityCache<JiraEndpoint> jiraEndpointCache() {
        if (jiraEndpointCache == null) {
            jiraEndpointCache = new EntityCache<>(jiraEndpointDAO, TimeUnit.MINUTES.toMillis(10));
        }
        return jiraEndpointCache;
    }

    public class JiraIntegrationQueueWorker implements Runnable {
        @Override
        public void run() {
            try {
                while (!isBeanDestroyed && !Thread.currentThread().isInterrupted()) {

                    Pair<Long, JiraHookEventData> event = queue.poll(TIMEOUT_WAIT, TIMEOUT_TIME_UNIT);
                    if (event == null) {
                        continue;
                    }

                    Long companyId = event.getA();
                    JiraHookEventData eventData = event.getB();

                    try {

                        log.info("Event for company={} contains data={}", companyId, eventData.toDebugString());

                        Issue issue = eventData.getIssue();
                        JiraEndpoint endpoint = jiraEndpointCache().findFirst(ep ->
                                Objects.equals(ep.getCompanyId(), companyId) &&
                                Objects.equals(ep.getProjectId(), String.valueOf(issue.getProject().getId()))
                        );

                        if (endpoint == null) {
                            log.warn("Unable to find end-point record for jira-issue company={}, project={}", companyId, issue.getProject());
                            log.error("Event for jira-issue has been dropped! company={}, project={}", companyId, issue.getProject());
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

                    } catch (Exception e) {
                        log.error("Exception occurred while handling event", e);
                        log.error("Event for jira-issue has been dropped! company={}, eventData={} ", companyId, eventData.toDebugString());
                    }
                }
                log.info("Worker execution is requested to exit, {}", isBeanDestroyed ? "bean destroyed" : "thread interrupted");
            } catch (InterruptedException e) {
                log.error("InterruptedException occurred at worker execution, exit now", e);
                startWorker();
            } catch (Exception e) {
                log.error("Exception occurred at worker execution, exit and restart now", e);
                startWorker();
            }
        }

        private final Logger log = LoggerFactory.getLogger(JiraIntegrationQueueWorker.class);
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

    private interface JiraEventHandler {
        AssembledCaseEvent handle(JiraEndpoint endpoint, JiraHookEventData event);
    }

    private boolean isBeanDestroyed = false;
    private EntityCache<JiraEndpoint> jiraEndpointCache;
    private JiraEventHandler defaultHandler;
    private Map<JiraHookEventType, JiraEventHandler> handlersMap = new HashMap<>();

    private final BlockingQueue<Pair<Long, JiraHookEventData>> queue = new LinkedBlockingQueue<>();
    private final ExecutorService executor = (ThreadPoolExecutor) Executors.newSingleThreadExecutor();

    private static final int QUEUE_ALARM_THRESHOLD_PERCENT = 80;
    private static final int EVENT_SEND_DELAY_SEC = 3;
    private static final long TIMEOUT_WAIT = 5L;
    private static final long TIMEOUT_TERMINATE = 8L;
    private static final TimeUnit TIMEOUT_TIME_UNIT = TimeUnit.SECONDS;
    private static final Logger log = LoggerFactory.getLogger(JiraIntegrationQueueServiceImpl.class);
}
