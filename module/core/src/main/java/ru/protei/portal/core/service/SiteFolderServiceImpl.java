package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.exception.RollbackTransactionException;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.ApplicationQuery;
import ru.protei.portal.core.model.query.PlatformQuery;
import ru.protei.portal.core.model.query.ServerQuery;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PlatformOption;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class SiteFolderServiceImpl implements SiteFolderService {

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

    @Override
    public Result<SearchResult<Platform>> getPlatforms( AuthToken token, PlatformQuery query) {

        SearchResult<Platform> sr = platformDAO.getSearchResultByQuery(query);

        if ( CollectionUtils.isEmpty(sr.getResults())) {
            return ok(sr);
        }

        Map<Long, Long> map = serverDAO.countByPlatformIds(sr.getResults().stream()
                .map(Platform::getId)
                .collect(Collectors.toList()));

        sr.getResults().forEach(platform -> {
            Long count = map.getOrDefault(platform.getId(), 0L);
            platform.setServersCount(count);
        });

        return ok(sr);
    }

    @Override
    public Result<SearchResult<Server>> getServers( AuthToken token, ServerQuery query) {

        SearchResult<Server> sr = serverDAO.getSearchResultByQuery(query);

        if (CollectionUtils.isEmpty(sr.getResults())) {
            return ok(sr);
        }

        Map<Long, Long> map = applicationDAO.countByServerIds(sr.getResults().stream()
                .map(Server::getId)
                .collect(Collectors.toList()));

        sr.getResults().forEach(server -> {
            Long count = map.getOrDefault(server.getId(), 0L);
            server.setApplicationsCount(count);
        });

        return ok(sr);
    }

    @Override
    public Result<SearchResult<Application>> getApplications( AuthToken token, ApplicationQuery query) {
        SearchResult<Application> sr = applicationDAO.getSearchResultByQuery(query);
        return ok(sr);
    }

    @Override
    public Result<SearchResult<Server>> getServersWithAppsNames( AuthToken token, ServerQuery query) {

        SearchResult<ServerApplication> serverApplications = serverApplicationDAO.getSearchResultByQuery(query);
        if (serverApplications == null) {
            return error(En_ResultStatus.GET_DATA_ERROR);
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
        return ok(sr);
    }


    @Override
    public Result<List<PlatformOption>> listPlatformsOptionList(AuthToken token, PlatformQuery query) {

        List<Platform> result = platformDAO.listByQuery(query);

        if (result == null) {
            return error(En_ResultStatus.GET_DATA_ERROR);
        }

        List<PlatformOption> options = result.stream()
                .map(p -> new PlatformOption(p.getName(), p.getId(), p.getProjectId() == null ? p.getCompanyId() : caseObjectDAO.get(p.getProjectId()).getInitiatorCompanyId()))
                .collect(Collectors.toList());

        return ok(options);
    }

    @Override
    public Result<List<EntityOption>> listServersOptionList( AuthToken token, ServerQuery query) {

        List<Server> result = serverDAO.listByQuery(query);

        if (result == null) {
            return error(En_ResultStatus.GET_DATA_ERROR);
        }

        List<EntityOption> options = result.stream()
                .map(p -> new EntityOption(p.getName(), p.getId()))
                .collect(Collectors.toList());

        return ok(options);
    }


    @Override
    public Result<Platform> getPlatform( AuthToken token, long id) {

        Platform result = platformDAO.get(id);

        if (result == null) {
            return error(En_ResultStatus.GET_DATA_ERROR);
        }

        jdbcManyRelationsHelper.fill(result, "attachments");

        return ok(result);
    }

    @Override
    public Result<Server> getServer( AuthToken token, long id) {

        Server result = serverDAO.get(id);

        if (result == null) {
            return error(En_ResultStatus.GET_DATA_ERROR);
        }

        return ok(result);
    }

    @Override
    public Result<Application> getApplication( AuthToken token, long id) {

        Application result = applicationDAO.get(id);

        if (result == null) {
            return error(En_ResultStatus.GET_DATA_ERROR);
        }

        return ok(result);
    }


    @Override
    @Transactional
    public Result<Platform> createPlatform( AuthToken token, Platform platform) {

        String params = platform.getParams();
        if (params != null && !isValid(params)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        Long id = platformDAO.persist(platform);

        if (id == null) {
            throw new RollbackTransactionException(En_ResultStatus.NOT_CREATED);
        }

        CaseObject caseObject = makePlatformCaseObject(id, platform.getName());
        caseObject.setCreatorId(token.getPersonId());
        Long caseId = caseObjectDAO.persist(caseObject);
        if (caseId == null) {
            throw new RollbackTransactionException(En_ResultStatus.NOT_CREATED);
        }

        platform.setCaseId(caseId);
        boolean isCaseIdSet = platformDAO.partialMerge(platform, "case_id");
        if (!isCaseIdSet) {
            throw new RollbackTransactionException(En_ResultStatus.NOT_CREATED);
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
            throw new RollbackTransactionException(En_ResultStatus.INTERNAL_ERROR);
        }

        return ok(result);
    }

    @Override
    @Transactional
    public Result<Server> createServer( AuthToken token, Server server) {

        String params = server.getParams();
        if (params != null && !isValid(params)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        Long id = serverDAO.persist(server);

        if (id == null) {
            return error(En_ResultStatus.NOT_CREATED);
        }

        return ok(serverDAO.get(id));
    }

    @Override
    @Transactional
    public Result<Server> createServerAndCloneApps( AuthToken token, Server server, Long serverIdOfAppsToBeCloned) {

        String params = server.getParams();
        if (params != null && !isValid(params)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        Result<Server> response = createServer(token, server);

        if (response.isOk() && response.getData() != null) {
            cloneApplicationsForServer(response.getData().getId(), serverIdOfAppsToBeCloned);
        }

        return response;
    }

    @Override
    @Transactional
    public Result<Application> createApplication( AuthToken token, Application application) {

        Long id = applicationDAO.persist(application);

        if (id == null) {
            return error(En_ResultStatus.NOT_CREATED);
        }

        return ok(applicationDAO.get(id));
    }

    @Override
    @Transactional
    public Result<Platform> updatePlatform(AuthToken token, Platform platform) {

        String params = platform.getParams();
        if (params != null && !isValid(params)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        Platform platformFromDb = platformDAO.get(platform.getId());

        if (platformFromDb == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Long oldCompanyId = platformFromDb.getCompanyId();
        if (oldCompanyId == null) {
            oldCompanyId = caseObjectDAO.get(platformFromDb.getProjectId()).getInitiatorCompanyId();
        }

        Long companyId = platform.getCompanyId();
        if (companyId == null) {
            companyId = caseObjectDAO.get(platform.getProjectId()).getInitiatorCompanyId();
        }

        if (!Objects.equals(oldCompanyId, companyId) && CollectionUtils.isNotEmpty(caseObjectDAO.getCaseNumbersByPlatformId(platform.getId()))) {
            return error(En_ResultStatus.NOT_ALLOWED_CHANGE_PLATFORM_COMPANY);
        }

        boolean status = platformDAO.merge(platform);

        if (!status) {
            return error(En_ResultStatus.NOT_UPDATED);
        }

        CaseObject caseObject = new CaseObject();
        caseObject.setId(platform.getCaseId());
        caseObject.setName(platform.getName());

        boolean caseStatus = caseObjectDAO.partialMerge(caseObject, "CASE_NAME");
        if (!caseStatus) {
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED);
        }

        return ok(platformDAO.get(platform.getId()));
    }

    @Override
    @Transactional
    public Result<Server> updateServer( AuthToken token, Server server) {

        String params = server.getParams();
        if (params != null && !isValid(params)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        boolean status = serverDAO.merge(server);

        if (!status) {
            return error(En_ResultStatus.NOT_UPDATED);
        }

        return ok(serverDAO.get(server.getId()));
    }

    @Override
    @Transactional
    public Result<Application> updateApplication( AuthToken token, Application application) {

        boolean status = applicationDAO.merge(application);

        if (!status) {
            return error(En_ResultStatus.NOT_UPDATED);
        }

        return ok(applicationDAO.get(application.getId()));
    }


    @Override
    @Transactional
    public Result<Long> removePlatform(AuthToken token, long id) {

        Platform platform = platformDAO.get(id);

        if (platform == null){
            return error(En_ResultStatus.NOT_FOUND);
        }

        CaseObject caseObject = new CaseObject();
        caseObject.setId(platform.getCaseId());
        caseObject.setDeleted(true);

        boolean caseObjectMergeResult = caseObjectDAO.partialMerge(caseObject, "deleted");
        boolean removePlatformResult = platformDAO.removeByKey(id);

        if (!removePlatformResult) {
            throw new RollbackTransactionException(En_ResultStatus.NOT_REMOVED,
                    "Platform was not removed. It could be removed earlier");
        }

        if (!caseObjectMergeResult) {
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED);
        }

        return ok(id);
    }

    @Override
    @Transactional
    public Result<Long> removeServer(AuthToken token, long id) {
        if (!serverDAO.removeByKey(id)) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        return ok(id);
    }

    @Override
    @Transactional
    public Result<Long> removeApplication(AuthToken token, long id) {
        if (!applicationDAO.removeByKey(id)) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        return ok(id);
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
        caseObject.setType(En_CaseType.SF_PLATFORM);
        caseObject.setCaseNumber(platformId);
        caseObject.setCreated(new Date());
        caseObject.setName(name);
        caseObject.setStateId(CrmConstants.State.CREATED);
        return caseObject;
    }

    private boolean isValid(String params) {
        return params.length() <= CrmConstants.Platform.PARAMETERS_MAX_LENGTH;
    }
}
