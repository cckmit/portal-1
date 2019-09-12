package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.JiraSLAMapEntry;

import java.util.List;

public interface SLAService {

    Result<List<JiraSLAMapEntry>> getJiraSLAEntries(AuthToken token, long mapId);

    Result<JiraSLAMapEntry> getJiraSLAEntry(AuthToken token, long mapId, String issueType, String severity);
}
