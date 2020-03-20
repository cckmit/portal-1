package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.EducationEntry;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.Date;
import java.util.List;

public interface EducationEntryDAO extends PortalBaseDAO<EducationEntry> {
    List<EducationEntry> getAll();
    SearchResult<EducationEntry> getAll(int offset, int limit, Boolean approved, Date date);
    List<EducationEntry> getApprovedForDate(Date date);
    List<EducationEntry> getForWallet(List<Long> depIds, Date date);
    EducationEntry get(Long id);
    boolean saveOrUpdate(EducationEntry entry);
}
