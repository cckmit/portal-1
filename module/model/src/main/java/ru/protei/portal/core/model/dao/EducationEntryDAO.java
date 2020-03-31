package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.EducationEntry;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.Date;
import java.util.List;

public interface EducationEntryDAO extends PortalBaseDAO<EducationEntry> {
    List<EducationEntry> getAll();
    SearchResult<EducationEntry> getResultForDate(int offset, int limit, Date date);
    List<EducationEntry> getAllForDate(Date date);
    List<EducationEntry> getForWallet(List<Long> depIds, Date date);
    EducationEntry get(Long id);
    boolean saveOrUpdate(EducationEntry entry);
}
