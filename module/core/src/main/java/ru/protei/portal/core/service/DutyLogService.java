package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.DutyLog;
import ru.protei.portal.core.model.query.DutyLogQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

/**
 * Сервис управления журналом дежурств
 */
public interface DutyLogService {

    @Privileged( En_Privilege.DUTY_LOG_VIEW )
    Result<SearchResult<DutyLog>> getDutyLogs(AuthToken authToken, DutyLogQuery query );

    @Privileged( En_Privilege.DUTY_LOG_VIEW )
    Result<DutyLog> getDutyLog( AuthToken authToken, Long id );

    @Privileged( En_Privilege.DUTY_LOG_CREATE )
    @Auditable( En_AuditType.DUTY_LOG_CREATE )
    Result<Long> createDutyLog(AuthToken authToken, DutyLog value );

    @Privileged( En_Privilege.DUTY_LOG_EDIT )
    @Auditable( En_AuditType.DUTY_LOG_MODIFY )
    Result<Long> updateDutyLog(AuthToken authToken, DutyLog value );
}
