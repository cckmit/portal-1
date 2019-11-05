package ru.protei.portal.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

class BackgroundTaskThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {
    public BackgroundTaskThreadPoolTaskExecutor( int maxDbConnectionPoolSize ) {
        this.maxConnections = maxDbConnectionPoolSize;
        this.corePollSize = corePollSize;

//            int maxDbConnectionPoolSize = 50; //взять из winter.properties
        int cps = maxDbConnectionPoolSize/10+1; //не занимать все соединения на фоновые задачи
        setCorePoolSize(cps);// основное количество обрабатывающих потоков (Должен быть меньше максимально пула соединений к базе данных, например =1/8)
        setMaxPoolSize(2*cps); //при превышении очереди добавить потоки, но не более MaxPoolSize (Должен быть больше CorePoolSize, например =2*CorePoolSize)
        setQueueCapacity(2*2*cps+cps); // при превышении очереди и превышении MaxPoolSize задача отбрасывается! (Должен быть больше МaxPoolSize, например =2*МaxPoolSize+1)
        setKeepAliveSeconds(2); //удалять поток если больше CorePoolSize и не переиспользован в течении этого времени (не сразу удалять поток, а погодя )
        setAllowCoreThreadTimeOut(false); //удалять в том числе потоки из CorePoolSize согласно setKeepAliveSeconds  (экономит память снижая производительность, актуально при большом числе потоков)
        setRejectedExecutionHandler( new ThreadPoolExecutor.CallerRunsPolicy() ); //при переполнении очереди и превышении MaxPoolSize выполнять задачу на вызывающем потоке
        setThreadFactory( new ThreadFactory() {
            @Override
            public Thread newThread( Runnable r ) {
                Thread thread = new Thread( r );
                thread.setName("T-"+thread.getId()+" background-blocked-task"  );
                return thread;
            }
        } );
    }

    @Override
    public ThreadPoolExecutor getThreadPoolExecutor() throws IllegalStateException {
        ThreadPoolExecutor threadPoolExecutor = super.getThreadPoolExecutor();
        int queueSize = threadPoolExecutor.getQueue().size();
        int activeCount = getActiveCount();
        log.debug( "background-blocked-tasks-thread-pool: activeThreads={} queueSize={}", activeCount, queueSize );
        if(activeCount > corePollSize ){
            log.warn( "background-blocked-tasks-thread-pool(): Queue is overflowed! Try to increase QueueCapacity. activeThreads={} queueSize={}", activeCount, queueSize );
        }
        return threadPoolExecutor;
    }

    private int corePollSize;
    private int maxConnections;

    private static final Logger log = LoggerFactory.getLogger( BackgroundTaskThreadPoolTaskExecutor.class );
}
