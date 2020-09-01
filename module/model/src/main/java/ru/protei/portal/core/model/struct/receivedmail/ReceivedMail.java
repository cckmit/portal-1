package ru.protei.portal.core.model.struct.receivedmail;

import java.util.List;

public class ReceivedMail {
    private Long caseNo;
    private String senderEmail;
    private List<MailReceiveContentAndType> contentAndTypes;

    public ReceivedMail(Long caseNo, String senderEmail, List<MailReceiveContentAndType> contentAndTypes) {
        this.caseNo = caseNo;
        this.senderEmail = senderEmail;
        this.contentAndTypes = contentAndTypes;
    }

    public Long getCaseNo() {
        return caseNo;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public List<MailReceiveContentAndType> getContentAndTypes() {
        return contentAndTypes;
    }

    @Override
    public String toString() {
        return "MailReceiveInfo{" +
                "caseNo=" + caseNo +
                ", senderEmail='" + senderEmail + '\'' +
                '}';
    }
}
