package ru.protei.portal.core.model.ent;

import java.io.Serializable;

public class UitsIssueInfo implements Serializable {

    private String id;
    private String summary;
    private String description;
    private CaseState state;

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

    @Override
    public String toString() {
        return "UitsIssueInfo{" +
                "stateId=" + state +
                ", id='" + id + '\'' +
                ", summary='" + summary + '\'' +
                ", description='" + description +
                '}';
    }
}
