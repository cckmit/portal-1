package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;

@JdbcEntity(table = "project_sla")
public class ProjectSla implements Serializable {
    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "importance_level_id")
    private Integer importanceLevelId;

    @JdbcColumn(name = "reaction_time")
    private Long reactionTime;

    @JdbcColumn(name = "temporary_solution_time")
    private Long temporarySolutionTime;

    @JdbcColumn(name = "full_solution_time")
    private Long fullSolutionTime;

    @JdbcColumn(name = "project_id")
    private Long projectId;

    public ProjectSla() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getImportanceLevelId() {
        return importanceLevelId;
    }

    public void setImportanceLevelId(Integer importanceLevelId) {
        this.importanceLevelId = importanceLevelId;
    }

    public Long getReactionTime() {
        return reactionTime;
    }

    public void setReactionTime(Long reactionTime) {
        this.reactionTime = reactionTime;
    }

    public Long getTemporarySolutionTime() {
        return temporarySolutionTime;
    }

    public void setTemporarySolutionTime(Long temporarySolutionTime) {
        this.temporarySolutionTime = temporarySolutionTime;
    }

    public Long getFullSolutionTime() {
        return fullSolutionTime;
    }

    public void setFullSolutionTime(Long fullSolutionTime) {
        this.fullSolutionTime = fullSolutionTime;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    @Override
    public String toString() {
        return "ContractSLA{" +
                "id=" + id +
                ", importanceLevelId=" + importanceLevelId +
                ", reactionTime=" + reactionTime +
                ", temporarySolutionTime=" + temporarySolutionTime +
                ", fullSolutionTime=" + fullSolutionTime +
                ", projectId=" + projectId +
                '}';
    }
}
