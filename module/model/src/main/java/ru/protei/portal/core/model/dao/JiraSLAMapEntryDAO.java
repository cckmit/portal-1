package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.JiraSLAMapEntry;

import java.util.List;

public interface JiraSLAMapEntryDAO extends PortalBaseDAO<JiraSLAMapEntry> {

    List<JiraSLAMapEntry> list(long mapId);

    List<JiraSLAMapEntry> listByIssueType(long mapId, String issueType);

    JiraSLAMapEntry getByIssueTypeAndSeverity(long mapId, String issueType, String severity);
}
