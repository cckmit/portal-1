package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.JiraSLAMapEntry;

public interface JiraSLAMapEntryDAO extends PortalBaseDAO<JiraSLAMapEntry> {

    JiraSLAMapEntry getByIssueType(long mapId, String issueType);

    JiraSLAMapEntry getByIssueTypeAndSeverity(long mapId, String issueType, String severity);
}
