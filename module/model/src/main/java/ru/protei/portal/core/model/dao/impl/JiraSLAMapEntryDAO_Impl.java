package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.JiraSLAMapEntryDAO;
import ru.protei.portal.core.model.ent.JiraSLAMapEntry;
import ru.protei.portal.core.model.helper.StringUtils;

import java.util.ArrayList;
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
        StringBuilder condition = new StringBuilder("MAP_ID=? and issue_type=?");
        List<Object> args = new ArrayList<>();
        args.add(mapId);
        args.add(issueType);

        if (StringUtils.isNotEmpty(severity)) {
            condition.append(" and severity=?");
            args.add(severity);
        } else {
            condition.append(" and severity is null");
        }
        return getByCondition(condition.toString(), args);
    }
}
