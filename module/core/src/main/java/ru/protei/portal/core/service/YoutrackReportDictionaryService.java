package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ReportYtWorkType;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.YoutrackReportDictionary;

import java.util.List;

public interface YoutrackReportDictionaryService {
    @Privileged(En_Privilege.YT_REPORT)
    Result<List<YoutrackReportDictionary>> getDictionaries(AuthToken token, En_ReportYtWorkType type);

    @Privileged(En_Privilege.YT_REPORT)
    Result<YoutrackReportDictionary> getDictionary(AuthToken token, Long id);

    @Privileged(En_Privilege.YT_REPORT)
    @Auditable(En_AuditType.YOUTRACK_REPORT_DICTIONARY_CREATE)
    Result<Long> createDictionary(AuthToken token, YoutrackReportDictionary dictionary);

    @Privileged(En_Privilege.YT_REPORT)
    @Auditable(En_AuditType.YOUTRACK_REPORT_DICTIONARY_MODIFY)
    Result<Long> updateDictionary(AuthToken token, YoutrackReportDictionary dictionary);

    @Privileged(En_Privilege.YT_REPORT)
    @Auditable(En_AuditType.YOUTRACK_REPORT_DICTIONARY_REMOVE)
    Result<Long> removeDictionary(AuthToken token, YoutrackReportDictionary dictionary);
}
