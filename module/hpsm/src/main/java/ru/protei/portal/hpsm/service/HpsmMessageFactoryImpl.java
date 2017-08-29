package ru.protei.portal.hpsm.service;

import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.MimeMessageHelper;
import ru.protei.portal.hpsm.api.HpsmMessageFactory;
import ru.protei.portal.hpsm.api.HpsmStatus;
import ru.protei.portal.core.mail.MailMessageFactory;
import ru.protei.portal.hpsm.struct.HpsmAttachment;
import ru.protei.portal.hpsm.struct.HpsmMessage;
import ru.protei.portal.hpsm.struct.HpsmMessageHeader;
import ru.protei.portal.hpsm.struct.HpsmPingMessage;
import ru.protei.portal.hpsm.utils.EventMsgInputStreamSource;
import ru.protei.portal.hpsm.utils.HpsmUtils;

import javax.mail.internet.MimeMessage;
import java.util.List;

/**
 * Created by michael on 15.05.17.
 */
public class HpsmMessageFactoryImpl implements HpsmMessageFactory {

    private static Logger logger = LoggerFactory.getLogger(HpsmMessageFactoryImpl.class);

    @Autowired
    @Qualifier("hpsmSerializer")
    private XStream xstream;

    @Autowired
    private MailMessageFactory messageFactory;


    public HpsmMessageFactoryImpl() {
    }


    @Override
    public HpsmMessage parseMessage(String data) {
        return data != null ? (HpsmMessage)xstream.fromXML(data) : null;
    }

    @Override
    public MimeMessage makeReplyMessage(String to, String from, HpsmMessageHeader subject, HpsmMessage hpsmMessage) throws Exception {
        return makeMessageBase (to, from, subject, hpsmMessage, HpsmUtils.RTTS_HPSM_XML_REPLY, null);
    }

    @Override
    public MimeMessage makeReplyMessage(String to, String from, HpsmMessageHeader subject, HpsmMessage hpsmMessage, List<HpsmAttachment> attachments) throws Exception {
        return makeMessageBase (to, from, subject, hpsmMessage, HpsmUtils.RTTS_HPSM_XML_REPLY, attachments);
    }

    @Override
    public MimeMessage makeRequestMesssage(String to, String from, HpsmMessageHeader subject, HpsmMessage hpsmMessage) throws Exception {
        return makeMessageBase (to, from, subject, hpsmMessage, HpsmUtils.RTTS_HPSM_XML_REQUEST, null);
    }


    private MimeMessage makeMessageBase (String to, String from,
                                 HpsmMessageHeader subject,
                                 HpsmMessage hpsmMessage,
                                 String xmlName,
                                         List<HpsmAttachment> attachmentList
    ) throws Exception {
        MimeMessage response = messageFactory.createMailMessage();
        MimeMessageHelper helper = new MimeMessageHelper(response, true);
        helper.setSubject(subject.toString());
        helper.setTo(to);
        helper.setFrom(from);
        helper.addAttachment(xmlName,
                new EventMsgInputStreamSource(xstream).attach(hpsmMessage),
                "application/xml");


        if (attachmentList != null && !attachmentList.isEmpty()) {
            logger.debug("send reply with attachments, count = {}", attachmentList.size());

            for (HpsmAttachment a : attachmentList) {
                helper.addAttachment(a.getFileName(), a.getStreamSource(), a.getContentType());
                logger.debug("attachment file {} is included", a.getFileName());
            }
        }

        return response;
    }

//    public MimeMessage createReplyMessage (String from, HpsmEvent request, HpsmMessageHeader subject, HpsmMessage hpsmMessage) throws Exception {
//
//        MimeMessage response = messageFactory.createMailMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(response, true);
//
//        helper.setSubject(subject.toString());
//        helper.setTo(request.getEmailSourceAddr());
//        helper.setFrom(from);
//        helper.addAttachment(HpsmUtils.RTTS_HPSM_XML_REQUEST, new EventMsgInputStreamSource(xstream).attach(hpsmMessage), "application/xml");
//
//        return response;
//    }

    public MimeMessage makePingMessgae(String to, String from, HpsmPingMessage cmd) throws Exception {

        MimeMessage msg = messageFactory.createMailMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, false);

        helper.setSubject(cmd.toString());
        helper.setTo(to);
        helper.setFrom(from);

        return msg;
    }

//    private MimeMessage createRejectMessage (String from, HpsmEvent request, String messageText) throws Exception {
//        return createRejectMessage(request.getEmailSourceAddr(), from, request.getSubject(), messageText);
//    }

    public MimeMessage createRejectMessage (String replyTo, String from, HpsmMessageHeader subject, String messageText) throws Exception {
        MimeMessage response = messageFactory.createMailMessage();

        HpsmMessageHeader respSubject = new HpsmMessageHeader(subject.getHpsmId(), subject.getOurId(), HpsmStatus.REJECTED);

        MimeMessageHelper helper = new MimeMessageHelper(response, false);

        helper.setSubject(respSubject.toString());
        helper.setTo(replyTo);
        helper.setFrom(from);

        if (messageText != null)
            helper.setText(messageText);

        return response;
    }


}
