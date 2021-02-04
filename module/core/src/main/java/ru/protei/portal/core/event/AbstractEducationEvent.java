package ru.protei.portal.core.event;

import ru.protei.portal.core.model.ent.EducationEntry;
import ru.protei.portal.core.model.ent.Person;

public interface AbstractEducationEvent {
    Person getInitiator();

    EducationEntry getEducationEntry();

    Object getSource();

    default boolean isCreateEvent() {
        return false;
    }

    String getTypeName();
}
