package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.EducationEntryDAO;
import ru.protei.portal.core.model.ent.EducationEntry;

import java.util.Date;
import java.util.List;

public class EducationEntryDAO_Impl extends PortalBaseJdbcDAO<EducationEntry> implements EducationEntryDAO {

    @Override
    public List<EducationEntry> getAllForDate(Date date) {
        return getListByCondition("date_end >= ?", date);
    }

    @Override
    public List<EducationEntry> getApprovedForDate(Date date) {
        return getListByCondition("approved is true AND date_end >= ?", date);
    }
}
