package ru.protei.portal.hpsm.handler;

import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.protei.portal.hpsm.api.MailHandler;
import ru.protei.portal.hpsm.struct.EventMsg;
import ru.protei.portal.hpsm.struct.EventSubject;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.InputStream;

/**
 * Created by michael on 25.04.17.
 */
public class HpsmEventCommandHandler implements MailHandler {

    private static Logger logger = LoggerFactory.getLogger(HpsmEventCommandHandler.class);

    public static final String RTTS_HPSM_XML = "rtts_hpsm.xml";

    @Autowired
    @Qualifier("hpsmSerializer")
    private XStream xstream;


    @Override
    public boolean handle(MimeMessage msg) {

        EventSubject subject = null;

        try {
            subject = EventSubject.parse(msg.getSubject());

            if (subject == null)
                return false;

            logger.debug("Got inbound event-message {}", subject.toString());

            if (!(msg.getContent() instanceof MimeMultipart)) {
                logger.debug("Wrong mail message type : {}, skip handling", msg.getContent().getClass());
                return true;
            }

            MimeMultipart mparts = (MimeMultipart) msg.getContent();

            EventMsg eventMsg = null;

            for (int i = 0; i < mparts.getCount(); i++) {
                logger.debug("process part #{}", i);
                logger.debug(" message part #{}, Content type: {}", i, mparts.getBodyPart(i).getContentType());

                String fileName = mparts.getBodyPart(i).getFileName();
                logger.debug(" message part #{}, File name: {}", i, fileName);

                if (fileName != null && fileName.equalsIgnoreCase(RTTS_HPSM_XML)) {
                    try (InputStream contentStream = mparts.getBodyPart(i).getInputStream()) {
                        eventMsg = (EventMsg) xstream.fromXML(contentStream);
                    }
                }
            }

            if (eventMsg != null) {
                logger.debug("event message parsed");
            }
            else {
                logger.debug("unable to parse event data");
                return true;
            }


        } catch (Throwable e) {
            logger.debug("error on event message handle", e);
        }


        return subject != null;
    }
}
