package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.ProjectSlaDAO;
import ru.protei.portal.core.model.ent.ProjectSla;

import java.util.Collections;
import java.util.List;

public class ProjectSlaDAO_Impl extends PortalBaseJdbcDAO<ProjectSla> implements ProjectSlaDAO {
    @Override
    public List<ProjectSla> getSlaByProjectId(Long projectId) {
        return getListByCondition("project_id = ?", Collections.singletonList(projectId));
    }
}
