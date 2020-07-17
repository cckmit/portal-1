package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.EducationEntryAttendanceDAO;
import ru.protei.portal.core.model.ent.EducationEntryAttendance;

import java.util.Date;
import java.util.List;

import static ru.protei.portal.core.model.helper.HelperFunc.makeInArg;

public class EducationEntryAttendanceDAO_Impl extends PortalBaseJdbcDAO<EducationEntryAttendance> implements EducationEntryAttendanceDAO {

    @Override
    public List<EducationEntryAttendance> getAllForEntry(Long entryId) {
        return getListByCondition("education_entry_id = ?", entryId);
    }

    @Override
    public List<EducationEntryAttendance> getAllForEntryAndWorkers(Long entryId, List<Long> workerIds) {
        return getListByCondition("education_entry_id = ? AND worker_entry_id IN " + makeInArg(workerIds, String::valueOf), entryId);
    }

    @Override
    public List<EducationEntryAttendance> getAllForDepAndDates(List<Long> depIds, Date rangeFrom, Date rangeTo) {
        return getListByCondition("worker_entry_id IN (SELECT DISTINCT id FROM worker_entry WHERE dep_id IN " + makeInArg(depIds, String::valueOf) + ")" +
                " AND (date_requested BETWEEN ? AND ?)", rangeFrom, rangeTo);
    }
}
