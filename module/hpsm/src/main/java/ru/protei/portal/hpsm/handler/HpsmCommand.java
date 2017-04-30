package ru.protei.portal.hpsm.handler;

import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.hpsm.struct.HpsmMessage;
import ru.protei.portal.hpsm.struct.HpsmMessageHeader;
import ru.protei.portal.hpsm.utils.HpsmUtils;

import javax.mail.internet.MimeMessage;

/**
 * Created by michael on 28.04.17.
 */
public class HpsmCommand {

    HpsmMessageHeader subject;
    HpsmMessage hpsmMessage;
    MimeMessage mailMessage;

    Company company;

    public HpsmCommand(HpsmMessageHeader subject, HpsmMessage msg, MimeMessage mailMessage) {
        this.subject = subject;
        this.hpsmMessage = msg;
        this.mailMessage = mailMessage;
    }

    public HpsmCommand assign(Company company) {
        this.company = company;
        return this;
    }

    public Company getCompany() {
        return company;
    }

    public MimeMessage getMailMessage() {
        return mailMessage;
    }

    public HpsmMessageHeader getSubject() {
        return subject;
    }

    public HpsmMessage getHpsmMessage() {
        return hpsmMessage;
    }

    public String getEmailSourceAddr() throws Exception {
        return HpsmUtils.getEmailFromAddress(mailMessage);
    }
}
