package ru.protei.portal.core.model.ent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.protei.portal.core.model.struct.Pair;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UitsIssueInfo implements Serializable {

    private String id;
    private String summary;
    private String description;
    private CaseState state;
    private List<CaseComment> comments;
    private List<Pair<Attachment, CaseAttachment>> attachments;

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary( String summary ) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public CaseState getState() {
        return state;
    }

    public void setState(CaseState state) {
        this.state = state;
    }

    public List<CaseComment> getComments() {
        return comments;
    }

    public void setComments(List<CaseComment> comments) {
        this.comments = comments;
    }

    public List<Pair<Attachment, CaseAttachment>> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Pair<Attachment, CaseAttachment>> attachments) {
        this.attachments = attachments;
    }

    @Override
    public String toString() {
        return "YouTrackIssueInfo{" +
                "stateId=" + state +
                ", id='" + id + '\'' +
                ", summary='" + summary + '\'' +
                ", description='" + description + '\'' +
                ", comments=" + comments +
                ", attachments=" + attachments +
                '}';
    }
}
