package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.ProjectSla;

import java.util.List;

public interface ProjectSlaService {
    Result<List<ProjectSla>> getSlaByProjectId(AuthToken token, Long projectId);
}
