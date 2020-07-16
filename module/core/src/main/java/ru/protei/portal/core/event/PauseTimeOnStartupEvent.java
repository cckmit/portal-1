package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;

public class PauseTimeOnStartupEvent extends ApplicationEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public PauseTimeOnStartupEvent( Object source ) {
        super( source );
    }

    public void setProjectId( Long id ) {


        this.id = id;
    }

    public void setPauseDate( Long pauseDate ) {

        this.pauseDate = pauseDate;
    }

    public Long getPauseDate() {
        return pauseDate;
    }

    private Long id;
    private Long pauseDate;
}
