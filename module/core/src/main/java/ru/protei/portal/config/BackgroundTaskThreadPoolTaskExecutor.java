package ru.protei.portal.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Выполнение фоновых задач связанных с доступом к базе данных.
 */
class BackgroundTaskThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {
    /**
     * Величина определяющая максимальное количество соединений с бд
     * используется для расчета количества потоков фоновых задач.
     * @param maxDbConnectionPoolSize - взять из winter.properties
     */
    public BackgroundTaskThreadPoolTaskExecutor( int maxDbConnectionPoolSize ) {
        int corePollSize = 1 + maxDbConnectionPoolSize/10; //занимать только часть соединений к базе, не меннее одного;

        setCorePoolSize(corePollSize);// основное количество обрабатывающих потоков (Должен быть меньше максимально пула соединений к базе данных, например =1/8)
        int maxPoolSize = 2 * corePollSize;
        setMaxPoolSize( maxPoolSize ); //при превышении очереди добавить потоки, но не более MaxPoolSize (Должен быть больше CorePoolSize, например =2*CorePoolSize)
        this.queueCapacity =  2 * maxPoolSize + corePollSize;
        setQueueCapacity(queueCapacity); // при превышении очереди и превышении MaxPoolSize задача отбрасывается или определяется политикой RejectedExecutionHandler!
        setKeepAliveSeconds(2); //удалять поток если больше CorePoolSize и не переиспользован в течении этого времени (не сразу удалять поток, а погодя )
        setAllowCoreThreadTimeOut(false); //удалять в том числе потоки из CorePoolSize согласно setKeepAliveSeconds  (экономит память снижая производительность, актуально при большом(десятки-сотни) числе потоков)
        setRejectedExecutionHandler( new ThreadPoolExecutor.CallerRunsPolicy() ); //при переполнении очереди и превышении MaxPoolSize выполнять задачу на вызывающем потоке
        setThreadFactory( new ThreadFactory() {
            @Override
            public Thread newThread( Runnable r ) {
                Thread thread = new Thread( r );
                thread.setName("T-"+thread.getId()+" background-task"  );
                return thread;
            }
        } );
    }

    @Override
    public ThreadPoolExecutor getThreadPoolExecutor() throws IllegalStateException {
        ThreadPoolExecutor threadPoolExecutor = super.getThreadPoolExecutor();
        int queueSize = threadPoolExecutor.getQueue().size();
        int activeCount = getActiveCount();
        log.debug( "background-tasks-thread-pool: activeThreads={} queueSize={}", activeCount, queueSize );
        if(queueSize >= queueCapacity - 1 ){
            log.warn( "background-tasks-thread-pool(): Queue is overflowed! Try to increase QueueCapacity. activeThreads={} queueSize={} queueCapacity={}", activeCount, queueSize, queueCapacity );
        }
        return threadPoolExecutor;
    }

    @EventListener
    public void onApplicationStop( ContextClosedEvent event){
        log.info( "onApplicationStop(): Stop background tasks." );
//        shutdown();
    }

    private int queueCapacity;

    private static final Logger log = LoggerFactory.getLogger( BackgroundTaskThreadPoolTaskExecutor.class );
}
