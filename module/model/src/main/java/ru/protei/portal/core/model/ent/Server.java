package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.List;

@JdbcEntity(table = "server")
public class Server implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="platform_id")
    private Long platformId;

    @JdbcColumn(name="name")
    private String name;

    @JdbcColumn(name="ip")
    private String ip;

    @JdbcColumn(name="parameters")
    private String params;

    @JdbcColumn(name="comment")
    private String comment;

    @JdbcOneToMany(localColumn = "id", table = "application", remoteColumn = "server_id")
    private List<Application> servers;

    @JdbcJoinedObject(localColumn = "platform_id", table = "platform")
    private Platform platform;

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

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Application> getServers() {
        return servers;
    }

    public void setServers(List<Application> servers) {
        this.servers = servers;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    @Override
    public String toString() {
        return "Server{" +
                "id=" + id +
                ", platformId=" + platformId +
                ", name=" + name +
                ", ip=" + ip +
                ", params=" + params +
                ", comment=" + comment +
                '}';
    }
}
