package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.EducationEntryDAO;
import ru.protei.portal.core.model.ent.EducationEntry;

import java.util.Date;
import java.util.List;

public class EducationEntryDAO_Impl extends PortalBaseJdbcDAO<EducationEntry> implements EducationEntryDAO {

    @Override
    public List<EducationEntry> getAllForDate(Date date) {
        return getListByCondition("date_end IS NULL OR date_end >= ?", date);
    }

    @Override
    public List<EducationEntry> getApprovedForDate(Date date) {
        return getListByCondition("approved IS TRUE AND (date_end IS NULL OR date_end >= ?)", date);
    }

    @Override
    public List<EducationEntry> getForWallet(Long depId, Date date) {
        return getListByCondition("id IN (SELECT DISTINCT education_entry_id FROM education_entry_attendance WHERE worker_entry_id IN " +
                "(SELECT DISTINCT id FROM worker_entry WHERE dep_id = ?)) AND approved IS TRUE AND (date_end IS NULL OR date_end >= ?))", depId, date);
    }
}
