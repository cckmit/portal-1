package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.EducationEntryAttendanceDAO;
import ru.protei.portal.core.model.ent.EducationEntryAttendance;

import java.util.List;

public class EducationEntryAttendanceDAO_Impl extends PortalBaseJdbcDAO<EducationEntryAttendance> implements EducationEntryAttendanceDAO {

    @Override
    public List<EducationEntryAttendance> getAllForEntry(Long entryId) {
        return getListByCondition("education_entry_id = ?", entryId);
    }
}
