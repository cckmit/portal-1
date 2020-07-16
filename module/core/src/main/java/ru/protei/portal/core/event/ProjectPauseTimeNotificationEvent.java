package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.Person;

import java.util.List;

public class ProjectPauseTimeNotificationEvent extends ApplicationEvent {

    public ProjectPauseTimeNotificationEvent( Object source, List<Person> subscribers ) {
        super( source );
        this.subscribers = subscribers;
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

    public Long getProjectId() {
        return id;
    }

    public List<Person> getSubscribers() {
        return subscribers;
    }

    public Long projectNumber() {
        return projectNumber;

    }

    public String projectName() {
        return projectName;
    }

    public void setProjectName( String projectName ) {
        this.projectName = projectName;
    }

    public void setProjectNumber( Long projectNumber ) {
        this.projectNumber = projectNumber;
    }

    private Long id;
    private Long pauseDate;
    private List<Person> subscribers;
    private String projectName;
    private Long projectNumber;


    @Override
    public String toString() {
        return "ProjectPauseTimeNotificationEvent{" +
                "id=" + id +
                ", pauseDate=" + pauseDate +
                ", projectNumber=" + projectNumber +
                ", projectName='" + projectName +
                ", subscribers=" + subscribers +
                '}';
    }
}
