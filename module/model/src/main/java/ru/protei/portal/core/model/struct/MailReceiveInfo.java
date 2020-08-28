package ru.protei.portal.core.model.struct;

import java.util.List;

public class MailReceiveInfo {
    private Long caseNo;
    private String senderEmail;
    private List<MailReceiveContentAndType> contentAndTypes;

    public MailReceiveInfo(Long caseNo, String senderEmail, List<MailReceiveContentAndType> contentAndTypes) {
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

    public boolean hasFullInfo() {
        return caseNo != null && senderEmail != null;
    }

    @Override
    public String toString() {
        return "MailReceiveInfo{" +
                "caseNo=" + caseNo +
                ", senderEmail='" + senderEmail + '\'' +
                '}';
    }
}
