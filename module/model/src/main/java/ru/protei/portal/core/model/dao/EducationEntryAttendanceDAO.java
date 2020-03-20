package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.EducationEntryAttendance;

import java.util.Date;
import java.util.List;

public interface EducationEntryAttendanceDAO extends PortalBaseDAO<EducationEntryAttendance> {
    List<EducationEntryAttendance> getAllForEntry(Long entryId);
    List<EducationEntryAttendance> getAllForDepAndDates(List<Long> depIds, Date rangeFrom, Date rangeTo);
    boolean saveOrUpdate(EducationEntryAttendance entry);
}
