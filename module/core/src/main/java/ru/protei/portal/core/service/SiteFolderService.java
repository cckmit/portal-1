package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.ApplicationQuery;
import ru.protei.portal.core.model.query.PlatformQuery;
import ru.protei.portal.core.model.query.ServerQuery;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.List;

public interface SiteFolderService {

    @Privileged(En_Privilege.SITE_FOLDER_VIEW)
    CoreResponse<Long> countPlatforms(AuthToken token, PlatformQuery query);

    @Privileged(En_Privilege.SITE_FOLDER_VIEW)
    CoreResponse<Long> countServers(AuthToken token, ServerQuery query);

    @Privileged(En_Privilege.SITE_FOLDER_VIEW)
    CoreResponse<Long> countApplications(AuthToken token, ApplicationQuery query);


    @Privileged(En_Privilege.SITE_FOLDER_VIEW)
    CoreResponse<List<Platform>> listPlatforms(AuthToken token, PlatformQuery query);

    @Privileged(En_Privilege.SITE_FOLDER_VIEW)
    CoreResponse<List<Server>> listServers(AuthToken token, ServerQuery query);

    @Privileged(En_Privilege.SITE_FOLDER_VIEW)
    CoreResponse<List<Application>> listApplications(AuthToken token, ApplicationQuery query);

    @Privileged(En_Privilege.SITE_FOLDER_VIEW)
    CoreResponse<List<Server>> listServersWithAppsNames(AuthToken token, ServerQuery query);


    CoreResponse<List<EntityOption>> listPlatformsOptionList(AuthToken token, PlatformQuery query);

    CoreResponse<List<EntityOption>> listServersOptionList(AuthToken token, ServerQuery query);


    @Privileged(En_Privilege.SITE_FOLDER_VIEW)
    CoreResponse<Platform> getPlatform(AuthToken token, long id);

    @Privileged(En_Privilege.SITE_FOLDER_VIEW)
    CoreResponse<Server> getServer(AuthToken token, long id);

    @Privileged(En_Privilege.SITE_FOLDER_VIEW)
    CoreResponse<Application> getApplication(AuthToken token, long id);


    @Privileged(En_Privilege.SITE_FOLDER_CREATE)
    CoreResponse<Platform> createPlatform(AuthToken token, Platform platform);

    @Privileged(En_Privilege.SITE_FOLDER_CREATE)
    CoreResponse<Server> createServer(AuthToken token, Server server, Long serverIdToBeCloned);

    @Privileged(En_Privilege.SITE_FOLDER_CREATE)
    CoreResponse<Application> createApplication(AuthToken token, Application application);


    @Privileged(En_Privilege.SITE_FOLDER_EDIT)
    CoreResponse<Platform> updatePlatform(AuthToken token, Platform platform);

    @Privileged(En_Privilege.SITE_FOLDER_EDIT)
    CoreResponse<Server> updateServer(AuthToken token, Server server);

    @Privileged(En_Privilege.SITE_FOLDER_EDIT)
    CoreResponse<Application> updateApplication(AuthToken token, Application application);


    @Privileged(En_Privilege.SITE_FOLDER_REMOVE)
    CoreResponse<Boolean> removePlatform(AuthToken token, long id);

    @Privileged(En_Privilege.SITE_FOLDER_REMOVE)
    CoreResponse<Boolean> removeServer(AuthToken token, long id);

    @Privileged(En_Privilege.SITE_FOLDER_REMOVE)
    CoreResponse<Boolean> removeApplication(AuthToken token, long id);
}
