package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.struct.Project;

public class ProjectSaveEvent extends ApplicationEvent implements AbstractProjectEvent {
    private Project project;
    private Long personId;
    private Object source;

    public ProjectSaveEvent(Project project, Long personId, Object source) {
        super(source);
        this.project = project;
        this.personId = personId;
        this.source = source;
    }

    @Override
    public Long getPersonId() {
        return personId;
    }

    @Override
    public Long getProjectId() {
        return project.getId();
    }

    @Override
    public Object getSource() {
        return source;
    }

    public Project getProject() {
        return project;
    }
}
