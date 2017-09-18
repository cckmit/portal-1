package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.query.DistrictQuery;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.struct.DistrictInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.ProductShortView;

import java.util.List;

/**
 * Сервис управления местоположениями
 */
public interface LocationService {
    CoreResponse<List<DistrictInfo>> districtList(AuthToken token, DistrictQuery query );

    CoreResponse<List<EntityOption>> regionShortList( AuthToken token );
}
