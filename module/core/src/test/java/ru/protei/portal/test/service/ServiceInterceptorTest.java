package ru.protei.portal.test.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.MainTestsConfiguration;
import ru.protei.portal.config.ServiceInterceptorConfiguration;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.service.SmokeyService;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

/**
 * Created by Mike on 06.11.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class, JdbcConfigurationContext.class, MainTestsConfiguration.class, ServiceInterceptorConfiguration.class})
public class ServiceInterceptorTest {

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
