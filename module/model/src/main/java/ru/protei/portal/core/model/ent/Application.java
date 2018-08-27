package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.struct.PathInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.List;

@JdbcEntity(table = "application")
public class Application implements Serializable, Removable {

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

    @JdbcColumn(name="dev_unit_id")
    private Long componentId;

    @JdbcJoinedObject(localColumn = "server_id", remoteColumn = "id")
    private Server server;

    @JdbcJoinedObject(localColumn = "dev_unit_id", remoteColumn = "id")
    private DevUnit component;

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

    public Long getComponentId() {
        return componentId;
    }

    public void setComponentId(Long componentId) {
        this.componentId = componentId;
    }

    public DevUnit getComponent() {
        return component;
    }

    public void setComponent(DevUnit component) {
        this.component = component;
    }


    public static Application fromEntityOption(EntityOption entityOption) {
        if (entityOption == null) {
            return null;
        }

        Application application = new Application();
        application.setId(entityOption.getId());
        application.setName(entityOption.getDisplayText());
        return application;
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
        return "Application{" +
                "id=" + id +
                ", serverId=" + serverId +
                ", componentId=" + componentId +
                ", name=" + name +
                ", comment=" + comment +
                ", paths=" + paths +
                '}';
    }
}
