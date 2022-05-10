package ru.protei.portal.test.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.IntegrationTestsConfiguration;
import ru.protei.portal.core.model.dao.PlatformDAO;
import ru.protei.portal.core.model.dao.ServerDAO;
import ru.protei.portal.core.model.dao.ServerGroupDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.core.model.ent.Server;
import ru.protei.portal.core.model.ent.ServerGroup;
import ru.protei.portal.core.service.SiteFolderService;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.embeddeddb.DatabaseConfiguration;
import ru.protei.portal.mock.AuthServiceMock;
import ru.protei.sn.remote_services.configuration.RemoteServiceFactory;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.http.HttpConfigurationContext;
import ru.protei.winter.http.client.factory.HttpClientFactory;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        CoreConfigurationContext.class,
        JdbcConfigurationContext.class,
        DatabaseConfiguration.class,
        IntegrationTestsConfiguration.class,
        RemoteServiceFactory.class, HttpClientFactory.class, HttpConfigurationContext.class
})
public class SiteFolderServiceImplTest extends BaseServiceTest {
    @Autowired
    SiteFolderService siteFolderService;
    @Autowired
    PlatformDAO platformDAO;
    @Autowired
    ServerDAO serverDAO;
    @Autowired
    ServerGroupDAO serverGroupDAO;

    @Test
    @Transactional
    public void createServerGroupWithoutPlatform() {
        ServerGroup serverGroup = new ServerGroup();
        serverGroup.setName("test_server_group");

        Result<ServerGroup> result
                = siteFolderService.createServerGroup(getAuthToken(), serverGroup);

        Assert.assertEquals(result.getStatus(), En_ResultStatus.INCORRECT_PARAMS);
    }

    @Test
    @Transactional
    public void createServerInServerGroupFromAnotherPlatform() {
        Platform platform = new Platform();
        platform.setName("test_platform");

        Long platformId = platformDAO.persist(platform);

        Assert.assertNotNull("Platform must be created. Platform=" + platform, platformId);

        Platform platform2 = new Platform();
        platform2.setName("test_platform2");

        Long platformId2 = platformDAO.persist(platform2);

        Assert.assertNotNull("Platform must be created. Platform=" + platform2, platformId2);

        ServerGroup serverGroup = createServerGroup(platformId, "test_server_group");

        Long serverGroupId = serverGroupDAO.persist(serverGroup);

        Server server = createServer(platformId2, "test_server");
        server.setServerGroupId(serverGroupId);

        Result<Server> result = siteFolderService.createServer(getAuthToken(), server);

        Assert.assertEquals(
                "The server and its group cannot have different platforms. " +
                        "Result status must be VALIDATION_ERROR" +
                        "Server=" + server + ", ServerGroup=" + serverGroup,
                result.getStatus(),
                En_ResultStatus.VALIDATION_ERROR
        );
    }

    @Test
    @Transactional
    public void createTwoServerGroupsWithSameNameInOnePlatform() {
        Platform platform = new Platform();
        platform.setName("test_platform");

        Long platformId = platformDAO.persist(platform);

        Assert.assertNotNull("Platform must be created. Platform=" + platform, platformId);

        String sameName = "test_server_group";

        ServerGroup serverGroup = createServerGroup(platformId, sameName);
        ServerGroup serverGroup2 = createServerGroup(platformId, sameName);

        Result<ServerGroup> result
                = siteFolderService.createServerGroup(getAuthToken(), serverGroup);

        Result<ServerGroup> result2
                = siteFolderService.createServerGroup(getAuthToken(), serverGroup2);

        Assert.assertEquals("Server group must be created. Server group=" + serverGroup,
                result.getStatus(),
                En_ResultStatus.OK);

        Assert.assertEquals(
                "Second server group with the same name cannot be saved. " +
                        "Result status must be ALREADY_EXIST. Server group=" + serverGroup2,
                result2.getStatus(),
                En_ResultStatus.ALREADY_EXIST
        );
    }

    @Test
    @Transactional
    public void createTwoServerGroupsWithSameNameInDifferentPlatforms() {
        Platform platform = new Platform();
        platform.setName("test_platform");

        Long platformId = platformDAO.persist(platform);

        Assert.assertNotNull("Platform must be created. Platform=" + platform, platformId);

        Platform platform2 = new Platform();
        platform2.setName("test_platform2");

        Long platformId2 = platformDAO.persist(platform2);

        Assert.assertNotNull("Platform must be created. Platform=" + platform2, platformId2);

        String sameName = "test_server_group";

        ServerGroup serverGroup = createServerGroup(platformId, sameName);
        ServerGroup serverGroup2 = createServerGroup(platformId2, sameName);

        Result<ServerGroup> result
                = siteFolderService.createServerGroup(getAuthToken(), serverGroup);

        Result<ServerGroup> result2
                = siteFolderService.createServerGroup(getAuthToken(), serverGroup2);

        Assert.assertEquals("Server group must be created. Server group=" + serverGroup,
                result.getStatus(),
                En_ResultStatus.OK);

        Assert.assertEquals("Server groups with the same name and in different platforms can exist. " +
                        "Server group 1=" + serverGroup + ", Server group 2=" + serverGroup2,
                result2.getStatus(),
                En_ResultStatus.OK
        );
    }

    @Test
    @Transactional
    public void moveServerToServerGroupFromAnotherPlatform() {
        Platform platform = new Platform();
        platform.setName("test_platform");

        Long platformId = platformDAO.persist(platform);

        Assert.assertNotNull("Platform must be created. Platform=" + platform, platformId);

        Platform platform2 = new Platform();
        platform2.setName("test_platform2");

        Long platformId2 = platformDAO.persist(platform2);

        Assert.assertNotNull("Platform must be created. Platform=" + platform2, platformId2);

        ServerGroup serverGroup = createServerGroup(platformId, "test_server_group");
        ServerGroup serverGroup2 = createServerGroup(platformId2, "test_server_group2");

        Long serverGroupId = serverGroupDAO.persist(serverGroup);
        Long serverGroupId2 = serverGroupDAO.persist(serverGroup2);

        Server server = createServer(platformId, "test_server");
        server.setServerGroupId(serverGroupId);

        Result<Server> result = siteFolderService.createServer(getAuthToken(), server);

        Assert.assertEquals(result.getStatus(), En_ResultStatus.OK);

        Server savedServer = result.getData();
        savedServer.setServerGroupId(serverGroupId2);

        result = siteFolderService.updateServer(getAuthToken(), savedServer);

        Assert.assertEquals(
                "The server and its group cannot have different platforms. " +
                        "Result status must be VALIDATION_ERROR. " +
                        "Server=" + server + ", ServerGroup=" + serverGroup,
                result.getStatus(),
                En_ResultStatus.VALIDATION_ERROR
        );
    }
}
