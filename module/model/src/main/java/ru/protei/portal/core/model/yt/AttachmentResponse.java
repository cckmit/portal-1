package ru.protei.portal.core.model.yt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Deprecated
public class AttachmentResponse {
    @JsonProperty("fileUrl")
    private List<YtAttachment> attachments;

    public List<YtAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<YtAttachment> attachments) {
        this.attachments = attachments;
    }

    @Override
    public String toString() {
        return "AttachmentResponse{" +
                "attachments=" + attachments +
                '}';
    }
}
