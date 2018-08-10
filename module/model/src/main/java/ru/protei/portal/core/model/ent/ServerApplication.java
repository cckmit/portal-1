package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;

@JdbcEntity(table = "server")
public class ServerApplication implements Serializable {

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

    @JdbcJoinedObject(localColumn = "platform_id", remoteColumn = "id")
    private Platform platform;

    @JdbcJoinedColumn(localColumn = "id", table = "application", remoteColumn = "server_id", mappedColumn = "name")
    private String appName;

    //@JdbcJoinedColumn(localColumn = "id", table = "application", remoteColumn = "server_id", mappedColumn = "comment")
    //private String appComment;

    //@JdbcJoinedColumn(localColumn = "id", table = "application", remoteColumn = "server_id", mappedColumn = "paths", converterType = ConverterType.JSON)
    //private PathInfo appPaths;

    public Server toServer() {
        Server server = new Server();
        server.setId(id);
        server.setPlatformId(platformId);
        server.setName(name);
        server.setIp(ip);
        server.setParams(params);
        server.setComment(comment);
        server.setPlatform(platform);
        return server;
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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
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
                ", appName=" + appName +
                '}';
    }
}
