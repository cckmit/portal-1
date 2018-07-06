package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.Application;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.core.model.ent.Server;
import ru.protei.portal.core.model.query.ApplicationQuery;
import ru.protei.portal.core.model.query.PlatformQuery;
import ru.protei.portal.core.model.query.ServerQuery;

import java.util.List;

public interface SiteFolderControllerAsync {

    void getPlatformsCount(PlatformQuery query, AsyncCallback<Long> async);

    void getServersCount(ServerQuery query, AsyncCallback<Long> async);

    void getApplicationsCount(ApplicationQuery query, AsyncCallback<Long> async);

    void getPlatforms(PlatformQuery query, AsyncCallback<List<Platform>> async);

    void getServers(ServerQuery query, AsyncCallback<List<Server>> async);

    void getApplications(ApplicationQuery query, AsyncCallback<List<Application>> async);

    void getPlatform(long id, AsyncCallback<Platform> async);

    void getServer(long id, AsyncCallback<Server> async);

    void getApplication(long id, AsyncCallback<Application> async);

    void savePlatform(Platform platform, AsyncCallback<Platform> async);

    void saveServer(Server server, AsyncCallback<Server> async);

    void saveApplication(Application application, AsyncCallback<Application> async);

    void removePlatform(long id, AsyncCallback<Boolean> async);

    void removeServer(long id, AsyncCallback<Boolean> async);

    void removeApplication(long id, AsyncCallback<Boolean> async);
}
