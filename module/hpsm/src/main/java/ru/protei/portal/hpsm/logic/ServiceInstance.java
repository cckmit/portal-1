package ru.protei.portal.hpsm.logic;

import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Company;
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

    Company getCompanyByBranch (String branchName);

    boolean acceptCase (CaseObject object);

    void sendReject (HpsmEvent request, String reason) throws Exception;
    void sendReject (String to, HpsmEvent request, String reason) throws Exception;
    void sendReject (String to, HpsmMessageHeader subject, String reason) throws Exception;

    void sendReply (String to, HpsmMessageHeader replyHeader, HpsmMessage replyMessage) throws Exception;
    void sendReply (HpsmMessageHeader replyHeader, HpsmMessage replyMessage) throws Exception;

    void sendReply (String replyTo, HpsmPingMessage msg) throws Exception;
}
