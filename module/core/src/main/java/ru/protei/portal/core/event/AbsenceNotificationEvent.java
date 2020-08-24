package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.PersonAbsence;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class AbsenceNotificationEvent extends ApplicationEvent {

    private final Person initiator;
    private final PersonAbsence oldState;
    private final PersonAbsence newState;
    private final List<PersonAbsence> multiAddAbsenceList;
    private final EventAction action;
    private final Set<Person> notifiers;

    public AbsenceNotificationEvent(Object source, EventAction action, Person initiator, PersonAbsence oldState,
                                    PersonAbsence newState, List<PersonAbsence> multiAddAbsenceList, Set<Person> notifiers) {
        super(source);
        this.action = action;
        this.initiator = initiator;
        this.oldState = oldState;
        this.newState = newState;
        this.notifiers = notifiers;
        this.multiAddAbsenceList = multiAddAbsenceList;
    }

    public EventAction getAction() {
        return action;
    }

    public Person getInitiator() {
        return initiator;
    }

    public PersonAbsence getOldState() {
        return oldState;
    }

    public PersonAbsence getNewState() {
        return newState;
    }

    public Set<Person> getNotifiers() {
        return notifiers;
    }

    public List<PersonAbsence> getMultiAddAbsenceList() {
        return multiAddAbsenceList;
    }

    public boolean isFromTimeChanged() {
        return oldState != null && !Objects.equals(oldState.getFromTime(), newState.getFromTime());
    }

    public boolean isTillTimeChanged() {
        return oldState != null && !Objects.equals(oldState.getTillTime(), newState.getTillTime());
    }

    public boolean isUserCommentChanged() {
        return oldState != null && !Objects.equals(oldState.getUserComment(), newState.getUserComment());
    }
}
