package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

import java.io.Serializable;

/**
 * Created by michael on 05.05.17.
 */
@JdbcEntity(table = "WorkingGroup")
public class WorkingGroup implements Serializable {
    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="groupName")
    private String name;

    @JdbcColumn(name="groupInfo")
    private String info;


    public WorkingGroup() {
    }

    public WorkingGroup(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }


    @Override
    public int hashCode() {
        return this.id == null ? 0 : this.id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WorkingGroup) {
            Long oid = ((WorkingGroup)obj).getId();
            return this.id == null ? oid == null : oid != null && this.id.equals(oid);
        }
        return false;
    }
}
