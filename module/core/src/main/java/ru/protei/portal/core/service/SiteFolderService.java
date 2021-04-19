package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.ApplicationQuery;
import ru.protei.portal.core.model.query.PlatformQuery;
import ru.protei.portal.core.model.query.ServerGroupQuery;
import ru.protei.portal.core.model.query.ServerQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PlatformOption;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

public interface SiteFolderService {

    @Privileged(En_Privilege.SITE_FOLDER_VIEW)
    Result<SearchResult<Platform>> getPlatforms( AuthToken token, PlatformQuery query);

    @Privileged(En_Privilege.SITE_FOLDER_VIEW)
    Result<SearchResult<Server>> getServers( AuthToken token, ServerQuery query);

    @Privileged(En_Privilege.SITE_FOLDER_VIEW)
    Result<SearchResult<Application>> getApplications( AuthToken token, ApplicationQuery query);

    @Privileged(En_Privilege.SITE_FOLDER_VIEW)
    Result<SearchResult<Server>> getServersWithAppsNames( AuthToken token, ServerQuery query);

    @Privileged(value = En_Privilege.SITE_FOLDER_VIEW)
    Result<List<ServerGroup>> getServerGroups(AuthToken token, ServerGroupQuery serverGroupQuery);

    Result<List<PlatformOption>> listPlatformsOptionList(AuthToken token, PlatformQuery query);

    Result<List<EntityOption>> listServersOptionList( AuthToken token, ServerQuery query);

    @Privileged(En_Privilege.SITE_FOLDER_VIEW)
    Result<Platform> getPlatform( AuthToken token, long id);

    @Privileged(En_Privilege.SITE_FOLDER_VIEW)
    Result<Server> getServer( AuthToken token, long id);

    @Privileged(En_Privilege.SITE_FOLDER_VIEW)
    Result<Application> getApplication( AuthToken token, long id);


    @Privileged(En_Privilege.SITE_FOLDER_CREATE)
    @Auditable(En_AuditType.PLATFORM_CREATE)
    Result<Platform> createPlatform( AuthToken token, Platform platform);

    @Privileged(En_Privilege.SITE_FOLDER_CREATE)
    @Auditable(En_AuditType.SERVER_CREATE)
    Result<Server> createServer( AuthToken token, Server server);

    @Privileged(En_Privilege.SITE_FOLDER_CREATE)
    Result<Server> createServerAndCloneApps( AuthToken token, Server server, Long serverIdOfAppsToBeCloned);

    @Privileged(En_Privilege.SITE_FOLDER_CREATE)
    @Auditable(En_AuditType.APPLICATION_CREATE)
    Result<Application> createApplication( AuthToken token, Application application);

    @Privileged(value = En_Privilege.SITE_FOLDER_CREATE)
    @Auditable(value = En_AuditType.SERVER_GROUP_CREATE)
    Result<ServerGroup> createServerGroup(AuthToken token, ServerGroup serverGroup);

    @Privileged(En_Privilege.SITE_FOLDER_EDIT)
    @Auditable(En_AuditType.PLATFORM_MODIFY)
    Result<Platform> updatePlatform( AuthToken token, Platform platform);

    @Privileged(En_Privilege.SITE_FOLDER_EDIT)
    @Auditable(En_AuditType.SERVER_MODIFY)
    Result<Server> updateServer( AuthToken token, Server server);

    @Privileged(En_Privilege.SITE_FOLDER_EDIT)
    @Auditable(En_AuditType.APPLICATION_MODIFY)
    Result<Application> updateApplication( AuthToken token, Application application);

    @Privileged(value = En_Privilege.SITE_FOLDER_EDIT)
    @Auditable(value = En_AuditType.SERVER_GROUP_MODIFY)
    Result<ServerGroup> updateServerGroup(AuthToken token, ServerGroup serverGroup);

    @Privileged(En_Privilege.SITE_FOLDER_REMOVE)
    @Auditable(En_AuditType.PLATFORM_REMOVE)
    Result<Long> removePlatform( AuthToken token, long id);

    @Privileged(En_Privilege.SITE_FOLDER_REMOVE)
    @Auditable(En_AuditType.SERVER_REMOVE)
    Result<Long> removeServer( AuthToken token, long id);

    @Privileged(En_Privilege.SITE_FOLDER_REMOVE)
    @Auditable(En_AuditType.APPLICATION_REMOVE)
    Result<Long> removeApplication( AuthToken token, long id);

    @Privileged(value = En_Privilege.SITE_FOLDER_REMOVE)
    @Auditable(value = En_AuditType.SERVER_GROUP_REMOVE)
    Result<Long> removeServerGroup(AuthToken token, Long serverGroupId);
}
