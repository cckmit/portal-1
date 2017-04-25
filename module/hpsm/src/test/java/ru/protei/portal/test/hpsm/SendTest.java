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
import ru.protei.portal.hpsm.struct.EventMsg;
import ru.protei.portal.hpsm.struct.EventSubject;
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
            EventSubject subject = new EventSubject("RTS000111", "", HpsmStatus.NEW);

            EventMsg eventMsg = new EventMsg();
            eventMsg.setHpsmId(subject.getHpsmId());
            eventMsg.status(subject.getStatus());
            eventMsg.setOurId(subject.getOurId());

            eventMsg.setAddress("Russia, SPB");
            eventMsg.setContactPerson("Michael Zavedeev");
            eventMsg.setContactPersonEmail("zavedeev@protei.ru");
            eventMsg.setSubject("test-message");
            eventMsg.setDescription("Just for test purpose only");
            eventMsg.setMessage("This is a comment");
            eventMsg.setRegistrationTime(new Date());

            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setSubject(subject.toString());
            helper.setTo("crm_test@protei.ru");
            helper.setFrom("hpsm-agent@testcompany.com");

//            message.setText("Hello!");
            helper.setText("<h1>Hello</h1>", true);

            helper.addAttachment("rtts_hpsm.xml", ctx.getBean(EventMsgInputStreamSource.class).attach(eventMsg), "application/xml");
            helper.addAttachment("example.trace.log", new ClassPathResource("/samples/attachments/java.trace.log"), "plain/text");

            System.out.println(ctx.getBean(EventMsgInputStreamSource.class).attach(eventMsg).asString());

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
