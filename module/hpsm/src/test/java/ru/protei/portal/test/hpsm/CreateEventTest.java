package ru.protei.portal.test.hpsm;

import org.junit.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.core.service.CaseControlService;
import ru.protei.portal.hpsm.api.HpsmStatus;
import ru.protei.portal.hpsm.logic.HpsmEvent;
import ru.protei.portal.hpsm.logic.HpsmMainEventHandler;
import ru.protei.portal.hpsm.utils.HpsmTestUtils;
import ru.protei.portal.hpsm.utils.VirtualMailSendChannel;
import ru.protei.portal.test.hpsm.config.HpsmTestConfiguration;

import javax.mail.internet.MimeMessage;

/**
 * Created by Mike on 01.05.2017.
 */
public class CreateEventTest {
    public static final String HPSM_TEST_CASE_ID1 = "hpsm-create-test-1";
    static ApplicationContext ctx;

    @BeforeClass
    public static void init () {
        ctx = new AnnotationConfigApplicationContext(HpsmTestConfiguration.class);
    }

    @Test
    public void test001 () throws Exception {

        VirtualMailSendChannel backChannel = new VirtualMailSendChannel();

        HpsmMainEventHandler handler = ctx.getBean(HpsmMainEventHandler.class);
        handler.setSendChannel(backChannel);

        HpsmTestUtils testUtils = ctx.getBean(HpsmTestUtils.class);

        boolean result = handler.handle(testUtils.createNewRequest(HPSM_TEST_CASE_ID1));

        Assert.assertTrue(result);

        MimeMessage responseMail = backChannel.get();

        Assert.assertNotNull(responseMail);

        HpsmEvent responseEvent = testUtils.parseEvent(responseMail);

        Assert.assertNotNull(responseEvent);

        System.out.println(responseEvent.getMailBodyText());

        Assert.assertEquals(HpsmStatus.REGISTERED, responseEvent.getSubject().getStatus());

        Assert.assertNotNull(responseEvent.getHpsmMessage());

        System.out.println(responseEvent.getSubject());
    }


    @Before
    public void beforeTest () {
        cleanup();
    }

    @After
    public void cleanup () {
        ctx.getBean(CaseControlService.class).deleteByExtAppId (HPSM_TEST_CASE_ID1);
    }
}
