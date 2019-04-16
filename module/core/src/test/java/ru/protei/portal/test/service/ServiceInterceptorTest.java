package ru.protei.portal.test.service;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.config.DatabaseConfiguration;
import ru.protei.portal.config.MainTestsConfiguration;
import ru.protei.portal.config.ServiceInterceptorConfiguration;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.service.SmokeyService;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

/**
 * Created by Mike on 06.11.2016.
 */
public class ServiceInterceptorTest {

    static ApplicationContext ctx;

    @BeforeClass
    public static void init () {
        ctx = new AnnotationConfigApplicationContext(CoreConfigurationContext.class, JdbcConfigurationContext.class, MainTestsConfiguration.class, ServiceInterceptorConfiguration.class);
    }

    @Test
    public void testHandleWrongRequest() {
        SmokeyService service = ctx.getBean(SmokeyService.class);

        CoreResponse<Boolean> response = service.throwException();

        Assert.assertNotNull(response);
        Assert.assertTrue(response.isError());
        Assert.assertEquals(En_ResultStatus.INTERNAL_ERROR, response.getStatus());
    }
}
