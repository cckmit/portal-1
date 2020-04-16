package ru.protei.portal.core.event;

public interface AbstractProjectEvent {
    Long getPersonId();

    Long getProjectId();

    Object getSource();

    default boolean isCreateEvent() {
        return false;
    }
}
