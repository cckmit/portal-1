package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.event.CaseCommentEvent;
import ru.protei.portal.core.event.CaseObjectEvent;
import ru.protei.portal.core.model.ent.Person;

import java.util.HashMap;
import java.util.Map;

public class EventAssemblerServiceImpl implements EventAssemblerService {

    @Override
    @EventListener
    public void onCaseObjectEvent(CaseObjectEvent event) {
        Person eventRelatedPerson = event.getPerson();
        if (assembledEventsMap.containsKey(eventRelatedPerson)) {
            AssembledCaseEvent caseEvent = assembledEventsMap.get(eventRelatedPerson);
            if (caseEvent.isLastStateSet()) {
                publishAndClear(eventRelatedPerson);
                assembledEventsMap.put(eventRelatedPerson, new AssembledCaseEvent(event));
            } else {
                caseEvent.attachCaseObject(event.getCaseObject());
                assembledEventsMap.put(eventRelatedPerson, caseEvent);
                publishAndClear(eventRelatedPerson);
            }
        } else
            assembledEventsMap.put(eventRelatedPerson, new AssembledCaseEvent(event));
    }

    @Override
    @EventListener
    public void onCaseCommentEvent(CaseCommentEvent event) {
        Person eventRelatedPerson = event.getPerson();
        if (assembledEventsMap.get(eventRelatedPerson).isCaseCommentAttached()) {
            publishAndClear(eventRelatedPerson);
            assembledEventsMap.put(eventRelatedPerson, new AssembledCaseEvent(event));
        } else {
            //In order to update case events map in both cases:
            // person and event pair already exist or
            // person and event pair does not exist;
            AssembledCaseEvent assembledCaseEvent = new AssembledCaseEvent(event);
            if (assembledEventsMap.containsKey(eventRelatedPerson)) {
                assembledCaseEvent = assembledEventsMap.get(eventRelatedPerson);
                assembledCaseEvent.attachCaseComment(event.getCaseComment());
            }
            assembledEventsMap.put(eventRelatedPerson, assembledCaseEvent);
        }
    }

    @Override
    public AssembledCaseEvent getPersonsEvent(Person person) {
        return assembledEventsMap.getOrDefault(person, null);
    }

    @Override
    public int getEventsCount() {
        return assembledEventsMap.size();
    }

    @Scheduled(fixedRate = SCHEDULE_TIME)
    public void checkEventsMap() {
        //Measured in ms
        assembledEventsMap.values().stream().filter(x -> eventExpirationControl.isExpired(x))
                .map(AssembledCaseEvent::getInitiator)
                .distinct()
                .forEach(this::publishAndClear);
    }

    private void publishAndClear(Person person) {
        AssembledCaseEvent personsEvent = assembledEventsMap.get(person);
        publisherService.publishEvent(personsEvent);
        clear(person);
    }

    private void clear(Person person) {
        assembledEventsMap.remove(person);
    }

    private final Map<Person, AssembledCaseEvent> assembledEventsMap = new HashMap<>();
    //Time interval for events checking, MS
    private final static long SCHEDULE_TIME = 5000;

    @Autowired
    EventPublisherService publisherService;

    @Autowired
    EventExpirationControl eventExpirationControl;
}
