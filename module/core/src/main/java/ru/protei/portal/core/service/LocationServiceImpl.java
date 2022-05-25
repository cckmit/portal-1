package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.LocationDAO;
import ru.protei.portal.core.model.dict.En_LocationType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Location;
import ru.protei.portal.core.model.query.LocationQuery;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.List;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
/**
 * Реализация сервиса управления местоположениями
 */
public class LocationServiceImpl implements LocationService {
    @Autowired
    LocationDAO locationDAO;

    @Override
    public Result< List< EntityOption > > regionShortList( AuthToken token ) {
        LocationQuery locationQuery = new LocationQuery();
        locationQuery.setType( En_LocationType.REGION );
        List<Location> regions = locationDAO.listByQuery( locationQuery );

        return Result.ok(
                regions.stream()
                .map( Location::toEntityOption )
                .collect( Collectors.toList() )
        );
    }
}
