package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Official;
import ru.protei.portal.core.model.query.OfficialQuery;

import java.util.List;
import java.util.Map;

/**
 * Created by serebryakov on 30/08/17.
 */
public interface OfficialService {

    /**
     * Возвращает проектную информацию по регионам
     * @param query    параметры запроса
     */
    @Privileged( En_Privilege.OFFICIAL_VIEW )
    CoreResponse<Map<String, List<Official>>> listOfficialsByRegions(AuthToken authToken, OfficialQuery query);
}
