package ru.protei.portal.core.model.struct;

public class MailReceiveInfo {
    private Long caseNo;
    private String senderEmail;
    private String text;

    public MailReceiveInfo(Long caseNo, String senderEmail, String text) {
        this.caseNo = caseNo;
        this.senderEmail = senderEmail;
        this.text = text;
    }

    public Long getCaseNo() {
        return caseNo;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public String getText() {
        return text;
    }

    public boolean hasFullInfo() {
        return caseNo != null && senderEmail != null && text != null;
    }

    @Override
    public String toString() {
        return "MailReceiveInfo{" +
                "caseNo=" + caseNo +
                ", senderEmail='" + senderEmail + '\'' +
                '}';
    }
}
