package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import protei.utils.common.Tuple;
import ru.protei.portal.core.event.*;
import ru.protei.portal.core.model.ent.Person;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EventAssemblerServiceImpl implements EventAssemblerService {

    @Override
    @EventListener
    public void publishEvent(CaseObjectEvent event) {
        handleEventForAssemble(event, new AssembledCaseEvent(event));
    }

    @Override
    @EventListener
    public void publishEvent(CaseCommentEvent event) {
        handleEventForAssemble(event, new AssembledCaseEvent(event));
    }

    @Override
    @EventListener
    public void publishEvent(CaseObjectCommentEvent event) {
        handleEventForAssemble(event, new AssembledCaseEvent(event));
    }

    private void handleEventForAssemble(AbstractCaseEvent event, AssembledCaseEvent assembledEvent) {

        if (isEagerPush(event)) {
            logger.info("Eager push on event for case {}", event.getCaseObject().defGUID());
            publisherService.publishEvent(assembledEvent);
            return;
        }

        Tuple<Person, Long> key = makeEventKey(event);

        if (!assembledEventsMap.containsKey(key)) {
            logger.info("Put event for case {} to map, no previous event found", event.getCaseObject().defGUID());
            assembledEventsMap.put(key, assembledEvent);
            return;
        }

        AssembledCaseEvent assembledPrevEvent = assembledEventsMap.get(key);

        if (!assembledPrevEvent.isLastStateSet() && !assembledPrevEvent.isCommentAttached()) {
            logger.info("Attach new event to previous event for case {}", event.getCaseObject().defGUID());
            if (event.getCaseObject() != null) {
                assembledPrevEvent.attachCaseObject(event.getCaseObject());
            }
            if (event.getCaseComment() != null) {
                assembledPrevEvent.attachCaseComment(event.getCaseComment());
            }
            assembledPrevEvent.synchronizeAttachments(
                    event.getAddedAttachments(),
                    event.getRemovedAttachments()
            );
            return;
        }

        logger.info("Put event for case {} to map, push previous event", event.getCaseObject().defGUID());
        publishAndClear(key);
        assembledEventsMap.put(key, assembledEvent);
    }

    @Override
    @EventListener
    public void publishEvent(CaseAttachmentEvent event) {

        if (isEagerPush(event)) {
            logger.info("Eager push on event for case {}", event.getCaseObject().defGUID());
            publisherService.publishEvent(new AssembledCaseEvent(event));
            return;
        }

        Tuple<Person, Long> key = makeEventKey(event);

        if (!assembledEventsMap.containsKey(key)) {
            logger.info("Put event for case {} to map, no previous event found", event.getCaseObject().defGUID());
            assembledEventsMap.put(key, new AssembledCaseEvent(event));
            return;
        }

        logger.info("Attach new event to previous event for case {}", event.getCaseObject().defGUID());
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
    public void forcePublishCaseRelatedEvents(Long caseId) {
        assembledEventsMap.keySet().stream().filter(x -> x.b.equals(caseId)).forEach(this::publishAndClear);
    }

    @Override
    public int getEventsCount() {
        return assembledEventsMap.size();
    }

    @Scheduled(fixedRate = SCHEDULE_TIME)
    public void checkEventsMap() {
        //Measured in ms
        logger.debug("event assembly, checkEventsMap, size={}", assembledEventsMap.size());
        Collection<Tuple<Person, Long>> events = assembledEventsMap.values().stream().filter(x -> eventExpirationControl.isExpired(x))
                .map(x -> new Tuple<>(x.getInitiator(), x.getCaseObject().getId()))
                .distinct()
                .collect(Collectors.toList());

        if (!events.isEmpty()) {
            logger.debug("publish set of events, initiators : {}", events.size());
            events.forEach(this::publishAndClear);
        }
    }

    private void publishAndClear(Tuple<Person, Long> key) {
        AssembledCaseEvent personsEvent = assembledEventsMap.get(key);
        logger.debug("publishAndClear event, case:{}, person:{}", personsEvent.getCaseObject().defGUID(),
                personsEvent.getInitiator().getDisplayName());
        publisherService.publishEvent(personsEvent);
        assembledEventsMap.remove(key);
    }

    private boolean isEagerPush(AbstractCaseEvent event) {
        return EAGER_PUSH.contains(event.getCaseObject().getExtAppType());
    }

    private Tuple<Person, Long> makeEventKey(AbstractCaseEvent event) {
        return new Tuple<>(event.getPerson(), event.getCaseObject().getId());
    }

    private final Map<Tuple<Person, Long>, AssembledCaseEvent> assembledEventsMap = new ConcurrentHashMap<>();
    //Time interval for events checking, MS
    private final static long SCHEDULE_TIME = 5000;
    //app types for eager push
    private final static Set<String> EAGER_PUSH = new HashSet<String>() {{
        add("junit-test");
        add("redmine");
    }};
    private static Logger logger = LoggerFactory.getLogger(EventAssemblerServiceImpl.class);

    @Autowired
    EventPublisherService publisherService;
    @Autowired
    EventExpirationControl eventExpirationControl;
}
