package ru.protei.portal.jira.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Последовательная обработка поступающих сообщений от jira.
 */
public class JiraQueueSingleThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {
    /**
     * Последовательная обработка поступающих сообщений от jira.
     * С целью сохранения порядка сообщений обработка ведется в один поток.
     * @param queueLimit - максимальное количество обрабатываемых событий от Jira. Не ограничено если = 0.
     *                   При queueLimit > 0 и превышение очереди, события отбрасываются.
     */
    public JiraQueueSingleThreadPoolTaskExecutor( int queueLimit) {

        setCorePoolSize(ONE_THREAD);// основное количество обрабатывающих потоков (Должен быть меньше максимально пула соединений к базе данных, например =1/8)
        setMaxPoolSize(ONE_THREAD);//при превышении очереди добавить потоки, но не более MaxPoolSize (Должен быть больше CorePoolSize, например =2*CorePoolSize)
        if(queueLimit != 0) {
            setQueueCapacity(queueLimit); // при превышении очереди и превышении MaxPoolSize задача отбрасывается или определяется политикой RejectedExecutionHandler! (Должен быть больше МaxPoolSize, например =2*МaxPoolSize+1)
            this.queueLimit = queueLimit;
        }
        setKeepAliveSeconds(0); //удалять поток если больше CorePoolSize и не переиспользован в течении этого времени (не сразу удалять поток, а погодя )
        setAllowCoreThreadTimeOut(false); //удалять в том числе потоки из CorePoolSize согласно setKeepAliveSeconds  (экономит память снижая производительность, актуально при большом(десятки-сотни) числе потоков)
        setRejectedExecutionHandler( new ThreadPoolExecutor.DiscardPolicy() ); //DiscardPolicy при переполнении очереди и превышении MaxPoolSize отбрасывать задачу!
        setThreadFactory( new ThreadFactory() {
            @Override
            public Thread newThread( Runnable r ) {
                Thread thread = new Thread( r );
                thread.setName("T-"+thread.getId()+" jira-queue"  );
                return thread;
            }
        } );
    }

    @Override
    public ThreadPoolExecutor getThreadPoolExecutor() throws IllegalStateException {
        ThreadPoolExecutor threadPoolExecutor = super.getThreadPoolExecutor();
        int queueSize = threadPoolExecutor.getQueue().size();

        log.debug( "jira-queue: queueSize={}", queueSize );
        int queueAlarmThreshold = (queueSize / queueLimit) * 100;
        if (queueAlarmThreshold > QUEUE_ALARM_THRESHOLD_PERCENT) {
            log.warn( "jira-queue: Queue threshold alarm! Reached  queueLimit={} queueSize={}", queueLimit, queueSize );
        }
        return threadPoolExecutor;
    }


    private int queueLimit = Integer.MAX_VALUE;
    private static final int ONE_THREAD = 1;
    private static final int QUEUE_ALARM_THRESHOLD_PERCENT = 80;

    private static final Logger log = LoggerFactory.getLogger( JiraQueueSingleThreadPoolTaskExecutor.class );
}
