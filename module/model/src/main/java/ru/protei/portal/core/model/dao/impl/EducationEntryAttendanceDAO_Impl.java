package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.EducationEntryAttendanceDAO;
import ru.protei.portal.core.model.ent.EducationEntryAttendance;

import java.util.Date;
import java.util.List;

public class EducationEntryAttendanceDAO_Impl extends PortalBaseJdbcDAO<EducationEntryAttendance> implements EducationEntryAttendanceDAO {

    @Override
    public List<EducationEntryAttendance> getAllForEntry(Long entryId) {
        return getListByCondition("education_entry_id = ?", entryId);
    }

    @Override
    public List<EducationEntryAttendance> getAllForDepAndDates(Long depId, Date rangeFrom, Date rangeTo) {
        return getListByCondition("worker_entry_id IN (SELECT DISTINCT id FROM worker_entry WHERE dep_id = ?)" +
                " AND (date_requested BETWEEN ? AND ?)", depId, rangeFrom, rangeTo);
    }
}
