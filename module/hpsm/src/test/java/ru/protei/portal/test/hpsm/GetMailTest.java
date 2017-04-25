package ru.protei.portal.test.hpsm;

import com.thoughtworks.xstream.XStream;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.Message;
import ru.protei.portal.hpsm.struct.EventMsg;
import ru.protei.portal.hpsm.HpsmConfiguration;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Created by michael on 20.04.17.
 */
public class GetMailTest {

    static ApplicationContext ctx;

    @BeforeClass
    public static void init () {
        ctx = new AnnotationConfigApplicationContext(HpsmConfiguration.class);
    }


    @Test
    public void test001 () {

        MessageSource mailReceiver = ctx.getBean(MessageSource.class);

        XStream xstream = ctx.getBean(XStream.class);

        try {
            Message<MimeMessage> msg = mailReceiver.receive();
            System.out.println(msg);
            System.out.println(msg.getHeaders());
            System.out.println(msg.getPayload());


            if (msg.getPayload().getContent() instanceof MimeMultipart) {
                System.out.println("Multupart message:");

                MimeMultipart mparts = (MimeMultipart) msg.getPayload().getContent();

                for (int i = 0; i < mparts.getCount(); i++) {
                    System.out.println("process part #" + i);

                    System.out.println(" Content type: " + mparts.getBodyPart(i).getContentType());
                    String fileName = mparts.getBodyPart(i).getFileName();
                    System.out.println(" File name: " + fileName);

                    if (fileName != null && fileName.equalsIgnoreCase("rtts_hpsm.xml")) {
                        System.out.println(" XML-data:");
//                        System.out.println(mparts.getBodyPart(i).getContent().toString());
                        //System.out.println(mparts.getBodyPart(i).getContent().toString());

                        EventMsg eventMsg = (EventMsg) xstream.fromXML(mparts.getBodyPart(i).getInputStream());

                        System.out.println(xstream.toXML(eventMsg));
                        //xstream.fr
                    }

                }
            }

//            if (msg.getPayload() instanceof MimeMultipart) {
//                MimeMultipart mp = (MimeMultipart)msg.getPayload();
//            }
//

                System.out.println(msg.getPayload().getSubject());
        }
        catch (Throwable e) {
            e.printStackTrace();
        }

    }
}
