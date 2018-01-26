package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
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
        if (personsCompleteEvents.keySet().stream().noneMatch(p -> p.equals(eventRelatedPerson))) {
            personsCompleteEvents.put(eventRelatedPerson, new CompleteCaseEvent(event));
        }
        objectEvents.add(event);
    }

    @Override
    @EventListener
    public void onCaseCommentEvent(CaseCommentEvent event) {
        Person eventRelatedPerson = event.getPerson();
        boolean isSecondComment
                = commentEvents.stream().map(x -> x.getPerson().getId()).anyMatch(x -> eventRelatedPerson.getId().equals(x));
        if (isSecondComment)
            assemblyAndPublish(eventRelatedPerson);
        commentEvents.add(event);
    }

    private void assemblyAndPublish(Person person) {
        CompleteCaseEvent personsEvent = personsCompleteEvents.get(person);
        objectEvents.stream().filter(x -> x.getPerson().equals(person))
                .forEach(personsEvent::attachCaseObjectEvent);
        commentEvents.stream().filter(x -> x.getPerson().equals(person)).forEach(personsEvent::attachCaseCommentEvent);
        publisherService.publishEvent(personsEvent);
        clear(person);
    }

    private void clear(Person person) {
        personsCompleteEvents.remove(person);
        objectEvents.removeIf(x -> x.getPerson().equals(person));
        commentEvents.removeIf(x -> x.getPerson().equals(person));
    }

    private final Map<Person, CompleteCaseEvent> personsCompleteEvents = new HashMap<>();
    private final List<CaseObjectEvent> objectEvents = new ArrayList<>();
    private final List<CaseCommentEvent> commentEvents = new ArrayList<>();

    @Autowired
    EventPublisherService publisherService;
}
