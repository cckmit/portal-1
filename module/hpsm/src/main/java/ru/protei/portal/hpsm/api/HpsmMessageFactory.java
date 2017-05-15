package ru.protei.portal.hpsm.api;

import ru.protei.portal.hpsm.struct.HpsmMessage;
import ru.protei.portal.hpsm.struct.HpsmMessageHeader;
import ru.protei.portal.hpsm.struct.HpsmPingMessage;

import javax.mail.internet.MimeMessage;

/**
 * Created by michael on 15.05.17.
 */
public interface HpsmMessageFactory {

//    MimeMessage createReplyMessage(String from, HpsmEvent request, HpsmMessageHeader subject, HpsmMessage hpsmMessage) throws Exception;

    MimeMessage makeMessgae (String to, String from, HpsmPingMessage cmd) throws Exception;
    MimeMessage makeMessage (String to, String from, HpsmMessageHeader subject, HpsmMessage hpsmMessage) throws  Exception;

    MimeMessage createRejectMessage (String replyTo, String from, HpsmMessageHeader subject, String messageText) throws Exception;

    HpsmMessage parseMessage (String data);
}
