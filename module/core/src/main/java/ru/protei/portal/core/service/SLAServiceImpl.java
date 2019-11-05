package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.JiraSLAMapEntryDAO;
import ru.protei.portal.core.model.dict.En_JiraSLAIssueType;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.JiraSLAMapEntry;

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
        boolean isSeverityMustBe = En_JiraSLAIssueType.byJiraOrPortal().contains(En_JiraSLAIssueType.forIssueType(issueType));
        JiraSLAMapEntry result = isSeverityMustBe ? jiraSLAMapEntryDAO.getByIssueTypeAndSeverity(mapId, issueType, severity) :
                jiraSLAMapEntryDAO.getByIssueType(mapId, issueType);
        return result == null ? error(NOT_FOUND) : ok(result);
    }

    @Autowired
    JiraSLAMapEntryDAO jiraSLAMapEntryDAO;
}
