package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.query.DistrictQuery;
import ru.protei.portal.core.model.struct.DistrictInfo;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.List;

/**
 * Сервис управления местоположениями
 */
public interface LocationService {
    Result<List<DistrictInfo>> districtList( AuthToken token, DistrictQuery query );

    Result<List<EntityOption>> regionShortList( AuthToken token );
}
