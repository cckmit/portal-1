package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.YoutrackReportDictionaryDAO;
import ru.protei.portal.core.model.dict.En_ReportYtWorkType;
import ru.protei.portal.core.model.ent.YoutrackReportDictionary;

import java.util.List;

public class YoutrackReportDictionaryDAO_Impl extends PortalBaseJdbcDAO<YoutrackReportDictionary> implements YoutrackReportDictionaryDAO {
    @Override
    public List<YoutrackReportDictionary> getByType(En_ReportYtWorkType type) {
        return getListByCondition(YoutrackReportDictionary.Columns.DICTIONARY_TYPE + " = ?", type.getId());
    }
}
