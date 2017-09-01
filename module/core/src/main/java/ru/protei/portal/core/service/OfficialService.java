package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Official;
import ru.protei.portal.core.model.ent.OfficialMember;
import ru.protei.portal.core.model.query.OfficialQuery;

import java.util.List;
import java.util.Map;

/**
 * Сервис для управления должностными лицами
 */
public interface OfficialService {

    /**
     * Возвращает список должностных лиц по регионам
     * @param query    параметры запроса
     */
    @Privileged({ En_Privilege.OFFICIAL_VIEW })
    CoreResponse<Map<String, List<Official>>> listOfficialsByRegions(AuthToken authToken, OfficialQuery query);


    /**
     * Возвращает матрицу принятия решений
     */
    @Privileged({ En_Privilege.OFFICIAL_VIEW })
    CoreResponse<Official> getOfficial(AuthToken authToken, Long id);

    /**
     * Возвращает должностное лицо
     */
    @Privileged({ En_Privilege.OFFICIAL_VIEW })
    CoreResponse<OfficialMember> getOfficialMember(AuthToken authToken, Long id);

    /**
     * Сохраняет должностное лицо
     */
    @Privileged({ En_Privilege.OFFICIAL_EDIT })
    @Auditable( En_AuditType.OFFICIAL_MODIFY)
    CoreResponse<OfficialMember> saveOfficialMember(AuthToken authToken, OfficialMember officialMember);

    /**
     * Сохраняет матрицу принятия решений
     */
    @Privileged({ En_Privilege.OFFICIAL_EDIT })
    @Auditable( En_AuditType.OFFICIAL_MODIFY)
    CoreResponse<Official> updateOfficial(AuthToken authToken, Official official);

    /**
     * Создает матрицу принятия решений
     */
    @Privileged({ En_Privilege.OFFICIAL_EDIT })
    @Auditable( En_AuditType.OFFICIAL_MODIFY)
    CoreResponse<Long> createOfficial(AuthToken authToken, Official official, Long creatorId);
}
