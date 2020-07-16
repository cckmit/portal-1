package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.JiraStatusMapEntryDAO;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.JiraStatusMapEntry;

import java.util.List;

import static ru.protei.portal.api.struct.Result.ok;

public class JiraStatusServiceImpl implements JiraStatusService {
    @Autowired
    JiraStatusMapEntryDAO jiraStatusMapEntryDAO;

    @Override
    public Result<List<JiraStatusMapEntry>> getJiraStatusMapEntryList(AuthToken token) {
        return ok(jiraStatusMapEntryDAO.getAll());
    }
}
