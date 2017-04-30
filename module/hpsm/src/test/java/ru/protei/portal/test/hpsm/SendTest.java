package ru.protei.portal.test.hpsm;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import ru.protei.portal.hpsm.*;
import ru.protei.portal.hpsm.api.HpsmStatus;
import ru.protei.portal.hpsm.struct.HpsmMessage;
import ru.protei.portal.hpsm.struct.HpsmMessageHeader;
import ru.protei.portal.hpsm.utils.EventMsgInputStreamSource;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;

/**
 * Created by michael on 19.04.17.
 */
public class SendTest {
    static ApplicationContext ctx;

    @BeforeClass
    public static void init () {
        ctx = new AnnotationConfigApplicationContext(HpsmConfiguration.class);
    }




    @Test
    public void testSend001 () {
        JavaMailSender sender = ctx.getBean(JavaMailSender.class);

        try {
            HpsmMessageHeader subject = new HpsmMessageHeader("RTS000111", "", HpsmStatus.NEW);

            HpsmMessage hpsmMessage = new HpsmMessage();
            hpsmMessage.setHpsmId(subject.getHpsmId());
            hpsmMessage.status(subject.getStatus());
            hpsmMessage.setOurId(subject.getOurId());

            hpsmMessage.setAddress("Russia, SPB");
            hpsmMessage.setContactPerson("Michael Zavedeev");
            hpsmMessage.setContactPersonEmail("zavedeev@protei.ru");
            hpsmMessage.setShortDescription("test-message");
            hpsmMessage.setDescription("Just for test purpose only");
            hpsmMessage.setMessage("This is a comment");
            hpsmMessage.setRegistrationTime(new Date());

            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setSubject(subject.toString());
            helper.setTo("crm_test@protei.ru");
            helper.setFrom("hpsm-agent@testcompany.com");

//            message.setText("Hello!");
            helper.setText("<h1>Hello</h1>", true);

            helper.addAttachment("rtts_hpsm.xml", ctx.getBean(EventMsgInputStreamSource.class).attach(hpsmMessage), "application/xml");
            helper.addAttachment("example.trace.log", new ClassPathResource("/samples/attachments/java.trace.log"), "plain/text");

            System.out.println(ctx.getBean(EventMsgInputStreamSource.class).attach(hpsmMessage).asString());

            //sender.send(message);

            ByteArrayOutputStream outBuffer = new ByteArrayOutputStream(32*1024);

            message.writeTo(outBuffer);


            MimeMessage test = new MimeMessage(null, new ByteArrayInputStream(outBuffer.toByteArray()));

            System.out.println(((MimeMultipart)test.getContent()).getBodyPart(1).getFileName());

           // message.getContent()

        } catch (Throwable e) {
            e.printStackTrace();
        }

    }
}
