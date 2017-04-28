package ru.protei.portal.hpsm.handler;

import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.hpsm.struct.EventMsg;
import ru.protei.portal.hpsm.struct.EventSubject;
import ru.protei.portal.hpsm.utils.HpsmUtils;

import javax.mail.internet.MimeMessage;

/**
 * Created by michael on 28.04.17.
 */
public class HpsmRequest {
    EventSubject subject;
    EventMsg eventMsg;
    MimeMessage mailMessage;

    Company company;

    public HpsmRequest(EventSubject subject, EventMsg msg, MimeMessage mailMessage) {
        this.subject = subject;
        this.eventMsg = msg;
        this.mailMessage = mailMessage;
    }

    public HpsmRequest assign(Company company) {
        this.company = company;
        return this;
    }

    public Company getCompany() {
        return company;
    }

    public MimeMessage getMailMessage() {
        return mailMessage;
    }

    public EventSubject getSubject() {
        return subject;
    }

    public EventMsg getEventMsg() {
        return eventMsg;
    }

    public String getEmailSourceAddr() throws Exception {
        return HpsmUtils.getEmailFromAddress(mailMessage);
    }
}
