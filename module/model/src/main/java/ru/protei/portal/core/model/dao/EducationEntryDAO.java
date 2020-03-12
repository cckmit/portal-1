package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.EducationEntry;

import java.util.Date;
import java.util.List;

public interface EducationEntryDAO extends PortalBaseDAO<EducationEntry> {
    List<EducationEntry> getAll();
    List<EducationEntry> getAllForDate(Date date);
    List<EducationEntry> getApprovedForDate(Date date);
    EducationEntry get(Long id);
    boolean saveOrUpdate(EducationEntry entry);
}
