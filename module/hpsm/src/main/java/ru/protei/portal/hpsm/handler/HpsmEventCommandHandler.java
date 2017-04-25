package ru.protei.portal.hpsm.handler;

import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.MimeMessageHelper;
import ru.protei.portal.hpsm.api.HpsmStatus;
import ru.protei.portal.hpsm.api.MailHandler;
import ru.protei.portal.hpsm.api.MailMessageFactory;
import ru.protei.portal.hpsm.api.MailSendChannel;
import ru.protei.portal.hpsm.struct.EventMsg;
import ru.protei.portal.hpsm.struct.EventSubject;
import ru.protei.portal.hpsm.struct.HpsmSetup;

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

    @Autowired
    @Qualifier("hpsmSendChannel")
    private MailSendChannel sendChannel;

    @Autowired
    @Qualifier("hpsmMessageFactory")
    private MailMessageFactory messageFactory;

    @Autowired
    private HpsmSetup setup;


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


    interface EventMsgHandler {
        MimeMessage handle (EventSubject requestSubject, EventMsg msg) throws Exception;
    }


    private EventMsgHandler createHandler (EventSubject subject) {
        return new RejectHandler();
    }


    class CreateNewHandler implements EventMsgHandler {
        @Override
        public MimeMessage handle(EventSubject requestSubject, EventMsg msg) throws Exception {
            return null;
        }
    }


    class RejectHandler implements EventMsgHandler {
        @Override
        public MimeMessage handle(EventSubject requestSubject, EventMsg msg) throws Exception {

            MimeMessage response = messageFactory.createMailMessage();

            EventSubject respSubject = new EventSubject(requestSubject.getHpsmId(), requestSubject.getOurId(), HpsmStatus.REJECTED);

            MimeMessageHelper helper = new MimeMessageHelper(response, false);

            helper.setSubject(respSubject.toString());
            helper.setTo(setup.hpsmAddress);
            helper.setFrom(setup.senderAddress);

            return response;
        }
    }
}
