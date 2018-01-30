package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import ru.protei.portal.core.event.CaseCommentEvent;
import ru.protei.portal.core.event.CaseObjectEvent;
import ru.protei.portal.core.event.CompleteCaseEvent;
import ru.protei.portal.core.model.ent.Person;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventAssemblerServiceImpl implements EventAssemblerService {

    @Override
    @EventListener
    public void onCaseObjectEvent(CaseObjectEvent event) {
        Person eventRelatedPerson = event.getPerson();
        if (personsCompleteEvents.containsKey(eventRelatedPerson)) {
            CompleteCaseEvent caseEvent = personsCompleteEvents.get(eventRelatedPerson);
            if (caseEvent.isLastStateSet()) {
                publishAndClear(eventRelatedPerson);
            } else {
                caseEvent.attachCaseObjectEvent(event);
                personsCompleteEvents.put(eventRelatedPerson, caseEvent);
                publishAndClear(eventRelatedPerson);
            }
        } else
            personsCompleteEvents.put(eventRelatedPerson, new CompleteCaseEvent(event));
    }

    @Override
    @EventListener
    public void onCaseCommentEvent(CaseCommentEvent event) {
        Person eventRelatedPerson = event.getPerson();
        boolean isSecondComment
                = commentEvents.stream().map(x -> x.getPerson().getId())
                .anyMatch(x -> eventRelatedPerson.getId().equals(x));
        if (isSecondComment) {
            publishAndClear(eventRelatedPerson);
            commentEvents.add(event);
        } else {
            CompleteCaseEvent completeCaseEvent = new CompleteCaseEvent(event);
            if (personsCompleteEvents.containsKey(eventRelatedPerson)) {
                completeCaseEvent = personsCompleteEvents.get(eventRelatedPerson);
                completeCaseEvent.attachCaseCommentEvent(event);
            }
            personsCompleteEvents.put(eventRelatedPerson, completeCaseEvent);
        }
    }

    @Scheduled(fixedRate = 5000)
    public void checkEventsMap() {
        final long currentTime = java.util.concurrent.TimeUnit.NANOSECONDS.toSeconds(System.nanoTime());
        personsCompleteEvents.values().stream().filter(x -> currentTime - x.getLastUpdated() >= 30)
                .map(CompleteCaseEvent::getPerson)
                .distinct()
                .forEach(this::publishAndClear);
    }

    private void publishAndClear(Person person) {
        CompleteCaseEvent personsEvent = personsCompleteEvents.get(person);
        publisherService.publishEvent(personsEvent);
        clear(person);
    }

    private void clear(Person person) {
        personsCompleteEvents.remove(person);
        commentEvents.removeIf(x -> x.getPerson().equals(person));
    }

    private final Map<Person, CompleteCaseEvent> personsCompleteEvents = new HashMap<>();
    private final List<CaseCommentEvent> commentEvents = new ArrayList<>();

    @Autowired
    EventPublisherService publisherService;
}
