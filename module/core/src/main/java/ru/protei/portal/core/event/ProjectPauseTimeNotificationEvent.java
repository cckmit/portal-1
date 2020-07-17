package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.Person;

import java.util.Date;

public class ProjectPauseTimeNotificationEvent extends ApplicationEvent {

    public ProjectPauseTimeNotificationEvent( Object source, Person subscriber, Long projectId, String projectName, Date pauseDate ) {
        super( source );
        this.subscriber = subscriber;
        this.projectId = projectId;
        this.projectName = projectName;
        this.pauseDate = pauseDate;
    }

    public void setProjectId( Long id ) {
        this.projectId = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setPauseDate( Date pauseDate ) {
        this.pauseDate = pauseDate;
    }

    public Date getPauseDate() {
        return pauseDate;
    }

    public Person getSubscriber() {
        return subscriber;
    }

    public void setProjectName( String projectName ) {
        this.projectName = projectName;
    }

    public String getProjectName() {
        return projectName;
    }

    private Long projectId;
    private Date pauseDate;
    private Person subscriber;
    private String projectName;

    @Override
    public String toString() {
        return "ProjectPauseTimeNotificationEvent{" +
                "projectId=" + projectId +
                ", pauseDate=" + pauseDate +
                ", projectNumber=" + projectId +
                ", projectName='" + projectName +
                ", subscriber=" + subscriber +
                '}';
    }

}
