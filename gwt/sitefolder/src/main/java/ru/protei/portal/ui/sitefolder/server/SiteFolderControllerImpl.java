package ru.protei.portal.ui.sitefolder.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.Application;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.core.model.ent.Server;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.model.query.ApplicationQuery;
import ru.protei.portal.core.model.query.PlatformQuery;
import ru.protei.portal.core.model.query.ServerQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.service.SiteFolderService;
import ru.protei.portal.ui.common.client.service.SiteFolderController;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service("SiteFolderController")
public class SiteFolderControllerImpl implements SiteFolderController {

    @Override
    public long getPlatformsCount(PlatformQuery query) throws RequestFailedException {

        log.debug("getPlatformsCount(): query={}", query);
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();
        CoreResponse<Long> result = siteFolderService.countPlatforms(descriptor.makeAuthToken(), query);
        return result.isOk() ? result.getData() : 0L;
    }

    @Override
    public long getServersCount(ServerQuery query) throws RequestFailedException {

        log.debug("getServersCount(): query={}", query);
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();
        CoreResponse<Long> result = siteFolderService.countServers(descriptor.makeAuthToken(), query);
        return result.isOk() ? result.getData() : 0L;
    }

    @Override
    public long getApplicationsCount(ApplicationQuery query) throws RequestFailedException {

        log.debug("getApplicationsCount(): query={}", query);
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();
        CoreResponse<Long> result = siteFolderService.countApplications(descriptor.makeAuthToken(), query);
        return result.isOk() ? result.getData() : 0L;
    }

    @Override
    public List<Platform> getPlatforms(PlatformQuery query) throws RequestFailedException {

        log.debug("getPlatforms(): query={}", query);
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();
        CoreResponse<List<Platform>> response = siteFolderService.listPlatforms(descriptor.makeAuthToken(), query);
        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public List<Server> getServers(ServerQuery query) throws RequestFailedException {

        log.debug("getServers(): query={}", query);
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();
        CoreResponse<List<Server>> response = siteFolderService.listServers(descriptor.makeAuthToken(), query);
        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public List<Application> getApplications(ApplicationQuery query) throws RequestFailedException {

        log.debug("getApplications(): query={}", query);
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();
        CoreResponse<List<Application>> response = siteFolderService.listApplications(descriptor.makeAuthToken(), query);
        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public List<EntityOption> getPlatformsOptionList(PlatformQuery query) throws RequestFailedException {

        log.debug("getPlatformsOptionList(): query={}", query);
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();
        CoreResponse<List<EntityOption>> response = siteFolderService.listPlatformsOptionList(descriptor.makeAuthToken(), query);
        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public List<EntityOption> getServersOptionList(ServerQuery query) throws RequestFailedException {

        log.debug("getServersOptionList(): query={}", query);
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();
        CoreResponse<List<EntityOption>> response = siteFolderService.listServersOptionList(descriptor.makeAuthToken(), query);
        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public Platform getPlatform(long id) throws RequestFailedException {

        log.debug("getPlatform(id={})", id);
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();
        CoreResponse<Platform> response = siteFolderService.getPlatform(descriptor.makeAuthToken(), id);
        log.debug("getPlatform(id={}): {}", id, response.isOk() ? "ok" : response.getStatus());
        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public Server getServer(long id) throws RequestFailedException {

        log.debug("getServer(id={})", id);
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();
        CoreResponse<Server> response = siteFolderService.getServer(descriptor.makeAuthToken(), id);
        log.debug("getServer(id={}): {}", id, response.isOk() ? "ok" : response.getStatus());
        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public Application getApplication(long id) throws RequestFailedException {

        log.debug("getApplication(id={})", id);
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();
        CoreResponse<Application> response = siteFolderService.getApplication(descriptor.makeAuthToken(), id);
        log.debug("getApplication(id={}): {}", id, response.isOk() ? "ok" : response.getStatus());
        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public Platform savePlatform(Platform platform) throws RequestFailedException {

        log.debug("savePlatform(): platform={}", platform);
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();
        CoreResponse<Platform> response;
        if (platform.getId() == null) {
            response = siteFolderService.createPlatform(descriptor.makeAuthToken(), platform);
        } else {
            response = siteFolderService.updatePlatform(descriptor.makeAuthToken(), platform);
        }
        log.debug("savePlatform(): {}", response.isOk() ? "ok" : response.getStatus());
        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public Server saveServer(Server server) throws RequestFailedException {

        log.debug("saveServer(): server={}", server);
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();
        CoreResponse<Server> response;
        if (server.getId() == null) {
            response = siteFolderService.createServer(descriptor.makeAuthToken(), server);
        } else {
            response = siteFolderService.updateServer(descriptor.makeAuthToken(), server);
        }
        log.debug("saveServer(): {}", response.isOk() ? "ok" : response.getStatus());
        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public Application saveApplication(Application application) throws RequestFailedException {

        log.debug("saveApplication(): application={}", application);
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();
        CoreResponse<Application> response;
        if (application.getId() == null) {
            response = siteFolderService.createApplication(descriptor.makeAuthToken(), application);
        } else {
            response = siteFolderService.updateApplication(descriptor.makeAuthToken(), application);
        }
        log.debug("saveApplication(): {}", response.isOk() ? "ok" : response.getStatus());
        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public boolean removePlatform(long id) throws RequestFailedException {

        log.debug("removePlatform(id={})", id);
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();
        CoreResponse<Boolean> response = siteFolderService.removePlatform(descriptor.makeAuthToken(), id);
        log.debug("removePlatform(id={}): {}", id, response.isOk() ? "ok" : response.getStatus());
        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public boolean removeServer(long id) throws RequestFailedException {

        log.debug("removeServer(id={})", id);
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();
        CoreResponse<Boolean> response = siteFolderService.removeServer(descriptor.makeAuthToken(), id);
        log.debug("removeServer(id={}): {}", id, response.isOk() ? "ok" : response.getStatus());
        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public boolean removeApplication(long id) throws RequestFailedException {

        log.debug("removeApplication(id={})", id);
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();
        CoreResponse<Boolean> response = siteFolderService.removeApplication(descriptor.makeAuthToken(), id);
        log.debug("removeApplication(id={}): {}", id, response.isOk() ? "ok" : response.getStatus());
        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    private UserSessionDescriptor getDescriptorAndCheckSession() throws RequestFailedException {
        UserSessionDescriptor descriptor = sessionService.getUserSessionDescriptor(httpServletRequest);
        if (descriptor == null) {
            throw new RequestFailedException(En_ResultStatus.SESSION_NOT_FOUND);
        }
        return descriptor;
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
