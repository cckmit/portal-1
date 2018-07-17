package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.ApplicationDAO;
import ru.protei.portal.core.model.dao.PlatformDAO;
import ru.protei.portal.core.model.dao.ServerDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.Application;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.core.model.ent.Server;
import ru.protei.portal.core.model.query.ApplicationQuery;
import ru.protei.portal.core.model.query.PlatformQuery;
import ru.protei.portal.core.model.query.ServerQuery;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.List;

public class SiteFolderServiceImpl implements SiteFolderService {

    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Autowired
    PlatformDAO platformDAO;

    @Autowired
    ServerDAO serverDAO;

    @Autowired
    ApplicationDAO applicationDAO;

    @Override
    public CoreResponse<Long> countPlatforms(AuthToken token, PlatformQuery query) {

        Long count = platformDAO.count(query);

        if (count == null) {
            return new CoreResponse<Long>().error(En_ResultStatus.GET_DATA_ERROR, 0L);
        }

        return new CoreResponse<Long>().success(count);
    }

    @Override
    public CoreResponse<Long> countServers(AuthToken token, ServerQuery query) {

        Long count = serverDAO.count(query);

        if (count == null) {
            return new CoreResponse<Long>().error(En_ResultStatus.GET_DATA_ERROR, 0L);
        }

        return new CoreResponse<Long>().success(count);
    }

    @Override
    public CoreResponse<Long> countApplications(AuthToken token, ApplicationQuery query) {

        Long count = applicationDAO.count(query);

        if (count == null) {
            return new CoreResponse<Long>().error(En_ResultStatus.GET_DATA_ERROR, 0L);
        }

        return new CoreResponse<Long>().success(count);
    }


    @Override
    public CoreResponse<List<Platform>> listPlatforms(AuthToken token, PlatformQuery query) {

        List<Platform> result = platformDAO.listByQuery(query);

        if (result == null) {
            return new CoreResponse<List<Platform>>().error(En_ResultStatus.GET_DATA_ERROR, null);
        }

        jdbcManyRelationsHelper.fill(result, "servers");

        return new CoreResponse<List<Platform>>().success(result);
    }

    @Override
    public CoreResponse<List<Server>> listServers(AuthToken token, ServerQuery query) {

        List<Server> result = serverDAO.listByQuery(query);

        if (result == null) {
            return new CoreResponse<List<Server>>().error(En_ResultStatus.GET_DATA_ERROR, null);
        }

        jdbcManyRelationsHelper.fill(result, "applications");

        return new CoreResponse<List<Server>>().success(result);
    }

    @Override
    public CoreResponse<List<Application>> listApplications(AuthToken token, ApplicationQuery query) {

        List<Application> result = applicationDAO.listByQuery(query);

        if (result == null) {
            return new CoreResponse<List<Application>>().error(En_ResultStatus.GET_DATA_ERROR, null);
        }

        return new CoreResponse<List<Application>>().success(result);
    }


    @Override
    public CoreResponse<Platform> getPlatform(AuthToken token, long id) {

        Platform result = platformDAO.get(id);

        if (result == null) {
            return new CoreResponse<Platform>().error(En_ResultStatus.GET_DATA_ERROR, null);
        }

        jdbcManyRelationsHelper.fill(result, "servers");

        return new CoreResponse<Platform>().success(result);
    }

    @Override
    public CoreResponse<Server> getServer(AuthToken token, long id) {

        Server result = serverDAO.get(id);

        if (result == null) {
            return new CoreResponse<Server>().error(En_ResultStatus.GET_DATA_ERROR, null);
        }

        jdbcManyRelationsHelper.fill(result, "applications");

        return new CoreResponse<Server>().success(result);
    }

    @Override
    public CoreResponse<Application> getApplication(AuthToken token, long id) {

        Application result = applicationDAO.get(id);

        if (result == null) {
            return new CoreResponse<Application>().error(En_ResultStatus.GET_DATA_ERROR, null);
        }

        return new CoreResponse<Application>().success(result);
    }


    @Override
    public CoreResponse<Platform> createPlatform(AuthToken token, Platform platform) {

        Long id = platformDAO.persist(platform);

        if (id == null) {
            return new CoreResponse<Platform>().error(En_ResultStatus.INTERNAL_ERROR, null);
        }

        Platform result = platformDAO.get(id);

        if (result == null) {
            return new CoreResponse<Platform>().error(En_ResultStatus.INTERNAL_ERROR, null);
        }

        return new CoreResponse<Platform>().success(result);
    }

    @Override
    public CoreResponse<Server> createServer(AuthToken token, Server server) {

        Long id = serverDAO.persist(server);

        if (id == null) {
            return new CoreResponse<Server>().error(En_ResultStatus.INTERNAL_ERROR, null);
        }

        Server result = serverDAO.get(id);

        if (result == null) {
            return new CoreResponse<Server>().error(En_ResultStatus.INTERNAL_ERROR, null);
        }

        return new CoreResponse<Server>().success(result);
    }

    @Override
    public CoreResponse<Application> createApplication(AuthToken token, Application application) {

        Long id = applicationDAO.persist(application);

        if (id == null) {
            return new CoreResponse<Application>().error(En_ResultStatus.INTERNAL_ERROR, null);
        }

        Application result = applicationDAO.get(id);

        if (result == null) {
            return new CoreResponse<Application>().error(En_ResultStatus.INTERNAL_ERROR, null);
        }

        return new CoreResponse<Application>().success(result);
    }


    @Override
    public CoreResponse<Platform> updatePlatform(AuthToken token, Platform platform) {

        boolean status = platformDAO.merge(platform);

        if (!status) {
            return new CoreResponse<Platform>().error(En_ResultStatus.INTERNAL_ERROR, null);
        }

        Platform result = platformDAO.get(platform.getId());

        if (result == null) {
            return new CoreResponse<Platform>().error(En_ResultStatus.INTERNAL_ERROR, null);
        }

        return new CoreResponse<Platform>().success(result);
    }

    @Override
    public CoreResponse<Server> updateServer(AuthToken token, Server server) {

        boolean status = serverDAO.merge(server);

        if (!status) {
            return new CoreResponse<Server>().error(En_ResultStatus.INTERNAL_ERROR, null);
        }

        Server result = serverDAO.get(server.getId());

        if (result == null) {
            return new CoreResponse<Server>().error(En_ResultStatus.INTERNAL_ERROR, null);
        }

        return new CoreResponse<Server>().success(result);
    }

    @Override
    public CoreResponse<Application> updateApplication(AuthToken token, Application application) {

        boolean status = applicationDAO.merge(application);

        if (!status) {
            return new CoreResponse<Application>().error(En_ResultStatus.INTERNAL_ERROR, null);
        }

        Application result = applicationDAO.get(application.getId());

        if (result == null) {
            return new CoreResponse<Application>().error(En_ResultStatus.INTERNAL_ERROR, null);
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
}
