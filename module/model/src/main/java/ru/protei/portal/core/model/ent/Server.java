package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JdbcEntity(table = "server")
public class Server implements Serializable, Removable {

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

    private Long applicationsCount;

    private List<String> appNames;

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

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public Long getApplicationsCount() {
        return applicationsCount;
    }

    public void setApplicationsCount(Long applicationsCount) {
        this.applicationsCount = applicationsCount;
    }

    public List<String> getAppNames() {
        return appNames;
    }

    public void setAppNames(List<String> appNames) {
        this.appNames = appNames;
    }

    public void addAppName(String appName) {
        if (appNames == null) {
            appNames = new ArrayList<>();
        }
        appNames.add(appName);
    }


    public static Server fromEntityOption(EntityOption entityOption) {
        if (entityOption == null) {
            return null;
        }

        Server server = new Server();
        server.setId(entityOption.getId());
        server.setName(entityOption.getDisplayText());
        return server;
    }

    public EntityOption toEntityOption() {
        EntityOption entityOption = new EntityOption();
        entityOption.setId(getId());
        entityOption.setDisplayText(getName());
        return entityOption;
    }


    @Override
    public boolean isAllowedRemove() {
        return id != null;
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
