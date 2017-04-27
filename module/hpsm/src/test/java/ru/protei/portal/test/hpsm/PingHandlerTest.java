package ru.protei.portal.test.hpsm;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.hpsm.HpsmConfiguration;
import ru.protei.portal.hpsm.api.MailMessageFactory;
import ru.protei.portal.hpsm.handler.HpsmPingCommandHandler;
import ru.protei.portal.hpsm.struct.HpsmPingCmd;
import ru.protei.portal.hpsm.struct.HpsmSetup;
import ru.protei.portal.hpsm.utils.HpsmUtils;
import ru.protei.portal.hpsm.utils.VirtualMailSendChannel;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import javax.mail.internet.MimeMessage;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by michael on 25.04.17.
 */
public class PingHandlerTest {

    static ApplicationContext ctx;

    @BeforeClass
    public static void init () {
        ctx = new AnnotationConfigApplicationContext(CoreConfigurationContext.class, JdbcConfigurationContext.class, HpsmConfiguration.class);
    }

    @Test
    public void test001 () throws Exception {

        VirtualMailSendChannel backChannel = new VirtualMailSendChannel();

        MailMessageFactory messageFactory = ctx.getBean(MailMessageFactory.class);
        HpsmSetup setup = ctx.getBean(HpsmSetup.class);

        HpsmPingCommandHandler handler = ctx.getBean(HpsmPingCommandHandler.class);
        handler.setSendChannel(backChannel);

        Calendar cld = GregorianCalendar.getInstance();
        cld.setTime(new Date());
        cld.set(Calendar.MILLISECOND, 0);

        HpsmPingCmd requestCmd = new HpsmPingCmd(true, cld.getTime());

        Assert.assertTrue(handler.handle(handler.makeMessgae( setup.getSenderAddress(), requestCmd)));

        MimeMessage response = backChannel.get();

        Assert.assertNotNull(response);

        HpsmPingCmd responseCmd = HpsmPingCmd.parse(response.getSubject());

        Assert.assertNotNull(responseCmd);
        Assert.assertTrue(responseCmd.isResponse());

        Assert.assertNotNull(responseCmd.getRequestTime());
        Assert.assertNotNull(responseCmd.getResponseTime());

//        System.out.println (requestCmd.getRequestTime().getTime());
//        System.out.println(responseCmd.getRequestTime().getTime());

        Assert.assertEquals(requestCmd.getRequestTime(), responseCmd.getRequestTime());

//        handler.handle()
    }
}
