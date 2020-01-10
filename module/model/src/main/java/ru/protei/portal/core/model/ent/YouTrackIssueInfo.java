package ru.protei.portal.core.model.ent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.winter.core.utils.Pair;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YouTrackIssueInfo implements Serializable {

    private String id;
    private String summary;
    private String description;
    private En_CaseState caseState;
    private En_ImportanceLevel importance;
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

    public void setState( En_CaseState caseState ) {
        this.caseState = caseState;
    }

    public void setImportance( En_ImportanceLevel importance ) {
        this.importance = importance;
    }

    public En_CaseState getCaseState() {
        return caseState;
    }

    public void setCaseState( En_CaseState caseState ) {
        this.caseState = caseState;
    }

    public En_ImportanceLevel getImportance() {
        return importance;
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
                "caseState=" + caseState +
                ", importance=" + importance +
                ", id='" + id + '\'' +
                ", summary='" + summary + '\'' +
                ", description='" + description + '\'' +
                ", comments=" + comments +
                ", attachments=" + attachments +
                '}';
    }
}
