package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.EducationEntryAttendance;

import java.util.List;

public interface EducationEntryAttendanceDAO extends PortalBaseDAO<EducationEntryAttendance> {
    List<EducationEntryAttendance> getAllForEntry(Long entryId);
    boolean saveOrUpdate(EducationEntryAttendance entry);
}
