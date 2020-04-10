package ru.protei.portal.core.event;

public interface AbstractProjectEvent {
    Long getPersonId();

    Long getProjectId();

    Object getSource();
}
