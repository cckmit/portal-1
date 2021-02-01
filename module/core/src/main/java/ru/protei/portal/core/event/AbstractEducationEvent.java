package ru.protei.portal.core.event;

import ru.protei.portal.core.model.ent.Person;

public interface AbstractEducationEvent {
    Person getPerson();

    Long getEducationId();

    Object getSource();

    default boolean isCreateEvent() {
        return false;
    }
}
