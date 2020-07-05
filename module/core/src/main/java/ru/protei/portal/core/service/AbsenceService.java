package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.core.model.query.AbsenceQuery;

import java.util.List;

public interface AbsenceService {

    @Privileged(En_Privilege.ABSENCE_VIEW)
    Result<List<PersonAbsence>> getAbsences(AuthToken token, AbsenceQuery query);

    @Privileged(En_Privilege.ABSENCE_VIEW)
    Result<PersonAbsence> getAbsence(AuthToken token, Long id);

    @Privileged(En_Privilege.ABSENCE_CREATE)
    @Auditable(En_AuditType.ABSENCE_CREATE)
    Result<Long> createAbsence(AuthToken token, PersonAbsence personAbsence);

    @Privileged(En_Privilege.ABSENCE_EDIT)
    @Auditable(En_AuditType.ABSENCE_MODIFY)
    Result<Long> updateAbsence(AuthToken token, PersonAbsence personAbsence);

    @Privileged(En_Privilege.ABSENCE_REMOVE)
    @Auditable(En_AuditType.ABSENCE_REMOVE)
    Result<Boolean> removeAbsence(AuthToken token, Long absenceId);

    @Privileged(En_Privilege.ABSENCE_EDIT)
    @Auditable(En_AuditType.ABSENCE_MODIFY)
    Result<Boolean> completeAbsence(AuthToken token, Long absenceId);

    @Privileged(En_Privilege.ABSENCE_REPORT)
    Result<Void> createReport(AuthToken token, String name, AbsenceQuery query);
}
