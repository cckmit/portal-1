package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.JiraStatusMapEntry;

import java.util.List;

public interface JiraStatusService {
    Result<List<JiraStatusMapEntry>> getJiraStatusMapEntryList(AuthToken token);
}
