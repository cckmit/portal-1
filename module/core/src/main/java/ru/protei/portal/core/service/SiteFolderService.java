package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Application;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.core.model.ent.Server;
import ru.protei.portal.core.model.query.ApplicationQuery;
import ru.protei.portal.core.model.query.PlatformQuery;
import ru.protei.portal.core.model.query.ServerQuery;

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
    CoreResponse<Platform> getPlatform(AuthToken token, long id);

    @Privileged(En_Privilege.SITE_FOLDER_VIEW)
    CoreResponse<Server> getServer(AuthToken token, long id);

    @Privileged(En_Privilege.SITE_FOLDER_VIEW)
    CoreResponse<Application> getApplication(AuthToken token, long id);


    @Privileged(En_Privilege.SITE_FOLDER_CREATE)
    CoreResponse<Boolean> createPlatform(AuthToken token, Platform platform);

    @Privileged(En_Privilege.SITE_FOLDER_CREATE)
    CoreResponse<Boolean> createServer(AuthToken token, Server server);

    @Privileged(En_Privilege.SITE_FOLDER_CREATE)
    CoreResponse<Boolean> createApplication(AuthToken token, Application application);


    @Privileged(En_Privilege.SITE_FOLDER_EDIT)
    CoreResponse<Boolean> updatePlatform(AuthToken token, Platform platform);

    @Privileged(En_Privilege.SITE_FOLDER_EDIT)
    CoreResponse<Boolean> updateServer(AuthToken token, Server server);

    @Privileged(En_Privilege.SITE_FOLDER_EDIT)
    CoreResponse<Boolean> updateApplication(AuthToken token, Application application);


    @Privileged(En_Privilege.SITE_FOLDER_REMOVE)
    CoreResponse<Boolean> removePlatform(AuthToken token, long id);

    @Privileged(En_Privilege.SITE_FOLDER_REMOVE)
    CoreResponse<Boolean> removeServer(AuthToken token, long id);

    @Privileged(En_Privilege.SITE_FOLDER_REMOVE)
    CoreResponse<Boolean> removeApplication(AuthToken token, long id);
}
