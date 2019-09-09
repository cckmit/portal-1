package ru.protei.portal.test.jira.mock;

import ru.protei.portal.core.model.dao.impl.JiraPriorityMapEntryDAO_Impl;
import ru.protei.portal.core.model.ent.JiraPriorityMapEntry;

public class JiraPriorityMapEntryDAO_ImplMock extends JiraPriorityMapEntryDAO_Impl {

    @Override
    public JiraPriorityMapEntry getByJiraPriorityName( long mapId, String jiraName) {
        return null;
    }
}
