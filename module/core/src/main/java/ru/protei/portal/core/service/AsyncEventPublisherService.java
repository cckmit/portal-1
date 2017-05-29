package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by michael on 04.05.17.
 */
public class AsyncEventPublisherService implements EventPublisherService,ApplicationEventPublisherAware {

    private static Logger logger = LoggerFactory.getLogger(AsyncEventPublisherService.class);

    ExecutorService executorService;
    ApplicationEventPublisher eventPublisher;

    public AsyncEventPublisherService () {
        executorService = Executors.newFixedThreadPool(3);
    }

    @Override
    public void publishEvent(ApplicationEvent event) {
        executorService.submit(() -> eventPublisher.publishEvent(event));
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }

    @PreDestroy
    public void destroy () {
        this.executorService.shutdownNow();
    }
}
