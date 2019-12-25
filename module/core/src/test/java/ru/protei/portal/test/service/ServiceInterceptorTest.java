package ru.protei.portal.test.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.DaoMockTestConfiguration;
import ru.protei.portal.config.PortalConfigTestConfiguration;
import ru.protei.portal.config.ServiceTestsConfiguration;
import ru.protei.portal.config.TestEventConfiguration;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.service.SmokeyService;

/**
 * Created by Mike on 06.11.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        PortalConfigTestConfiguration.class,
        ServiceTestsConfiguration.class,
        DaoMockTestConfiguration.class,
        ServiceInterceptorTest.ContextConfiguration.class,
        TestEventConfiguration.class
})
public class ServiceInterceptorTest {

    @Configuration
    static class ContextConfiguration {
        @Bean
        public SmokeyService getSmokeyService() {
            return new SmokeyService();
        }
    }

    @Test
    public void testHandleWrongRequest() {

        Result<Boolean> response = smokeyService.throwException();

        Assert.assertNotNull(response);
        Assert.assertTrue(response.isError());
        Assert.assertEquals(En_ResultStatus.INTERNAL_ERROR, response.getStatus());
    }

    @Autowired
    SmokeyService smokeyService;
}
