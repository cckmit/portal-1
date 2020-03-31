package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.ProjectSla;

import java.util.List;

public interface ProjectSlaDAO extends PortalBaseDAO<ProjectSla> {
    List<ProjectSla> getSlaByProjectId(Long contractId);
}
