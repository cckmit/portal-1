package ru.protei.portal.core.model.struct;

public class MailReceiveContentAndType {
    private String content;
    private String contentType;

    public MailReceiveContentAndType(String content, String contentType) {
        this.content = content;
        this.contentType = contentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
