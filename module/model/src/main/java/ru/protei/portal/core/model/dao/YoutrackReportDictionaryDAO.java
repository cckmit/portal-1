package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.dict.En_ReportYtWorkType;
import ru.protei.portal.core.model.ent.YoutrackReportDictionary;

import java.util.List;

public interface YoutrackReportDictionaryDAO extends PortalBaseDAO<YoutrackReportDictionary> {
    List<YoutrackReportDictionary> getByType(En_ReportYtWorkType type);
}