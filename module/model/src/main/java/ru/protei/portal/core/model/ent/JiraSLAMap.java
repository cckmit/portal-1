package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

import java.io.Serializable;

@JdbcEntity(table = "jira_sla_map")
public class JiraSLAMap implements Serializable {

    @JdbcId(name = "id")
    private long id;

    @JdbcColumn(name = "name")
    private String name;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
