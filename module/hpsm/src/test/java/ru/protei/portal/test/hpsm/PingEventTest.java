package ru.protei.portal.test.hpsm;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.hpsm.api.HpsmMessageFactory;
import ru.protei.portal.hpsm.logic.InboundPingMessageHandler;
import ru.protei.portal.hpsm.struct.HpsmPingMessage;
import ru.protei.portal.hpsm.utils.HpsmTestUtils;
import ru.protei.portal.hpsm.utils.TestServiceInstance;
import ru.protei.portal.test.hpsm.config.HpsmTestConfiguration;

import javax.mail.internet.MimeMessage;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by michael on 25.04.17.
 */
public class PingEventTest {

    static ApplicationContext ctx;

    @BeforeClass
    public static void init () {
        ctx = new AnnotationConfigApplicationContext(HpsmTestConfiguration.class);
    }

    @Test
    public void test001 () throws Exception {

        TestServiceInstance testServiceInstance = ctx.getBean(TestServiceInstance.class);
        InboundPingMessageHandler handler = ctx.getBean(InboundPingMessageHandler.class);
        HpsmMessageFactory messageFactory = ctx.getBean(HpsmMessageFactory.class);

        Calendar cld = GregorianCalendar.getInstance();
        cld.setTime(new Date());
        cld.set(Calendar.MILLISECOND, 0);

        HpsmPingMessage requestCmd = new HpsmPingMessage(true, cld.getTime());

        Assert.assertTrue(handler.handle(
                messageFactory.makePingMessgae(HpsmTestUtils.HPSM_MAIL_ADDRESS, HpsmTestUtils.SENDER_ADDRESS, requestCmd),
                testServiceInstance
        ));

        MimeMessage response = testServiceInstance.getSentMessage();

        Assert.assertNotNull(response);

        HpsmPingMessage responseCmd = HpsmPingMessage.parse(response.getSubject());

        Assert.assertNotNull(responseCmd);
        Assert.assertTrue(responseCmd.isResponse());

        Assert.assertNotNull(responseCmd.getRequestTime());
        Assert.assertNotNull(responseCmd.getResponseTime());

        Assert.assertEquals(requestCmd.getRequestTime(), responseCmd.getRequestTime());
    }
}
