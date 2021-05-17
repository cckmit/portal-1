package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@JdbcEntity(table = "case_object")
public class CaseObjectMetaNotifiers implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.EXPLICIT)
    private Long id;

    @JdbcColumn(name = "MODIFIED")
    private Date modified;

    @JdbcManyToMany(linkTable = "case_notifier", localLinkColumn = "case_id", remoteLinkColumn = "person_id")
    private Set<Person> notifiers; //may contain partially filled objects!

    public CaseObjectMetaNotifiers() {}

    public CaseObjectMetaNotifiers(CaseObject co) {
        setId(co.getId());
        setModified(co.getModified());
        setNotifiers(co.getNotifiers());
    }

    public CaseObjectMetaNotifiers(Delivery de) {
        setId(de.getId());
        setModified(de.getModified());
        setNotifiers(de.getSubscribers());
    }

    public CaseObject collectToCaseObject(CaseObject co) {
        co.setId(getId());
        co.setModified(getModified());
        co.setNotifiers(getNotifiers());
        return co;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public Set<Person> getNotifiers() {
        return notifiers;
    }

    public void setNotifiers(Set<Person> notifiers) {
        this.notifiers = notifiers;
    }
}
