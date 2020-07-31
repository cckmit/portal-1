package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.EducationEntryDAO;
import ru.protei.portal.core.model.ent.EducationEntry;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.*;

import static ru.protei.portal.core.model.helper.HelperFunc.makeInArg;

public class EducationEntryDAO_Impl extends PortalBaseJdbcDAO<EducationEntry> implements EducationEntryDAO {

    @Override
    public SearchResult<EducationEntry> getResultForDate(int offset, int limit, Date date, Boolean approvedAttendances) {
        String sql = "1=1";
        List<Object> params = new ArrayList<>();
        if (date != null) {
            sql += " AND (date_end IS NULL OR date_end >= ?)";
            params.add(date);
        }
        if (approvedAttendances != null) {
            sql += " AND id IN (SELECT education_entry_id FROM education_entry_attendance WHERE approved IS " + (approvedAttendances ? "TRUE" : "FALSE") + ")";
        }
        return getListByCondition(sql, params, offset, limit);
    }

    @Override
    public List<EducationEntry> getAllForDate(Date date) {
        return getListByCondition("(date_end IS NULL OR date_end >= ?)", date);
    }

    @Override
    public List<EducationEntry> getForWallet(List<Long> depIds, Date date) {
        return getListByCondition("id IN " +
                "(SELECT DISTINCT education_entry_id FROM education_entry_attendance WHERE approved IS TRUE AND worker_entry_id IN " +
                    "(SELECT DISTINCT id FROM worker_entry WHERE dep_id IN " + makeInArg(depIds, String::valueOf) + ")" +
                ") AND (date_end IS NULL OR date_end >= ?)", date);
    }
}
