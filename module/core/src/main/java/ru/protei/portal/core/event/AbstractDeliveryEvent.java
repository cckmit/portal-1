package ru.protei.portal.core.event;

public interface AbstractDeliveryEvent {

    Long getPersonId();

    Long getDeliveryId();

    Object getSource();

    default boolean isCreateEvent() {
        return false;
    }
}
