package ru.protei.portal.core.service.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by michael on 04.05.17.
 */
public class AsyncEventPublisherService implements EventPublisherService,ApplicationEventPublisherAware {

    private static Logger logger = LoggerFactory.getLogger(AsyncEventPublisherService.class);

    ExecutorService executorService;
    ApplicationEventPublisher eventPublisher;
    int maxQueueSize=0;

    public AsyncEventPublisherService () {
        executorService = Executors.newFixedThreadPool(3, new ThreadFactory() {
            @Override
            public Thread newThread( Runnable r ) {
                Thread thread = new Thread( r );
                thread.setName( "T-" + thread.getId() + " event-publisher" );
                return thread;
            }
        });
    }

    @Override
    public void publishEvent(ApplicationEvent event) {
        final long start = System.currentTimeMillis();
        // размер очереди на момент добавления задачи в очередь
        final int size = ((ThreadPoolExecutor) executorService).getQueue().size();
        if (maxQueueSize < size) {
            maxQueueSize = size;
        }
        executorService.submit( () -> {
            if(size > 5) {
                logger.warn( "publishEvent(): Queue_size={} mqs={} timeSpentInQueue={}ms {} ", size, maxQueueSize, System.currentTimeMillis() - start, event );
            }else{
                logger.info( "publishEvent(): Queue_size={} mqs={} timeSpentInQueue={}ms {} ", size, maxQueueSize, System.currentTimeMillis() - start, event );
            }
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
