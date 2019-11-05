package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.JiraSLAMapEntryDAO;
import ru.protei.portal.core.model.ent.JiraSLAMapEntry;

import java.util.List;

public class JiraSLAMapEntryDAO_Impl extends PortalBaseJdbcDAO<JiraSLAMapEntry> implements JiraSLAMapEntryDAO {

    @Override
    public List<JiraSLAMapEntry> list(long mapId) {
        return getListByCondition("MAP_ID=?", mapId);
    }

    @Override
    public List<JiraSLAMapEntry> listByIssueType(long mapId, String issueType) {
        return getListByCondition("MAP_ID=? and issue_type=?", mapId, issueType);
    }

    @Override
    public JiraSLAMapEntry getByIssueTypeAndSeverity(long mapId, String issueType, String severity) {
        return getByCondition("MAP_ID=? and issue_type=? and severity=?", mapId, issueType, severity);
    }

    @Override
    public JiraSLAMapEntry getByIssueType(long mapId, String issueType) {
        return getByCondition("MAP_ID=? and issue_type=?", mapId, issueType);
    }
}
