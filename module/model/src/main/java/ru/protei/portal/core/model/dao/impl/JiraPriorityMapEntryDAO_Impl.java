package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.JiraPriorityMapEntryDAO;
import ru.protei.portal.core.model.dao.RedminePriorityMapEntryDAO;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.JiraPriorityMapEntry;
import ru.protei.portal.core.model.ent.RedminePriorityMapEntry;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JiraPriorityMapEntryDAO_Impl extends PortalBaseJdbcDAO<JiraPriorityMapEntry> implements JiraPriorityMapEntryDAO {

    @Override
    public JiraPriorityMapEntry getByPortalPriorityId(long mapId, En_ImportanceLevel level) {
        List<JiraPriorityMapEntry> list = getListByCondition("MAP_ID = ? AND LOCAL_priority_id = ?", mapId, level.getId());

        if (list.isEmpty())
            return null;

        if (list.size() > 1)
            Collections.sort(list, Comparator.comparing(JiraPriorityMapEntry::getJiraPriorityId));

        return list.get(0);
    }

    @Override
    public JiraPriorityMapEntry getByJiraPriorityName(long mapId, String jiraName) {
        if (jiraName == null)
            return null;

        JiraPriorityMapEntry entry = getByCondition("map_id=? and jira_priority_name = ?", mapId, jiraName);

        if (entry == null) {
            String digits = extractDigitsFromName(jiraName);

            entry = digits != null ? getByCondition("map_id=? and jira_priority_name like ?", mapId, digits+"%") : null;
        }

        return entry;
    }


    private static Pattern pattern = Pattern.compile(".*([0-9]{2}).*");

    private String extractDigitsFromName (String name) {
        Matcher m = pattern.matcher(name);
        return m.matches() ? m.group(1) : null;
    }
}
