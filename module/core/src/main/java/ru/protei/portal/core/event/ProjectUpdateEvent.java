package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.dto.Project;

public class ProjectUpdateEvent extends ApplicationEvent implements AbstractProjectEvent {
    private Project oldProjectState;
    private Project newProjectState;
    private Long personId;

    public ProjectUpdateEvent(Object source, Project oldProjectState, Project newProjectState, Long personId) {
        super(source);
        this.oldProjectState = oldProjectState;
        this.personId = personId;
        this.source = source;
        this.newProjectState = newProjectState;
    }

    @Override
    public Long getPersonId() {
        return personId;
    }

    @Override
    public Long getProjectId() {
        return oldProjectState.getId();
    }

    public Project getOldProjectState() {
        return oldProjectState;
    }

    public Project getNewProjectState() {
        return newProjectState;
    }
}
