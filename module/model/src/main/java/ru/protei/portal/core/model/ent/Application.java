package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.struct.PathInfo;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.List;

@JdbcEntity(table = "application")
public class Application implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="server_id")
    private Long serverId;

    @JdbcColumn(name="name")
    private String name;

    @JdbcColumn(name="comment")
    private String comment;

    @JdbcColumn(name="paths", converterType = ConverterType.JSON)
    private PathInfo paths;

    @JdbcJoinedObject(localColumn = "server_id", table = "server")
    private Server server;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public PathInfo getPaths() {
        return paths;
    }

    public void setPaths(PathInfo paths) {
        this.paths = paths;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    @Override
    public String toString() {
        return "Application{" +
                "id=" + id +
                ", serverId=" + serverId +
                ", name=" + name +
                ", comment=" + comment +
                ", paths=" + paths +
                '}';
    }
}
