package ru.protei.portal.hpsm.logic;

import ru.protei.portal.hpsm.struct.HpsmMessage;
import ru.protei.portal.hpsm.struct.HpsmMessageHeader;
import ru.protei.portal.hpsm.struct.HpsmPingMessage;

import javax.mail.internet.MimeMessage;

/**
 * Created by michael on 12.05.17.
 */
public interface ServiceInstance {

    String id ();

    MimeMessage read ();

    void sendReject (HpsmEvent request, String reason);

    void sendReply (HpsmEvent request, HpsmMessageHeader replyHeader, HpsmMessage replyMessage);

    void sendReply (HpsmPingMessage msg);
}
