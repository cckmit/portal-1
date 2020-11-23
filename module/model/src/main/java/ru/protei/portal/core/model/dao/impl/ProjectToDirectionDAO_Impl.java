package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.ProjectToDirectionDAO;
import ru.protei.portal.core.model.ent.ProjectToDirection;
import ru.protei.winter.jdbc.JdbcBaseDAO;

public class ProjectToDirectionDAO_Impl extends JdbcBaseDAO<ProjectToDirection, ProjectToDirection> implements ProjectToDirectionDAO {
    @Override
    public boolean removeByKey(ProjectToDirection key) {
        return removeByCondition("project_id=" + key.getProjectId() + " and direction_id=" + key.getDirectionId()) > 0;
    }
}