package ru.protei.portal.core.service.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import ru.protei.portal.core.service.EventPublisherService;

import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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
        final long start = System.currentTimeMillis();
        // размер очереди на момент добавления задачи в очередь
        final int size = ((ThreadPoolExecutor) executorService).getQueue().size();
        executorService.submit( () -> {
            logger.info( "publishEvent(): Queue_size={} timeSpentInQueue={}ms {} ", size, System.currentTimeMillis() - start, event );
            eventPublisher.publishEvent( event );
        } );
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
