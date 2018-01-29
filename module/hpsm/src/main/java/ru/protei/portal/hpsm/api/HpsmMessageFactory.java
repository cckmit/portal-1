package ru.protei.portal.hpsm.api;

import ru.protei.portal.hpsm.struct.HpsmAttachment;
import ru.protei.portal.hpsm.struct.HpsmMessage;
import ru.protei.portal.hpsm.struct.HpsmMessageHeader;
import ru.protei.portal.hpsm.struct.HpsmPingMessage;

import javax.mail.internet.MimeMessage;
import java.util.List;

/**
 * Created by michael on 15.05.17.
 */
public interface HpsmMessageFactory {

//    MimeMessage createReplyMessage(String from, HpsmEvent request, HpsmMessageHeader subject, HpsmMessage hpsmMessage) throws Exception;

    MimeMessage makePingMessgae(String to, String from, HpsmPingMessage cmd) throws Exception;
    MimeMessage makeRequestMesssage(String to, String from, HpsmMessageHeader subject, HpsmMessage hpsmMessage) throws  Exception;
    MimeMessage makeReplyMessage (String to, String from, HpsmMessageHeader subject, HpsmMessage hpsmMessage) throws  Exception;
    MimeMessage makeReplyMessage (String to, String from, HpsmMessageHeader subject, HpsmMessage hpsmMessage, List<HpsmAttachment> attachments) throws  Exception;

    MimeMessage createRejectMessage (String replyTo, String from, HpsmMessageHeader subject, String messageText) throws Exception;

    HpsmMessage parseMessage (String data);
}
