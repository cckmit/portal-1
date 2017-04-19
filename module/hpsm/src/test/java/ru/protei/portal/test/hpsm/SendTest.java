package ru.protei.portal.test.hpsm;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import ru.protei.portal.hpsm.EventMsg;
import ru.protei.portal.hpsm.EventMsgInputStreamSource;
import ru.protei.portal.hpsm.HpsmConfiguration;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

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
            MimeMessage message = sender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setSubject("Hello from spring");

            helper.setTo("zavedeev@protei.ru");
            helper.setFrom("mail@mycompany.com");

//            message.setText("Hello!");
            helper.setText("<h1>Hello</h1>", true);


            EventMsg eventMsg = new EventMsg();
            eventMsg.setHpsmId("hpsm-11");
            eventMsg.setStatusText("opened");
            eventMsg.setOurId("crm-22");
            eventMsg.setAddress("Russia, SPB");
            eventMsg.setContactPerson("Michael Zavedeev");
            eventMsg.setContactPersonEmail("zavedeev@protei.ru");

            helper.addAttachment("rtts_hpsm.xml", ctx.getBean(EventMsgInputStreamSource.class).attach(eventMsg), "application/xml");


            System.out.println(ctx.getBean(EventMsgInputStreamSource.class).attach(eventMsg).asString());


           // sender.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }


    }
}
