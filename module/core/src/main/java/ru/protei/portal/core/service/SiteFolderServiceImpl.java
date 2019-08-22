package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.exception.ResultStatusException;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.ApplicationQuery;
import ru.protei.portal.core.model.query.PlatformQuery;
import ru.protei.portal.core.model.query.ServerQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.CoreResponse.errorSt;
import static ru.protei.portal.api.struct.CoreResponse.ok;

public class SiteFolderServiceImpl implements SiteFolderService {

    @Override
    public CoreResponse<SearchResult<Platform>> getPlatforms(AuthToken token, PlatformQuery query) {

        SearchResult<Platform> sr = platformDAO.getSearchResultByQuery(query);

        if ( CollectionUtils.isEmpty(sr.getResults())) {
            return new CoreResponse<SearchResult<Platform>>().success(sr);
        }

        Map<Long, Long> map = serverDAO.countByPlatformIds(sr.getResults().stream()
                .map(Platform::getId)
                .collect(Collectors.toList()));

        sr.getResults().forEach(platform -> {
            Long count = map.getOrDefault(platform.getId(), 0L);
            platform.setServersCount(count);
            // RESET PRIVACY INFO
            if (platform.getManager() != null) {
                platform.getManager().resetPrivacyInfo();
            }
        });

        return new CoreResponse<SearchResult<Platform>>().success(sr);
    }

    @Override
    public CoreResponse<SearchResult<Server>> getServers(AuthToken token, ServerQuery query) {

        SearchResult<Server> sr = serverDAO.getSearchResultByQuery(query);

        if (CollectionUtils.isEmpty(sr.getResults())) {
            return new CoreResponse<SearchResult<Server>>().success(sr);
        }

        Map<Long, Long> map = applicationDAO.countByServerIds(sr.getResults().stream()
                .map(Server::getId)
                .collect(Collectors.toList()));

        sr.getResults().forEach(server -> {
            Long count = map.getOrDefault(server.getId(), 0L);
            server.setApplicationsCount(count);
        });

        return new CoreResponse<SearchResult<Server>>().success(sr);
    }

    @Override
    public CoreResponse<SearchResult<Application>> getApplications(AuthToken token, ApplicationQuery query) {
        SearchResult<Application> sr = applicationDAO.getSearchResultByQuery(query);
        return new CoreResponse<SearchResult<Application>>().success(sr);
    }

    @Override
    public CoreResponse<SearchResult<Server>> getServersWithAppsNames(AuthToken token, ServerQuery query) {

        SearchResult<ServerApplication> serverApplications = serverApplicationDAO.getSearchResultByQuery(query);
        if (serverApplications == null) {
            return errorSt(En_ResultStatus.GET_DATA_ERROR);
        }

        Map<Long, Server> servers = new HashMap<>();

        serverApplications.getResults().forEach(sa -> {
            Long serverId = sa.getServer().getId();
            Server server = servers.getOrDefault(serverId, sa.getServer());
            if (sa.getApplication() != null && HelperFunc.isNotEmpty(sa.getApplication().getName())) {
                server.addAppName(sa.getApplication().getName());
            }
            servers.put(serverId, server);
        });

        SearchResult<Server> sr = new SearchResult<>(new ArrayList<>(servers.values()));
        return new CoreResponse<SearchResult<Server>>().success(sr);
    }


    @Override
    public CoreResponse<List<EntityOption>> listPlatformsOptionList(AuthToken token, PlatformQuery query) {

        List<Platform> result = platformDAO.listByQuery(query);

        if (result == null) {
            return errorSt(En_ResultStatus.GET_DATA_ERROR);
        }

        List<EntityOption> options = result.stream()
                .map(p -> new EntityOption(p.getName(), p.getId()))
                .collect(Collectors.toList());

        return ok(options);
    }

    @Override
    public CoreResponse<List<EntityOption>> listServersOptionList(AuthToken token, ServerQuery query) {

        List<Server> result = serverDAO.listByQuery(query);

        if (result == null) {
            return errorSt(En_ResultStatus.GET_DATA_ERROR);
        }

        List<EntityOption> options = result.stream()
                .map(p -> new EntityOption(p.getName(), p.getId()))
                .collect(Collectors.toList());

        return ok(options);
    }


    @Override
    public CoreResponse<Platform> getPlatform(AuthToken token, long id) {

        Platform result = platformDAO.get(id);

        if (result == null) {
            return errorSt(En_ResultStatus.GET_DATA_ERROR);
        }

        jdbcManyRelationsHelper.fill(result, "attachments");

        return new CoreResponse<Platform>().success(result);
    }

    @Override
    public CoreResponse<Server> getServer(AuthToken token, long id) {

        Server result = serverDAO.get(id);

        if (result == null) {
            return errorSt(En_ResultStatus.GET_DATA_ERROR);
        }

        return new CoreResponse<Server>().success(result);
    }

    @Override
    public CoreResponse<Application> getApplication(AuthToken token, long id) {

        Application result = applicationDAO.get(id);

        if (result == null) {
            return errorSt(En_ResultStatus.GET_DATA_ERROR);
        }

        return new CoreResponse<Application>().success(result);
    }


    @Override
    @Transactional
    public CoreResponse<Platform> createPlatform(AuthToken token, Platform platform) {

        Long id = platformDAO.persist(platform);

        if (id == null) {
            throw new ResultStatusException(En_ResultStatus.NOT_CREATED);
        }

        CaseObject caseObject = makePlatformCaseObject(id, platform.getName());
        Long caseId = caseObjectDAO.persist(caseObject);
        if (caseId == null) {
            throw new ResultStatusException(En_ResultStatus.NOT_CREATED);
        }

        platform.setCaseId(caseId);
        boolean isCaseIdSet = platformDAO.partialMerge(platform, "case_id");
        if (!isCaseIdSet) {
            throw new ResultStatusException(En_ResultStatus.NOT_CREATED);
        }

        if (CollectionUtils.isNotEmpty(platform.getAttachments())) {
            caseAttachmentDAO.persistBatch(
                    platform.getAttachments()
                            .stream()
                            .map(attachment -> new CaseAttachment(platform.getCaseId(), attachment.getId()))
                            .collect(Collectors.toList())
            );
        }

        Platform result = platformDAO.get(id);
        if (result == null) {
            throw new ResultStatusException(En_ResultStatus.INTERNAL_ERROR);
        }

        return new CoreResponse<Platform>().success(result);
    }

    @Override
    public CoreResponse<Server> createServer(AuthToken token, Server server) {

        Long id = serverDAO.persist(server);

        if (id == null) {
            return errorSt(En_ResultStatus.NOT_CREATED);
        }

        Server result = serverDAO.get(id);

        if (result == null) {
            return errorSt(En_ResultStatus.INTERNAL_ERROR);
        }

        return new CoreResponse<Server>().success(result);
    }

    @Override
    public CoreResponse<Server> createServerAndCloneApps(AuthToken token, Server server, Long serverIdOfAppsToBeCloned) {

        CoreResponse<Server> response = createServer(token, server);

        if (response.isOk() && response.getData() != null) {
            cloneApplicationsForServer(response.getData().getId(), serverIdOfAppsToBeCloned);
        }

        return response;
    }

    @Override
    public CoreResponse<Application> createApplication(AuthToken token, Application application) {

        Long id = applicationDAO.persist(application);

        if (id == null) {
            return errorSt(En_ResultStatus.NOT_CREATED);
        }

        Application result = applicationDAO.get(id);

        if (result == null) {
            return errorSt(En_ResultStatus.INTERNAL_ERROR);
        }

        return new CoreResponse<Application>().success(result);
    }


    @Override
    public CoreResponse<Platform> updatePlatform(AuthToken token, Platform platform) {

        boolean status = platformDAO.merge(platform);

        if (!status) {
            return errorSt(En_ResultStatus.NOT_UPDATED);
        }

        Platform result = platformDAO.get(platform.getId());

        if (result == null) {
            return errorSt(En_ResultStatus.INTERNAL_ERROR);
        }

        return new CoreResponse<Platform>().success(result);
    }

    @Override
    public CoreResponse<Server> updateServer(AuthToken token, Server server) {

        boolean status = serverDAO.merge(server);

        if (!status) {
            return errorSt(En_ResultStatus.NOT_UPDATED);
        }

        Server result = serverDAO.get(server.getId());

        if (result == null) {
            return errorSt(En_ResultStatus.INTERNAL_ERROR);
        }

        return new CoreResponse<Server>().success(result);
    }

    @Override
    public CoreResponse<Application> updateApplication(AuthToken token, Application application) {

        boolean status = applicationDAO.merge(application);

        if (!status) {
            return errorSt(En_ResultStatus.NOT_UPDATED);
        }

        Application result = applicationDAO.get(application.getId());

        if (result == null) {
            return errorSt(En_ResultStatus.INTERNAL_ERROR);
        }

        return new CoreResponse<Application>().success(result);
    }


    @Override
    public CoreResponse<Boolean> removePlatform(AuthToken token, long id) {

        boolean result = platformDAO.removeByKey(id);

        return new CoreResponse<Boolean>().success(result);
    }

    @Override
    public CoreResponse<Boolean> removeServer(AuthToken token, long id) {

        boolean result = serverDAO.removeByKey(id);

        return new CoreResponse<Boolean>().success(result);
    }

    @Override
    public CoreResponse<Boolean> removeApplication(AuthToken token, long id) {

        boolean result = applicationDAO.removeByKey(id);

        return new CoreResponse<Boolean>().success(result);
    }


    private void cloneApplicationsForServer(Long serverId, Long serverIdOfAppsToBeCloned) {
        if (serverIdOfAppsToBeCloned == null || serverId == null) {
            return;
        }

        List<Application> applications = applicationDAO.listByQuery(ApplicationQuery.forServerId(serverIdOfAppsToBeCloned));

        if (applications == null || applications.size() == 0) {
            return;
        }

        applications.forEach(app -> {
            app.setId(null);
            app.setServerId(serverId);
        });

        applicationDAO.persistBatch(applications);
    }

    private CaseObject makePlatformCaseObject(Long platformId, String name) {
        CaseObject caseObject = new CaseObject();
        caseObject.setCaseType(En_CaseType.SF_PLATFORM);
        caseObject.setCaseNumber(platformId);
        caseObject.setCreated(new Date());
        caseObject.setName(name);
        caseObject.setState(En_CaseState.CREATED);
        return caseObject;
    }

    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;
    @Autowired
    PlatformDAO platformDAO;
    @Autowired
    ServerDAO serverDAO;
    @Autowired
    ApplicationDAO applicationDAO;
    @Autowired
    ServerApplicationDAO serverApplicationDAO;
    @Autowired
    CaseObjectDAO caseObjectDAO;
    @Autowired
    CaseAttachmentDAO caseAttachmentDAO;
}
