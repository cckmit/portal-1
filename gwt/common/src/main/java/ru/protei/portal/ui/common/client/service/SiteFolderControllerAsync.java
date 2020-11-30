package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.Application;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.core.model.ent.Server;
import ru.protei.portal.core.model.query.ApplicationQuery;
import ru.protei.portal.core.model.query.PlatformQuery;
import ru.protei.portal.core.model.query.ServerQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PlatformOption;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

public interface SiteFolderControllerAsync {

    void getPlatforms(PlatformQuery query, AsyncCallback<SearchResult<Platform>> async);

    void getServers(ServerQuery query, AsyncCallback<SearchResult<Server>> async);

    void getApplications(ApplicationQuery query, AsyncCallback<SearchResult<Application>> async);

    void getServersWithAppsNames(ServerQuery query, AsyncCallback<SearchResult<Server>> async);


    void getServersOptionList(ServerQuery query, AsyncCallback<List<EntityOption>> async);

    void getPlatformsOptionList(PlatformQuery query, AsyncCallback<List<PlatformOption>> async);


    void getPlatform(long id, AsyncCallback<Platform> async);

    void getServer(long id, AsyncCallback<Server> async);

    void getApplication(long id, AsyncCallback<Application> async);

    void savePlatform(Platform platform, AsyncCallback<Platform> async);

    void saveServer(Server server, Long serverIdOfAppsToBeCloned, AsyncCallback<Server> async);

    void saveApplication(Application application, AsyncCallback<Application> async);


    void removePlatform(long id, AsyncCallback<Long> async);

    void removeServer(long id, AsyncCallback<Long> async);

    void removeApplication(long id, AsyncCallback<Long> async);
}
