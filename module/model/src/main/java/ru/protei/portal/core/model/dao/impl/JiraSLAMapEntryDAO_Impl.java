package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.JiraSLAMapEntryDAO;
import ru.protei.portal.core.model.ent.JiraSLAMapEntry;

public class JiraSLAMapEntryDAO_Impl extends PortalBaseJdbcDAO<JiraSLAMapEntry> implements JiraSLAMapEntryDAO {

    @Override
    public JiraSLAMapEntry getByIssueType(long mapId, String issueType) {
        return getByCondition("MAP_ID=? and issue_type=?", mapId, issueType);
    }

    @Override
    public JiraSLAMapEntry getByIssueTypeAndSeverity(long mapId, String issueType, String severity) {
        return getByCondition("MAP_ID=? and issue_type=? and severity=?", mapId, issueType, severity);
    }
}
