package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.JiraSLAMapEntryDAO;
import ru.protei.portal.core.model.dao.ProjectSlaDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.JiraSLAMapEntry;
import ru.protei.portal.core.model.ent.ProjectSla;
import ru.protei.portal.core.model.helper.CollectionUtils;

import java.util.Collections;
import java.util.List;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.dict.En_ResultStatus.NOT_FOUND;

public class SLAServiceImpl implements SLAService {

    @Override
    public Result<List<JiraSLAMapEntry>> getJiraSLAEntries(AuthToken token, long mapId) {
        List<JiraSLAMapEntry> result = jiraSLAMapEntryDAO.list(mapId);
        return result == null ? error(NOT_FOUND) : ok(result);
    }

    @Override
    public Result<JiraSLAMapEntry> getJiraSLAEntry(AuthToken token, long mapId, String issueType, String severity) {
        JiraSLAMapEntry result = jiraSLAMapEntryDAO.getByIssueTypeAndSeverity(mapId, issueType, severity);
        return result == null ? error(NOT_FOUND) : ok(result);
    }

    @Override
    public Result<List<ProjectSla>> getProjectSlaByProjectId(AuthToken token, Long projectId) {
        if (projectId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        List<ProjectSla> slaList = projectSlaDAO.getSlaByProjectId(projectId);

        if (CollectionUtils.isEmpty(slaList)) {
            return ok(Collections.emptyList());
        }

        return ok(slaList);
    }

    @Autowired
    ProjectSlaDAO projectSlaDAO;

    @Autowired
    JiraSLAMapEntryDAO jiraSLAMapEntryDAO;
}
