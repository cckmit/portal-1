package ru.protei.portal.core.model.struct.receivedmail;

public class ReceivedMail {
    private Long caseNo;
    private String senderEmail;
    private String content;

    public ReceivedMail(Long caseNo, String senderEmail, String content) {
        this.caseNo = caseNo;
        this.senderEmail = senderEmail;
        this.content = content;
    }

    public Long getCaseNo() {
        return caseNo;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "MailReceiveInfo{" +
                "caseNo=" + caseNo +
                ", senderEmail='" + senderEmail + '\'' +
                '}';
    }
}
