package ru.protei.portal.core.event;

public interface AbstractEmployeeRegistrationEvent {

    Long getPersonId();

    Long getEmployeeRegistrationId();

    Object getSource();

    default boolean isCreateEvent() {
        return false;
    }
}
