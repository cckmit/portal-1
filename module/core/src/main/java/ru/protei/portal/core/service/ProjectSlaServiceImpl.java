package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.ProjectSlaDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.ProjectSla;
import ru.protei.portal.core.model.helper.CollectionUtils;

import java.util.Collections;
import java.util.List;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class ProjectSlaServiceImpl implements ProjectSlaService {
    @Autowired
    ProjectSlaDAO projectSlaDAO;

    @Override
    public Result<List<ProjectSla>> getSlaByProjectId(AuthToken token, Long projectId) {
        if (projectId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        List<ProjectSla> slaList = projectSlaDAO.getSlaByProjectId(projectId);

        if (CollectionUtils.isEmpty(slaList)) {
            return ok(Collections.emptyList());
        }

        return ok(slaList);
    }
}
