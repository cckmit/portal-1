package ru.protei.portal.hpsm.handler;

import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.MimeMessageHelper;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.hpsm.api.HpsmStatus;
import ru.protei.portal.hpsm.api.MailHandler;
import ru.protei.portal.hpsm.api.MailMessageFactory;
import ru.protei.portal.hpsm.api.MailSendChannel;
import ru.protei.portal.hpsm.service.HpsmService;
import ru.protei.portal.hpsm.struct.EventMsg;
import ru.protei.portal.hpsm.struct.EventSubject;
import ru.protei.portal.hpsm.struct.HpsmSetup;
import ru.protei.portal.hpsm.utils.HpsmUtils;

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

    @Autowired
    private CaseService caseService;

    @Autowired
    private HpsmService hpsmService;


    @Override
    public boolean handle(MimeMessage msg) {

        EventSubject subject = null;

        try {
            subject = EventSubject.parse(msg.getSubject());

            if (subject == null)
                return false;


            HpsmRequest request = buildRequest(subject, msg);

            EventMsgHandler handler = createHandler(request);
            logger.debug("created handler : {}", handler);

            MimeMessage responseMsg = handler.handle(request);

            if (responseMsg != null) {
                logger.debug("handler return message with subject {}, send it back", responseMsg.getSubject());

                sendChannel.send(responseMsg);
            }
            else {
                logger.debug("Handler return empty message, it's nothing to send back");
            }

        } catch (Throwable e) {
            logger.debug("error on event message handle", e);
        }


        return subject != null;
    }


    class HpsmRequest {
        EventSubject subject;
        EventMsg eventMsg;
        MimeMessage mailMessage;

        Company company;

        public HpsmRequest(EventSubject subject, EventMsg msg, MimeMessage mailMessage) {
            this.subject = subject;
            this.eventMsg = msg;
            this.mailMessage = mailMessage;
        }

        public EventSubject getSubject() {
            return subject;
        }

        public EventMsg getEventMsg() {
            return eventMsg;
        }

        public String getEmailSourceAddr () throws Exception {
            return HpsmUtils.getEmailFromAddress(mailMessage);
        }
    }

    private HpsmRequest buildRequest (EventSubject subject, MimeMessage msg) throws Exception {

        logger.debug("Got inbound event-message {}", subject.toString());

        if (!(msg.getContent() instanceof MimeMultipart)) {
            logger.debug("Wrong mail message type : {}, skip handling", msg.getContent().getClass());
            return null;
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
            return null;
        }

        hpsmService.getCompanyByBranchName()


        return new HpsmRequest(subject, eventMsg, msg);
    }


    interface EventMsgHandler {
        MimeMessage handle (HpsmRequest request) throws Exception;
    }


    private EventMsgHandler createHandler (HpsmRequest request) {
        if (request.getSubject().isNewCaseRequest())
            return new CreateNewCaseHandler();

        return new RejectHandler();
    }


    class CreateNewCaseHandler implements EventMsgHandler {
        @Override
        public MimeMessage handle(HpsmRequest request) throws Exception {



            return null;
        }
    }



    class RejectHandler implements EventMsgHandler {

        private String messageText;

        public RejectHandler () {

        }

        public RejectHandler(String message) {
            this.messageText = message;
        }

        @Override
        public MimeMessage handle(HpsmRequest request) throws Exception {

            MimeMessage response = messageFactory.createMailMessage();

            EventSubject respSubject = new EventSubject(request.subject.getHpsmId(), request.subject.getOurId(), HpsmStatus.REJECTED);

            MimeMessageHelper helper = new MimeMessageHelper(response, false);

            helper.setSubject(respSubject.toString());
            helper.setTo(request.getEmailSourceAddr());
            helper.setFrom(setup.getSenderAddress());

            if (messageText != null)
                helper.setText(messageText);

            return response;
        }
    }


}
