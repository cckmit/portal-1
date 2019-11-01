package ru.protei.portal.core.service.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import protei.utils.common.Tuple;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.*;
import ru.protei.portal.core.model.dict.En_ExtAppType;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.service.AsseblerService;
//import ru.protei.portal.core.utils.EventExpirationControl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.lang.System.currentTimeMillis;

public class EventAssemblerServiceImpl implements EventAssemblerService {

    @Override
    @EventListener
    public void publishEvent(CaseObjectEvent event) {
        log.info( "publishEvent(): CaseObjectEvent id={} {}", event.getCaseObjectId(), event.getPerson() );
        AssembledCaseEvent assembledPrevEvent = getAssembledCaseEvent( event );
        log.info( "publishEvent(): Assembled id={} {} {}", assembledPrevEvent.getCaseObjectId(),  assembledPrevEvent, assembledPrevEvent.getInitiator() );
        assembledPrevEvent.attachEvent( event );
//        handleEventForAssemble(event, assembledPrevEvent);
    }

    @Override
    @EventListener
    public void publishEvent(CaseCommentEvent event) {
        log.info( "publishEvent(): CaseCommentEvent id={} {}", event.getCaseObjectId(), event.getPerson() );
        AssembledCaseEvent assembledPrevEvent = getAssembledCaseEvent( event );
        log.info( "publishEvent(): Assembled id={} {} {}", assembledPrevEvent.getCaseObjectId(),  assembledPrevEvent, assembledPrevEvent.getInitiator() );
        assembledPrevEvent.attachEvent( event );
//        handleEventForAssemble(event, assembledPrevEvent);
    }

    @Override
    @EventListener
    public void publishEvent(CaseObjectCommentEvent event) {
        log.info( "publishEvent(): CaseObjectCommentEvent id={} {}", event.getCaseObjectId(), event.getPerson() );
        AssembledCaseEvent assembledPrevEvent = getAssembledCaseEvent( event );
        log.info( "publishEvent(): Assembled id={} {} {}", assembledPrevEvent.getCaseObjectId(), assembledPrevEvent, assembledPrevEvent.getInitiator() );
        assembledPrevEvent.attachEvent( event );
//        handleEventForAssemble(event, assembledPrevEvent);
    }

    private AssembledCaseEvent getAssembledCaseEvent( AbstractCaseEvent event ) {
        Tuple<Person, Long> key = makeEventKey(event);
        log.info( "publishEvent getAssembledCaseEvent(): {}",  key);
        return assembledEventsMap.computeIfAbsent(key, k->new AssembledCaseEvent( event ));//concurrency
    }

    private void handleEventForAssemble(AbstractCaseEvent event, AssembledCaseEvent assembledPrevEvent) {
//    private void handleEventForAssemble(AbstractCaseEvent event) {

//        if (isEagerPush(assembledPrevEvent)) {
//            logger.info("Eager push on event for case {}", event.getCaseObjectId());
//            assemblerService.proceed(assembledEventsMap.remove( makeEventKey(event) ));
//            return;
//        }

//        Tuple<Person, Long> key = makeEventKey(event);

//        if (!assembledEventsMap.containsKey(key)) {
//            logger.info("Put event for case {} to map, no previous event found", event.getCaseObject().defGUID());
//            assembledEventsMap.put(key, assembledEvent);
//            return;
//        }

//        AssembledCaseEvent assembledPrevEvent = assembledEventsMap.computeIfAbsent(key, k->new AssembledCaseEvent( event ));


//        if (!assembledPrevEvent.isLastStateSet() && !assembledPrevEvent.isCommentAttached()) {
//            logger.info("Attach new event to previous event for case {}", event.getCaseObject().defGUID());
//            if (event.getCaseObject() != null) {
//                assembledPrevEvent.attachCaseObject(event.getCaseObject());
//            }
//            if (event.getCaseComment() != null) {
//                assembledPrevEvent.attachCaseComment(event.getCaseComment());
//            }
//            assembledPrevEvent.synchronizeAttachments(
//                    event.getAddedAttachments(),
//                    event.getRemovedAttachments()
//            );
//            return;
//        }

//        logger.info("Put event for case {} to map, push previous event", event.getCaseObjectId());
//        publishAndClear(key);
//        assembledEventsMap.put(key, assembledEvent);
    }

    @Override
    @EventListener
    public void publishEvent(CaseAttachmentEvent event) {

//        if (isEagerPush(event)) {
        if (event.isEagerEvent()) {
            log.info("Eager push on event for case {}", event.getCaseObject().defGUID());
            assemblerService.proceed(new AssembledCaseEvent(event));
            return;
        }

        Tuple<Person, Long> key = makeEventKey(event);

        if (!assembledEventsMap.containsKey(key)) {
            log.info("Put event for case {} to map, no previous event found", event.getCaseObject().defGUID());
            assembledEventsMap.put(key, new AssembledCaseEvent(event));
            return;
        }

        log.info("Attach new event to previous event for case {}", event.getCaseObject().defGUID());
        AssembledCaseEvent assembledPrevEvent = assembledEventsMap.get(key);
        assembledPrevEvent.synchronizeAttachments(
                event.getAddedAttachments(),
                event.getRemovedAttachments()
        );
    }

    @Override
    public AssembledCaseEvent getEvent(Person person, Long caseId) {
        Tuple<Person, Long> key = new Tuple<>(person, caseId);
        return assembledEventsMap.getOrDefault(key, null);
    }


    @Override
    public int getEventsCount() {
        return assembledEventsMap.size();
    }

    @Scheduled(fixedRate = SCHEDULE_TIME)
    public void checkEventsMap() {
        //Measured in ms
//        logger.debug("event assembly, checkEventsMap, size={}", assembledEventsMap.size());
        Collection<Tuple<Person, Long>> events = assembledEventsMap.values().stream()
                .filter(x -> isExpired(x))
                .map(x -> new Tuple<>(x.getInitiator(), x.getCaseObjectId()))
                .distinct()
                .collect(Collectors.toList());

        if (!events.isEmpty()) {
            log.debug("publish set of events, initiators : {}", events.size());
//            events.forEach(this::publishAndClear);
        }
    }

    private void publishAndClear(Tuple<Person, Long> key) {
        AssembledCaseEvent personsEvent = assembledEventsMap.remove(key);
        log.info("publishAndClear event, case:{}, person:{}", personsEvent.getCaseObjectId(),
                personsEvent.getInitiator().getDisplayName());
        assemblerService.proceed(personsEvent);
//        assembledEventsMap.remove(key);
    }

//    private boolean isEagerPush(AbstractCaseEvent event) {
//        return EAGER_PUSH.contains(event.getCaseObject().getExtAppType());
//    }

    private Tuple<Person, Long> makeEventKey(AbstractCaseEvent event) {
        return new Tuple<>(event.getPerson(), event.getCaseObjectId());
    }

    private static final long SEC = 1000;
    private final Map<Tuple<Person, Long>, AssembledCaseEvent> assembledEventsMap = new ConcurrentHashMap<>();
    //Time interval for events checking, MS
    private final static long SCHEDULE_TIME = 1 * SEC;
    //app types for eager push
    private final static Set<String> EAGER_PUSH = new HashSet<String>() {{//TODO add flag into events
        add("junit-test");
        add(En_ExtAppType.REDMINE.getCode());
    }};
    private static Logger log = LoggerFactory.getLogger(EventAssemblerServiceImpl.class);

    public boolean isExpired(AssembledCaseEvent event) {
        if (event.isEagerEvent()) {
            return (currentTimeMillis() - event.getLastUpdated()) >= 2 * SEC;
        }
        long lastUpdated = event.getLastUpdated();
        long l = currentTimeMillis() - lastUpdated;
        long waitingPeriodMillis = config.data().eventAssemblyConfig().getWaitingPeriodMillis();
        return l  >= waitingPeriodMillis;
    }

    @Autowired
    private PortalConfig config;
    @Autowired
    AsseblerService assemblerService;

//    @Autowired
//    EventExpirationControl eventExpirationControl;
}
