package ru.protei.portal.hpsm.logic;

import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.hpsm.struct.HpsmAttachment;
import ru.protei.portal.hpsm.struct.HpsmMessage;
import ru.protei.portal.hpsm.struct.HpsmMessageHeader;
import ru.protei.portal.hpsm.utils.HpsmUtils;

import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 28.04.17.
 */
public class HpsmEvent {

    HpsmMessageHeader subject;
    HpsmMessage hpsmMessage;
    MimeMessage mailMessage;

    List<HpsmAttachment> attachments;

    String mailBodyText;

    Company company;

    public HpsmEvent(HpsmMessageHeader subject, HpsmMessage msg, MimeMessage mailMessage) {
        this.subject = subject;
        this.hpsmMessage = msg;
        this.mailMessage = mailMessage;
    }

    public boolean hasAttachments () {
        return this.attachments != null && !this.attachments.isEmpty();
    }

    public void addAttachment (HpsmAttachment attachment) {
        if (this.attachments == null)
            this.attachments = new ArrayList<>();

        this.attachments.add(attachment);
    }

    public List<HpsmAttachment> getAttachments() {
        return attachments;
    }

    public HpsmEvent assign (HpsmMessage message) {
        this.hpsmMessage = message;
        return this;
    }

    public HpsmEvent assign(Company company) {
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

    public void setMailBodyText (String text) {
        this.mailBodyText = text;
    }

    public String getMailBodyText () {
        return this.mailBodyText;
    }
}
