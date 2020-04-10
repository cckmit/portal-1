package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.CaseLink;

public class ProjectLinkEvent extends ApplicationEvent implements AbstractProjectEvent {
    private Long projectId;
    private Long personId;
    private CaseLink addedLink;
    private CaseLink removedLink;

    public ProjectLinkEvent(Long projectId, Long personId, CaseLink addedLink, CaseLink removedLink, Object source) {
        super(source);
        this.projectId = projectId;
        this.personId = personId;
        this.addedLink = addedLink;
        this.removedLink = removedLink;
        this.source = source;
    }

    @Override
    public Long getPersonId() {
        return personId;
    }

    @Override
    public Long getProjectId() {
        return projectId;
    }

    public CaseLink getAddedLink() {
        return addedLink;
    }

    public CaseLink getRemovedLink() {
        return removedLink;
    }
}
