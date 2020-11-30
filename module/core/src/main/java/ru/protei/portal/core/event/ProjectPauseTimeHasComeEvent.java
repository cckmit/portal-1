package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;

public class ProjectPauseTimeHasComeEvent extends ApplicationEvent {

    public ProjectPauseTimeHasComeEvent( Object source, Long id, Long pauseDate ) {
        super( source );
        this.id = id;
        this.pauseDate = pauseDate;
    }

    public Long getPauseDate() {
        return pauseDate;
    }

    public Long getProjectId() {
        return id;
    }

    private Long id;
    private Long pauseDate;
}
