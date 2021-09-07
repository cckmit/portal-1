package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_YoutrackWorkType;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.YoutrackWorkDictionary;

import java.util.List;

public interface YoutrackWorkDictionaryService {
    @Privileged(En_Privilege.YT_REPORT)
    Result<List<YoutrackWorkDictionary>> getDictionaries(AuthToken token, En_YoutrackWorkType type);

    @Privileged(En_Privilege.YT_REPORT)
    @Auditable(En_AuditType.YOUTRACK_REPORT_DICTIONARY_CREATE)
    Result<YoutrackWorkDictionary> createDictionary(AuthToken token, YoutrackWorkDictionary dictionary);

    @Privileged(En_Privilege.YT_REPORT)
    @Auditable(En_AuditType.YOUTRACK_REPORT_DICTIONARY_MODIFY)
    Result<YoutrackWorkDictionary> updateDictionary(AuthToken token, YoutrackWorkDictionary dictionary);

    @Privileged(En_Privilege.YT_REPORT)
    @Auditable(En_AuditType.YOUTRACK_REPORT_DICTIONARY_REMOVE)
    Result<YoutrackWorkDictionary> removeDictionary(AuthToken token, YoutrackWorkDictionary dictionary);
}
