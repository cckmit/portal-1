package ru.protei.portal.test.hpsm.handlers.inbound;

import org.junit.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.ExternalCaseAppDAO;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.ExternalCaseAppData;
import ru.protei.portal.core.service.CaseControlService;
import ru.protei.portal.hpsm.api.HpsmStatus;
import ru.protei.portal.hpsm.logic.HpsmEvent;
import ru.protei.portal.hpsm.logic.InboundMainMessageHandler;
import ru.protei.portal.hpsm.utils.HpsmTestUtils;
import ru.protei.portal.hpsm.utils.TestServiceInstance;
import ru.protei.portal.test.hpsm.config.HpsmTestConfiguration;

import javax.mail.internet.MimeMessage;

public class CaseHandlersTest {
    public static final String HPSM_TEST_CASE_ID2 = "hpsm-update-test-1";
    static ApplicationContext ctx;

    @BeforeClass
    public static void init () {
        ctx = new AnnotationConfigApplicationContext(HpsmTestConfiguration.class);
    }

    @Test
    public void test001 () throws Exception {

        TestServiceInstance testServiceInstance = ctx.getBean(TestServiceInstance.class);
        InboundMainMessageHandler handler = ctx.getBean(InboundMainMessageHandler.class);
        CaseObjectDAO caseObjectDAO = ctx.getBean(CaseObjectDAO.class);
        ExternalCaseAppDAO externalCaseAppDAO = ctx.getBean(ExternalCaseAppDAO.class);

        HpsmTestUtils testUtils = ctx.getBean(HpsmTestUtils.class);

        boolean result = handler.handle(testUtils.createNewRequest(HPSM_TEST_CASE_ID2), testServiceInstance);

        Assert.assertTrue(result);

        MimeMessage responseMail = testServiceInstance.getSentMessage();

        Assert.assertNotNull(responseMail);

        HpsmEvent responseEvent = testUtils.parseEvent(responseMail);

        Assert.assertNotNull(responseEvent);

        System.out.println(responseEvent.getMailBodyText());

        Assert.assertEquals(HpsmStatus.REGISTERED, responseEvent.getSubject().getStatus());

        Assert.assertNotNull(responseEvent.getHpsmMessage());

        System.out.println(responseEvent.getSubject());

        ExternalCaseAppData appData = externalCaseAppDAO.getByExternalAppId(HPSM_TEST_CASE_ID2);

        Assert.assertNotNull(appData);

        CaseObject resultCase = caseObjectDAO.get(appData.getId());

        Assert.assertNotNull(resultCase.getInitiator());
        Assert.assertNotNull(resultCase.getInitiatorCompany());
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
