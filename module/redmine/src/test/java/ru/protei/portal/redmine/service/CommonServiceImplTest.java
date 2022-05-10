package ru.protei.portal.redmine.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.redmine.config.PortalConfigTestConfiguration;
import ru.protei.portal.redmine.config.RedmineTestConfiguration;
import ru.protei.portal.redmine.config.RedmineTestConfigurationMockDao;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.sn.remote_services.configuration.RemoteServiceFactory;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.http.HttpConfigurationContext;
import ru.protei.winter.http.client.factory.HttpClientFactory;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        PortalConfigTestConfiguration.class,
        RedmineTestConfiguration.class,
        RedmineTestConfigurationMockDao.class
})
public class CommonServiceImplTest {

    @Autowired
    CommonService commonService;

    @Test
    public void saveCase() {
        assertNotNull( "Expected case object id",
                commonService.saveCase( createCase() ).getData() );

    }

    private CaseObject createCase() {
        return new CaseObject();
    }

}