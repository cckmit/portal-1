package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.JiraSLAMapEntry;
import ru.protei.portal.core.model.ent.ProjectSla;

import java.util.List;

public interface SLAService {

    Result<List<JiraSLAMapEntry>> getJiraSLAEntries(AuthToken token, long mapId);

    Result<JiraSLAMapEntry> getJiraSLAEntry(AuthToken token, long mapId, String issueType, String severity);

    Result<List<ProjectSla>> getProjectSlaByPlatformId(AuthToken token, Long platformId);
}
