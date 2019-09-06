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

    @JdbcColumn(name = "time_of_reaction_min")
    private Long timeOfReactionMinutes;

    @JdbcColumn(name = "time_of_decision_min")
    private Long timeOfDecisionMinutes;

    public JiraSLAMapEntry() {}

    public long getId() {
        return id;
    }

    public long getMapId() {
        return mapId;
    }

    public String getIssueType() {
        return issueType;
    }

    public String getSeverity() {
        return severity;
    }

    public String getDescription() {
        return description;
    }

    public Long getTimeOfReactionMinutes() {
        return timeOfReactionMinutes;
    }

    public Long getTimeOfDecisionMinutes() {
        return timeOfDecisionMinutes;
    }
}
