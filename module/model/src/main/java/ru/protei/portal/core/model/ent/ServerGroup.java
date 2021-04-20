package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

import java.util.Objects;

@JdbcEntity(table = "server_group")
public class ServerGroup extends AuditableObject {
    public static final String AUDIT_TYPE = "ServerGroup";

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "platform_id")
    private Long platformId;

    @JdbcColumn(name = "name")
    private String name;

    @Override
    public String getAuditType() {
        return AUDIT_TYPE;
    }

    public ServerGroup() {}

    public ServerGroup(Long platformId) {
        this.platformId = platformId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Long platformId) {
        this.platformId = platformId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerGroup that = (ServerGroup) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ServerGroup{" +
                "id=" + id +
                ", platformId=" + platformId +
                ", name='" + name + '\'' +
                '}';
    }
}
