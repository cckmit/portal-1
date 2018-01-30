package ru.protei.portal.test.hpsm.handlers.inbound;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.ExternalCaseAppDAO;
import ru.protei.portal.core.service.CaseControlService;
import ru.protei.portal.hpsm.logic.InboundMainMessageHandler;
import ru.protei.portal.hpsm.utils.HpsmTestUtils;
import ru.protei.portal.hpsm.utils.TestServiceInstance;
import ru.protei.portal.test.hpsm.config.HpsmTestConfiguration;

public class CaseHandlersTest {
    public static final String HPSM_TEST_CASE_ID2 = "hpsm-update-test-1";
    static ApplicationContext ctx;

    @BeforeClass
    public static void init () {
        ctx = new AnnotationConfigApplicationContext(HpsmTestConfiguration.class);
    }

    @Test
    public void testForUpdateHandler () throws Exception {

        TestServiceInstance testServiceInstance = ctx.getBean(TestServiceInstance.class);
        InboundMainMessageHandler handler = ctx.getBean(InboundMainMessageHandler.class);
        CaseObjectDAO caseObjectDAO = ctx.getBean(CaseObjectDAO.class);
        ExternalCaseAppDAO externalCaseAppDAO = ctx.getBean(ExternalCaseAppDAO.class);

        HpsmTestUtils testUtils = ctx.getBean(HpsmTestUtils.class);

        boolean result = handler.handle(testUtils.createNewRequest(HPSM_TEST_CASE_ID2), testServiceInstance);
    }


    @Before
    public void beforeTest () {
        cleanup();
    }

    @After
    public void cleanup () {
        ctx.getBean(CaseControlService.class).deleteByExtAppId (HPSM_TEST_CASE_ID2);
    }
}
