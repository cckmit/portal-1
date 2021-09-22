package ru.protei.portal.core.event;

public interface AbstractCardBatchEvent {

    Long getPersonId();

    Long getCardBatchId();

    Object getSource();

    default boolean isCreateEvent() {
        return false;
    }
}
