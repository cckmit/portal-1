package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.api.ApiAbsence;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.core.model.query.AbsenceApiQuery;
import ru.protei.portal.core.model.query.AbsenceQuery;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

public interface AbsenceService {

    @Privileged(En_Privilege.ABSENCE_VIEW)
    Result<SearchResult<PersonAbsence>> getAbsences(AuthToken token, AbsenceQuery query);

    @Privileged(En_Privilege.ABSENCE_VIEW)
    Result<PersonAbsence> getAbsence(AuthToken token, Long id);

    @Privileged(En_Privilege.ABSENCE_CREATE)
    @Auditable(En_AuditType.ABSENCE_CREATE)
    Result<Long> createAbsenceFromPortal(AuthToken token, PersonAbsence absence);

    @Privileged(En_Privilege.ABSENCE_EDIT)
    @Auditable(En_AuditType.ABSENCE_MODIFY)
    Result<Long> updateAbsence(AuthToken token, PersonAbsence absence);

    @Privileged(En_Privilege.ABSENCE_REMOVE)
    @Auditable(En_AuditType.ABSENCE_REMOVE)
    Result<Long> removeAbsence(AuthToken token, PersonAbsence absence);

    @Privileged(En_Privilege.ABSENCE_EDIT)
    @Auditable(En_AuditType.ABSENCE_MODIFY)
    Result<Boolean> completeAbsence(AuthToken token, PersonAbsence absence);

    @Privileged(En_Privilege.ABSENCE_REPORT)
    Result<Void> createReport(AuthToken token, String name, AbsenceQuery query);

    @Privileged(En_Privilege.ABSENCE_VIEW)
    Result<List<ApiAbsence>> getAbsencesByApiQuery(AuthToken authToken, AbsenceApiQuery query);

    @Privileged(En_Privilege.ABSENCE_CREATE)
    @Auditable(En_AuditType.ABSENCE_CREATE)
    Result<Long> createAbsenceByApi(AuthToken token, ApiAbsence absence);

    @Privileged(En_Privilege.ABSENCE_EDIT)
    @Auditable(En_AuditType.ABSENCE_MODIFY)
    Result<Long> updateAbsenceByApi(AuthToken authToken, ApiAbsence apiAbsence);

    @Privileged(En_Privilege.ABSENCE_REMOVE)
    @Auditable(En_AuditType.ABSENCE_REMOVE)
    Result<Long> removeAbsenceByApi(AuthToken authToken, ApiAbsence apiAbsence);
}
