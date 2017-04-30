package ru.protei.portal.test.hpsm;

import com.thoughtworks.xstream.XStream;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.mail.javamail.MimeMessageHelper;
import ru.protei.portal.hpsm.HpsmConfiguration;
import ru.protei.portal.hpsm.api.HpsmStatus;
import ru.protei.portal.hpsm.api.MailMessageFactory;
import ru.protei.portal.hpsm.handler.HpsmEvent;
import ru.protei.portal.hpsm.handler.HpsmMainEventHandler;
import ru.protei.portal.hpsm.struct.HpsmMessage;
import ru.protei.portal.hpsm.struct.HpsmMessageHeader;
import ru.protei.portal.hpsm.struct.HpsmSetup;
import ru.protei.portal.hpsm.utils.EventMsgInputStreamSource;
import ru.protei.portal.hpsm.utils.HpsmUtils;
import ru.protei.portal.hpsm.utils.VirtualMailSendChannel;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import javax.mail.internet.MimeMessage;

import static ru.protei.portal.hpsm.utils.HpsmUtils.RTTS_HPSM_XML;

/**
 * Created by Mike on 01.05.2017.
 */
public class CreateEventTest {
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
        XStream xstream = ctx.getBean(XStream.class);

        HpsmMainEventHandler handler = ctx.getBean(HpsmMainEventHandler.class);

        handler.setSendChannel(backChannel);

        MimeMessage mailMessage = messageFactory.createMailMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true);

        HpsmMessageHeader requestSubject = new HpsmMessageHeader("hpsm-test-1", null, HpsmStatus.NEW);
        HpsmMessage requestMessage = new HpsmMessage();
        requestMessage.setHpsmId(requestSubject.getHpsmId());
        requestMessage.setOurId(requestSubject.getOurId());
        requestMessage.status(requestSubject.getStatus());

        helper.setSubject(requestSubject.toString());
        helper.setTo(setup.getSenderAddress());
        helper.setFrom(setup.getHpsmMailAddress());
        helper.addAttachment(RTTS_HPSM_XML, new EventMsgInputStreamSource(xstream).attach(requestMessage), "application/xml");

        boolean result = handler.handle(mailMessage);

        Assert.assertTrue(result);

        MimeMessage responseMail = backChannel.get();

        Assert.assertNotNull(responseMail);

        HpsmEvent responseEvent = HpsmUtils.parseEvent(responseMail, xstream);

        Assert.assertNotNull(responseEvent);

        System.out.println(responseEvent.getMailBodyText());

    }
}
