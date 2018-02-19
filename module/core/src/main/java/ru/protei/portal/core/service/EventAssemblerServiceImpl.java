package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.event.CaseAttachmentEvent;
import ru.protei.portal.core.event.CaseCommentEvent;
import ru.protei.portal.core.event.CaseObjectEvent;
import ru.protei.portal.core.model.ent.Person;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EventAssemblerServiceImpl implements EventAssemblerService {

    private static Logger logger = LoggerFactory.getLogger(EventAssemblerServiceImpl.class);

    @Override
    @EventListener
    public void onCaseObjectEvent(CaseObjectEvent event) {
        Person eventRelatedPerson = event.getPerson();
        int hash = computeHash(eventRelatedPerson, event.getCaseObject().getId());
        if (assembledEventsMap.containsKey(hash)) {
            logger.debug("assemble event on case {}", event.getCaseObject().defGUID());
            AssembledCaseEvent caseEvent = assembledEventsMap.get(hash);
            if (caseEvent.isLastStateSet()) {
                publishAndClear(hash);
                assembledEventsMap.put(hash, new AssembledCaseEvent(event));
            } else {
                //Cuz reference
                caseEvent.attachCaseObject(event.getCaseObject());
            }
        } else {
            logger.debug("push new event on case {} for assembly", event.getCaseObject().defGUID());
            assembledEventsMap.put(hash, new AssembledCaseEvent(event));
        }
    }

    @Override
    @EventListener
    public void onCaseCommentEvent(CaseCommentEvent event) {
        Person eventRelatedPerson = event.getPerson();
        int hash = computeHash(eventRelatedPerson, event.getCaseObject().getId());
        if (assembledEventsMap.containsKey(hash)
                && assembledEventsMap.get(hash).isCaseCommentAttached()) {
            logger.debug("onCaseCommentEvent, publish prev event on case {}", event.getCaseObject().defGUID());
            publishAndClear(hash);
            assembledEventsMap.put(hash, new AssembledCaseEvent(event));
        } else {
            logger.debug("onCaseCommentEvent, push new event on case {}", event.getCaseObject().defGUID());
            //In order to update case events map in both cases:
            // person and event pair already exist or
            // person and event pair does not exist;
            AssembledCaseEvent assembledCaseEvent = new AssembledCaseEvent(event);
            if (assembledEventsMap.containsKey(hash)) {
                logger.debug("attach comment event to existing case {}", event.getCaseObject().defGUID());
                assembledCaseEvent = assembledEventsMap.get(hash);
                assembledCaseEvent.attachCaseComment(event.getCaseComment());
                assembledCaseEvent.attachAddedAttachments(event.getAddedAttachments());
                assembledCaseEvent.attachRemovedAttachments(event.getRemovedAttachments());
            }
            assembledEventsMap.put(hash, assembledCaseEvent);
        }
    }

    @Override
    @EventListener
    public void onCaseAttachmentEvent(CaseAttachmentEvent event) {
        Person eventRelatedPerson = event.getPerson();
        int hash = computeHash(eventRelatedPerson, event.getCaseObject().getId());
        logger.debug("onCaseAttachmentEvent, adding attachments on case {}", event.getCaseObject().defGUID());
        AssembledCaseEvent assembledCaseEvent = new AssembledCaseEvent(event);
        if (assembledEventsMap.containsKey(hash)) {
            logger.debug("attach attachment event to existing case {}", event.getCaseObject().defGUID());
            assembledCaseEvent = assembledEventsMap.get(hash);
            assembledCaseEvent.attachAddedAttachments(event.getAddedAttachments());
            assembledCaseEvent.attachRemovedAttachments(event.getRemovedAttachments());
        } else
            assembledEventsMap.put(hash, assembledCaseEvent);
    }

    @Override
    public AssembledCaseEvent getEvent(Person person, long caseId) {
        return assembledEventsMap.getOrDefault(computeHash(person, caseId), null);
    }

    @Override
    public int getEventsCount() {
        return assembledEventsMap.size();
    }

    @Scheduled(fixedRate = SCHEDULE_TIME)
    public void checkEventsMap() {
        //Measured in ms
        logger.debug("event assembly, checkEventsMap, size={}", assembledEventsMap.size());
        Collection<Integer> events = assembledEventsMap.values().stream().filter(x -> eventExpirationControl.isExpired(x))
                .map(x -> computeHash(x.getInitiator(), x.getCaseObject().getId()))
                .distinct()
                .collect(Collectors.toList());

        if (!events.isEmpty()) {
            logger.debug("publish set of events, initiators : {}", events.size());
            events.forEach(this::publishAndClear);
        }
    }

    private void publishAndClear(int hash) {
        AssembledCaseEvent personsEvent = assembledEventsMap.get(hash);
        logger.debug("publishAndClear event, case:{}, person:{}", personsEvent.getCaseObject().defGUID(),
                personsEvent.getInitiator().getDisplayName());
        publisherService.publishEvent(personsEvent);
        assembledEventsMap.remove(hash);
    }

    private int computeHash(Person person, long caseId) {
        int result = 37;
        return  31 * result + ((int) (person.getId() >>> 32) * (int) caseId);
    }

    private final Map<Integer, AssembledCaseEvent> assembledEventsMap = new ConcurrentHashMap<>();
    //Time interval for events checking, MS
    private final static long SCHEDULE_TIME = 5000;

    @Autowired
    EventPublisherService publisherService;

    @Autowired
    EventExpirationControl eventExpirationControl;
}
