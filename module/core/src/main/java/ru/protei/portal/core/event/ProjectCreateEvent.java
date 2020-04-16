package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;

public class ProjectCreateEvent extends ApplicationEvent implements AbstractProjectEvent {
    private Long personId;
    private Long projectId;

    public ProjectCreateEvent(Object source, Long personId, Long projectId) {
        super(source);
        this.personId = personId;
        this.projectId = projectId;
    }

    @Override
    public Long getPersonId() {
        return personId;
    }

    @Override
    public Long getProjectId() {
        return projectId;
    }

    @Override
    public boolean isCreateEvent() {
        return true;
    }
}
