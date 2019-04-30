package ru.protei.portal.core.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Aspect
@Order(0)
public class DAOConnectionTracker {

//    @Pointcut("within(ru.protei.winter.jdbc.JdbcWinterDataSource..*)")
//    private void inJdbcWinterDataSource() {}

    @Pointcut("within(ru.protei.portal.core.model.dao..*)")
    private void inDAOLayer() {}

    @Around("inDAOLayer()")
    public Object doTrack(ProceedingJoinPoint pjp) throws Throwable {
        String methodName = pjp.getSignature().toShortString();
        boolean skip = methodName.toLowerCase().contains("sqlcondition") ||
                methodName.toLowerCase().contains("sqlbuilder");
        if (skip) {
            return pjp.proceed();
        }
        Timer timer = new Timer("Timer");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                log.error("DAO: {} = execution took more than 3000ms", methodName);
            }
        }, 3000L);
        String token;
        int pendingSize;
        synchronized (lock) {
            token = methodName + "-" + (++id);
            pendingSize = pending.size();
            pending.add(token);
        }
        long currentTimeMillis = System.currentTimeMillis();
        Object result;
        try {
            result = pjp.proceed();
        } finally {
            long executionTime = System.currentTimeMillis() - currentTimeMillis;
            synchronized (lock) {
                pending.remove(token);
            }
            timer.cancel();
            if (executionTime < 100L) {
                log.info("p = {} | t = {}ms | {}", pendingSize, executionTime, methodName);
            } else {
                log.warn("p = {} | t = {}ms | {}", pendingSize, executionTime, methodName);
            }
        }
        return result;
    }

    private static Logger log = LoggerFactory.getLogger(DAOConnectionTracker.class);
    private long id = 0L;
    private List<String> pending = new ArrayList<>();
    private final Object lock = new Object();
}
