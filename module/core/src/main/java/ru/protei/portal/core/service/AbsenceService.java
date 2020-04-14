package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.PersonAbsence;

public interface AbsenceService {

    @Privileged({ En_Privilege.ABSENCE_CREATE})
    @Auditable(En_AuditType.ABSENCE_CREATE)
    Result<Long> createAbsence(AuthToken token, PersonAbsence personAbsence);
}
