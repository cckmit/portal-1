package ru.protei.portal.core.model.struct;

public class MailReceiveInfo {
    public Long caseNo;
    public String senderEmail;
    public String text;

    public MailReceiveInfo(Long caseNo, String senderEmail, String text) {
        this.caseNo = caseNo;
        this.senderEmail = senderEmail;
        this.text = text;
    }
}
