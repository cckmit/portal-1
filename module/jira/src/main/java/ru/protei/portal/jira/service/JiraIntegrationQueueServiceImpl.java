package ru.protei.portal.jira.service;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.ent.JiraEndpoint;
import ru.protei.portal.core.service.AssemblerService;
import ru.protei.portal.jira.dict.JiraHookEventType;
import ru.protei.portal.jira.dto.JiraHookEventData;
import ru.protei.winter.core.utils.Pair;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
    public boolean enqueue(JiraEndpoint endpoint, JiraHookEventData eventData) {

        int queueLimit = config.data().jiraConfig().getQueueLimit();
        int queueSize = queue.size();
        if (queueLimit > 0) {
            if (queueSize > queueLimit) {
                log.error("Event has not been enqueued, reached queue limit {}/{}, endpoint={}, eventData={}",
                        queueSize, queueLimit, endpoint, eventData.toFullString());
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

        boolean isEnqueued = queue.offer(new Pair<>(endpoint, eventData));
        if (!isEnqueued) {
            log.error("Event has not been enqueued, endpoint={}, eventData={}",
                    endpoint, eventData.toFullString());
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
            Pair<JiraEndpoint, JiraHookEventData> event = queue.peek();
            if (event == null) {
                continue;
            }
            JiraEndpoint endpoint = event.getA();
            JiraHookEventData eventData = event.getB();
            log.error("Event for jira-issue has been dropped! endpoint={}, eventData={}", endpoint, eventData.toDebugString());
        }
        queue.clear();
    }

    private void sendEvent(AssembledCaseEvent event) {
        assemblerService.proceed(event);
        log.info("Send assembled event {}", event.getCaseObject().defGUID());
    }

    public class JiraIntegrationQueueWorker implements Runnable {
        @Override
        public void run() {
            try {
                while (!isBeanDestroyed && !Thread.currentThread().isInterrupted()) {

                    Pair<JiraEndpoint, JiraHookEventData> event = queue.poll(TIMEOUT_WAIT, TIMEOUT_TIME_UNIT);
                    if (event == null) {
                        continue;
                    }

                    JiraEndpoint endpoint = event.getA();
                    JiraHookEventData eventData = event.getB();

                    try {
                        JiraEventHandler handler = handlersMap.getOrDefault(eventData.getEventType(), defaultHandler);
                        AssembledCaseEvent caseEvent = handler.handle(endpoint, eventData).get();
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
                        log.error("Event for jira-issue has been dropped! endpoint={}, eventData={} ", endpoint, eventData.toDebugString());
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
    AssemblerService assemblerService;
    @Autowired
    JiraIntegrationService integrationService;

    private interface JiraEventHandler {
        CompletableFuture<AssembledCaseEvent> handle(JiraEndpoint endpoint, JiraHookEventData event);
    }

    private boolean isBeanDestroyed = false;
    private JiraEventHandler defaultHandler;
    private Map<JiraHookEventType, JiraEventHandler> handlersMap = new HashMap<>();

    private final BlockingQueue<Pair<JiraEndpoint, JiraHookEventData>> queue = new LinkedBlockingQueue<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private static final int QUEUE_ALARM_THRESHOLD_PERCENT = 80;
    private static final int EVENT_SEND_DELAY_SEC = 3;
    private static final long TIMEOUT_WAIT = 5L;
    private static final long TIMEOUT_TERMINATE = 8L;
    private static final TimeUnit TIMEOUT_TIME_UNIT = TimeUnit.SECONDS;
    private static final Logger log = LoggerFactory.getLogger(JiraIntegrationQueueServiceImpl.class);
}
