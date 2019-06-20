package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import protei.utils.common.Tuple;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.event.CaseAttachmentEvent;
import ru.protei.portal.core.event.CaseCommentEvent;
import ru.protei.portal.core.event.CaseObjectEvent;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.HelperFunc;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EventAssemblerServiceImpl implements EventAssemblerService {

    @Override
    @EventListener
    public void publishEvent(CaseObjectEvent event) {
        if (EAGER_PUSH.contains(event.getCaseObject().getExtAppType())) {
            publisherService.publishEvent(new AssembledCaseEvent(event));
            return;
        }

        Person eventRelatedPerson = event.getPerson();
        Tuple<Person, Long> key = new Tuple<>(eventRelatedPerson, event.getCaseObject().getId());
        if (assembledEventsMap.containsKey(key)) {
            logger.debug("assemble event on case {}", event.getCaseObject().defGUID());
            AssembledCaseEvent caseEvent = assembledEventsMap.get(key);
            if (caseEvent.isLastStateSet()) {
                publishAndClear(key);
                assembledEventsMap.put(key, new AssembledCaseEvent(event));
            } else {
                //Cuz reference
                assembledEventsMap.get(key).attachCaseObject(event.getCaseObject());
            }
        } else {
            logger.debug("push new event on case {} for assembly", event.getCaseObject().defGUID());
            assembledEventsMap.put(key, new AssembledCaseEvent(event));

        }
    }

    @Override
    @EventListener
    public void publishEvent(CaseCommentEvent event) {
        if (EAGER_PUSH.contains(event.getCaseObject().getExtAppType())) {
            publisherService.publishEvent(new AssembledCaseEvent(event));
            return;
        }

        Person eventRelatedPerson = event.getPerson();
        Tuple<Person, Long> key = new Tuple<>(eventRelatedPerson, event.getCaseObject().getId());

        AssembledCaseEvent existingEvent = assembledEventsMap.get(key);
        if ( existingEvent != null && ( existingEvent.isCaseCommentAttached() || existingEvent.isCaseCommentRemoved() ) )
        {
            if (existingEvent.getCaseComment() != null && event.getRemovedCaseComment() != null
                    && ( HelperFunc.equals(existingEvent.getCaseComment().getId(), event.getRemovedCaseComment().getId()))) {
                logger.debug("onCaseCommentEvent, remove new (not mailed) comment on case {}", event.getCaseObject().defGUID());
                assembledEventsMap.remove(key);
            } else if (existingEvent.getCaseComment() != null && event.getCaseComment() != null
                    && HelperFunc.equals(existingEvent.getCaseComment().getId(), event.getCaseComment().getId())) {
                    ; // //we don't take into account a last edited comment
                } else {
                    logger.debug("onCaseCommentEvent, publish prev event on case {}", event.getCaseObject().defGUID());
                    publishAndClear(key);
                    assembledEventsMap.put(key, new AssembledCaseEvent(event));
                }
        } else {
            logger.debug("onCaseCommentEvent, push new event on case {}", event.getCaseObject().defGUID());
            //In order to update case events map in both cases:
            // person and event pair already exist or
            // person and event pair does not exist;

            if (assembledEventsMap.containsKey(key)) {
                logger.debug("attach comment event to existing case {}", event.getCaseObject().defGUID());
                AssembledCaseEvent assembledCaseEvent = assembledEventsMap.get(key);
                assembledCaseEvent.attachCaseComment(event.getCaseComment());
                assembledCaseEvent.attachCaseObject(event.getCaseObject());
                assembledCaseEvent.synchronizeAttachments(
                        event.getAddedAttachments(),
                        event.getRemovedAttachments()
                );
            } else {
                assembledEventsMap.put(key, new AssembledCaseEvent(event));
            }
        }
    }

    @Override
    @EventListener
    public void publishEvent(CaseAttachmentEvent event) {
        if (EAGER_PUSH.contains(event.getCaseObject().getExtAppType())) {
            publisherService.publishEvent(new AssembledCaseEvent(event));
            return;
        }

        Person eventRelatedPerson = event.getPerson();
        Tuple<Person, Long> key = new Tuple<>(eventRelatedPerson, event.getCaseObject().getId());
        logger.debug("onCaseAttachmentEvent, adding attachments on case {}", event.getCaseObject().defGUID());

        if (assembledEventsMap.containsKey(key)) {
            logger.debug("attach attachment event to existing case {}", event.getCaseObject().defGUID());
            assembledEventsMap.get(key).synchronizeAttachments(
                    event.getAddedAttachments(),
                    event.getRemovedAttachments()
            );
        } else {
            assembledEventsMap.put(key, new AssembledCaseEvent(event));
        }
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
