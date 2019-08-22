package ru.protei.portal.core.model.ent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.yt.Comment;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YouTrackIssueInfo implements Serializable {

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

    private En_CaseState caseState;
    private En_ImportanceLevel importance;
    private String id;
    private String summary;
    private String description;

    @Override
    public String toString() {
        return "YouTrackIssueInfo{" +
                "id='" + id + '\'' +
                ", caseState=" + caseState +
                ", importance=" + importance +
                ", summary='" + summary + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
