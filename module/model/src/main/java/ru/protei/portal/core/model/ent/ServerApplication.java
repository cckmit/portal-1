package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;

@JdbcEntity(table = "server")
public class ServerApplication implements Serializable {

    @JdbcEmbed
    private Server server;

    @JdbcJoinedObject(localColumn = "id", table = "application", remoteColumn = "server_id")
    private Application application;

    public Server getServer() {
        return server;
    }

    public Application getApplication() {
        return application;
    }

    @Override
    public String toString() {
        return "ServerApplication{" +
                "server=" + server +
                ", application=" + application +
                '}';
    }
}
