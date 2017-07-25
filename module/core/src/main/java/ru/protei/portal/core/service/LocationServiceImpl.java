package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.LocationDAO;
import ru.protei.portal.core.model.dict.En_LocationType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Location;
import ru.protei.portal.core.model.query.DistrictQuery;
import ru.protei.portal.core.model.query.LocationQuery;
import ru.protei.portal.core.model.struct.DistrictInfo;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса управления местоположениями
 */
public class LocationServiceImpl implements LocationService {

    @Autowired
    LocationDAO locationDAO;

    @Override
    public CoreResponse<List<DistrictInfo>> districtList(AuthToken token, DistrictQuery query) {

        List<Location> list = locationDAO.listByQuery(query);

        if (list == null)
            new CoreResponse<List<DistrictInfo>>().error(En_ResultStatus.GET_DATA_ERROR);

        return new CoreResponse<List<DistrictInfo>>().success(
            list.stream()
                .map( ( item ) -> item.toDistrictInfo() )
                .collect( Collectors.toList() )
        );
    }

    @Override
    public CoreResponse< List< EntityOption > > regionShortList( AuthToken token ) {
        LocationQuery locationQuery = new LocationQuery();
        locationQuery.setType( En_LocationType.REGION );
        List<Location> regions = locationDAO.listByQuery( locationQuery );

        return new CoreResponse<List<EntityOption>>().success(
            regions.stream()
                .map( Location::toEntityOption )
                .collect( Collectors.toList() )
        );
    }
}
