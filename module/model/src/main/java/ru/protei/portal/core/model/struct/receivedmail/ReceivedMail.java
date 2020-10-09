package ru.protei.portal.core.model.struct.receivedmail;

public class ReceivedMail {
    private final Long caseNo;
    private final String senderEmail;
    private final String content;
    private final String contentType;

    public ReceivedMail(Long caseNo, String senderEmail, String content, String contentType) {
        this.caseNo = caseNo;
        this.senderEmail = senderEmail;
        this.content = content;
        this.contentType = contentType;
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

    public String getContentType() {
        return contentType;
    }

    @Override
    public String toString() {
        return "MailReceiveInfo{" +
                "caseNo=" + caseNo +
                ", senderEmail='" + senderEmail + '\'' +
                '}';
    }
}
