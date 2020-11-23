package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;

@JdbcEntity(table = "project_to_direction")
public class ProjectToDirection {

    @JdbcColumn(name = "project_id")
    private Long projectId;

    @JdbcColumn(name = "direction_id")
    private Long directionId;

    public ProjectToDirection() {
    }

    public ProjectToDirection(Long projectId, Long directionId) {
        this.projectId = projectId;
        this.directionId = directionId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getDirectionId() {
        return directionId;
    }

    public void setDirectionId(Long directionId) {
        this.directionId = directionId;
    }
}
