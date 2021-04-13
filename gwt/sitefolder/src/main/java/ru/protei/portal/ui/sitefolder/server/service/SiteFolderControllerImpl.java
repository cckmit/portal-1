package ru.protei.portal.ui.sitefolder.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.ApplicationQuery;
import ru.protei.portal.core.model.query.PlatformQuery;
import ru.protei.portal.core.model.query.ServerQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PlatformOption;
import ru.protei.portal.core.service.SiteFolderService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.SiteFolderController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service("SiteFolderController")
public class SiteFolderControllerImpl implements SiteFolderController {

    @Override
    public SearchResult<Platform> getPlatforms(PlatformQuery query) throws RequestFailedException {
        log.info("getPlatforms(): query={}", query);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(siteFolderService.getPlatforms(token, query));
    }

    @Override
    public SearchResult<Server> getServers(ServerQuery query) throws RequestFailedException {
        log.info("getServers(): query={}", query);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(siteFolderService.getServers(token, query));
    }

    @Override
    public SearchResult<Application> getApplications(ApplicationQuery query) throws RequestFailedException {
        log.info("getApplications(): query={}", query);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(siteFolderService.getApplications(token, query));
    }

    @Override
    public SearchResult<Server> getServersWithAppsNames(ServerQuery query) throws RequestFailedException {
        log.info("getServersWithAppsNames(): query={}", query);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(siteFolderService.getServersWithAppsNames(token, query));
    }


    @Override
    public List<PlatformOption> getPlatformsOptionList(PlatformQuery query) throws RequestFailedException {

        log.info("getPlatformsOptionList(): query={}", query);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        Result<List<PlatformOption>> response = siteFolderService.listPlatformsOptionList(token, query);
        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public List<EntityOption> getServersOptionList(ServerQuery query) throws RequestFailedException {

        log.info("getServersOptionList(): query={}", query);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        Result<List<EntityOption>> response = siteFolderService.listServersOptionList(token, query);
        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public Platform getPlatform(long id) throws RequestFailedException {

        log.info("getPlatform(id={})", id);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        Result<Platform> response = siteFolderService.getPlatform(token, id);
        log.info("getPlatform(id={}): {}", id, response.isOk() ? "ok" : response.getStatus());
        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public Server getServer(long id) throws RequestFailedException {

        log.info("getServer(id={})", id);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        Result<Server> response = siteFolderService.getServer(token, id);
        log.info("getServer(id={}): {}", id, response.isOk() ? "ok" : response.getStatus());
        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public Application getApplication(long id) throws RequestFailedException {

        log.info("getApplication(id={})", id);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        Result<Application> response = siteFolderService.getApplication(token, id);
        log.info("getApplication(id={}): {}", id, response.isOk() ? "ok" : response.getStatus());
        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public Platform savePlatform(Platform platform) throws RequestFailedException {

        log.info("savePlatform(): platform={}", platform);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        Result<Platform> response;
        if (platform.getId() == null) {
            response = siteFolderService.createPlatform(token, platform);
        } else {
            response = siteFolderService.updatePlatform(token, platform);
        }
        log.info("savePlatform(): {}", response.isOk() ? "ok" : response.getStatus());
        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public Server saveServer(Server server, Long serverIdOfAppsToBeCloned) throws RequestFailedException {

        log.info("saveServer(): server={}", server);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        Result<Server> response;
        if (server.getId() == null) {
            if (serverIdOfAppsToBeCloned == null) {
                response = siteFolderService.createServer(token, server);
            } else {
                response = siteFolderService.createServerAndCloneApps(token, server, serverIdOfAppsToBeCloned);
            }
        } else {
            response = siteFolderService.updateServer(token, server);
        }
        log.info("saveServer(): {}", response.isOk() ? "ok" : response.getStatus());
        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public Application saveApplication(Application application) throws RequestFailedException {

        log.info("saveApplication(): application={}", application);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        Result<Application> response;
        if (application.getId() == null) {
            response = siteFolderService.createApplication(token, application);
        } else {
            response = siteFolderService.updateApplication(token, application);
        }
        log.info("saveApplication(): {}", response.isOk() ? "ok" : response.getStatus());
        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public Long removePlatform(long id) throws RequestFailedException {

        log.info("removePlatform(id={})", id);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        Result<Long> response = siteFolderService.removePlatform(token, id);
        log.info("removePlatform(id={}): {}", id, response.isOk() ? "ok" : response.getStatus());
        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public Long removeServer(long id) throws RequestFailedException {

        log.info("removeServer(id={})", id);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        Result<Long> response = siteFolderService.removeServer(token, id);
        log.info("removeServer(id={}): {}", id, response.isOk() ? "ok" : response.getStatus());
        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public Long removeApplication(long id) throws RequestFailedException {

        log.info("removeApplication(id={})", id);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        Result<Long> response = siteFolderService.removeApplication(token, id);
        log.info("removeApplication(id={}): {}", id, response.isOk() ? "ok" : response.getStatus());
        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Autowired
    SiteFolderService siteFolderService;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    HttpServletRequest request;

    private static final Logger log = LoggerFactory.getLogger(SiteFolderControllerImpl.class);
}
