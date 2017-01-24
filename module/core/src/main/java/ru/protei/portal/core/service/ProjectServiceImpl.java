package ru.protei.portal.core.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_LocationType;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Location;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.LocationQuery;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.struct.RegionInfo;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Реализация сервиса управления проектами
 */
public class ProjectServiceImpl implements ProjectService {

    private static Logger log = LoggerFactory.getLogger(ProjectServiceImpl.class);

    @Autowired
    CaseObjectDAO caseObjectDAO;

    @Autowired
    LocationDAO locationDAO;

    @Autowired
    JdbcManyRelationsHelper helper;

    @Override
    public CoreResponse<List<RegionInfo>> listByRegions( ProjectQuery query ) {
        LocationQuery locationQuery = new LocationQuery();
        locationQuery.setType( En_LocationType.REGION );
        List<Location> regions = locationDAO.listByQuery( locationQuery );
        Map<Long, RegionInfo > regionInfos = regions.stream().collect(
                Collectors.toMap( Location::getId, Location::toRegionInfo )
        );


        CaseQuery caseQuery = new CaseQuery();
        caseQuery.setType( En_CaseType.PROJECT );
        List<CaseObject> projects = caseObjectDAO.listByQuery( caseQuery );
        projects.forEach( (project)->{
            applyProjectToAllRegions( project, regionInfos );
        } );


        return new CoreResponse<List<RegionInfo>>().success( new ArrayList<>( regionInfos.values() ));
    }

    private void applyProjectToAllRegions( CaseObject project, Map<Long, RegionInfo> regions ) {
        if ( project == null ) {
            return;
        }

        helper.fillAll( project );

        List<Location> locations = project.getLocations();
        if ( locations == null ) {
            return;
        }

        locations.forEach( (location)->{
            applyProjectToRegion( project, location, regions );
        } );
    }

    private void applyProjectToRegion( CaseObject project, Location location, Map<Long, RegionInfo> regions ) {
        RegionInfo region = findRegionByLocation( regions, location );
        if ( region == null ) {
            return;
        }

        if ( region.state == En_RegionState.UNKNOWN ) {
            region.state = En_RegionState.forId( project.getStateId() );
            return;
        }
    }

    private RegionInfo findRegionByLocation( Map<Long, RegionInfo> regions, Location location ) {
        // добавить сюда поиск региона, если location у проекта не регион а муниципальное образование например
        if ( !En_LocationType.REGION.equals( location.getType() ) ) {
            return null;
        }

        RegionInfo info = regions.get( location.getId() );
        if ( info != null ) {
            return info;
        }

        return null;
    }

}
