package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;

@JdbcEntity(table = "person_notifier")
public class PersonNotifier implements Serializable {

    @JdbcId(name = "id",idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "person_id")
    private Long personId;

    @JdbcJoinedObject(localColumn="person_id", remoteColumn = "id")
    private Person person;

    @JdbcColumn(name = "notifier_id")
    private Long notifierId;

    @JdbcJoinedObject(localColumn="notifier_id", remoteColumn = "id")
    private Person notifier;

    public PersonNotifier() {
    }

    public PersonNotifier(Long personId, Long notifierId) {
        this.personId = personId;
        this.notifierId = notifierId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Long getNotifierId() {
        return notifierId;
    }

    public void setNotifierId(Long notifierId) {
        this.notifierId = notifierId;
    }

    public Person getNotifier() {
        return notifier;
    }

    public void setNotifier(Person notifier) {
        this.notifier = notifier;
    }
}
