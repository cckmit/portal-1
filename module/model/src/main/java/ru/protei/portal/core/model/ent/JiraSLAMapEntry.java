package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;

import java.io.Serializable;

@JdbcEntity(table = "jira_sla_map_entry")
public class JiraSLAMapEntry implements Serializable {

    @JdbcColumn(name = "id")
    private long id;

    @JdbcColumn(name = "MAP_ID")
    private long mapId;

    @JdbcColumn(name = "issue_type")
    private String issueType;

    @JdbcColumn(name = "severity")
    private String severity;

    @JdbcColumn(name = "description")
    private String description;

    @JdbcColumn(name = "time_of_reaction_hours")
    private Long timeOfReactionHours;

    @JdbcColumn(name = "time_of_decision_days")
    private Long timeOfDecisionDays;

    public JiraSLAMapEntry() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMapId() {
        return mapId;
    }

    public void setMapId(long mapId) {
        this.mapId = mapId;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getTimeOfReactionHours() {
        return timeOfReactionHours;
    }

    public void setTimeOfReactionHours(Long timeOfReactionHours) {
        this.timeOfReactionHours = timeOfReactionHours;
    }

    public Long getTimeOfDecisionDays() {
        return timeOfDecisionDays;
    }

    public void setTimeOfDecisionDays(Long timeOfDecisionDays) {
        this.timeOfDecisionDays = timeOfDecisionDays;
    }
}
