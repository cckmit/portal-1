package ru.protei.portal.core.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType;
import ru.protei.portal.core.model.dict.En_LocationType;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.LocationQuery;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.core.model.struct.RegionInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;
import java.util.function.Consumer;
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

    @Autowired
    CaseMemberDAO caseMemberDAO;

    @Autowired
    CaseLocationDAO caseLocationDAO;

    @Autowired
    CaseTypeDAO caseTypeDAO;

    @Override
    public CoreResponse<List<RegionInfo>> listRegions( ProjectQuery query ) {
        LocationQuery locationQuery = new LocationQuery();
        locationQuery.setType( En_LocationType.REGION );
        List<Location> regions = locationDAO.listByQuery( locationQuery );
        Map<Long, RegionInfo > regionInfos = regions.stream().collect(
                Collectors.toMap( Location::getId, Location::toRegionInfo )
        );

        CaseQuery caseQuery = new CaseQuery();
        caseQuery.setType( En_CaseType.PROJECT );
        caseQuery.setStateIds( query.getStates().stream()
                .map( (state)->{ return new Long(state.getId()).intValue();} )
                .collect( Collectors.toList() )
        );
        caseQuery.setProductId( query.getDirectionId() );

        List<CaseObject> projects = caseObjectDAO.listByQuery( caseQuery );
        projects.forEach( (project)->{
            iterateAllLocations( project, (location)->{
                applyCaseToRegionInfo( project, location, regionInfos );
            } );
        } );


        return new CoreResponse<List<RegionInfo>>().success( new ArrayList<>( regionInfos.values() ));
    }

    @Override
    public CoreResponse<Map<String, List<ProjectInfo>>> listProjectsByRegions( ProjectQuery query ) {
        Map<String, List<ProjectInfo>> regionToProjectMap = new HashMap<>();
        CaseQuery caseQuery = new CaseQuery();
        caseQuery.setType( En_CaseType.PROJECT );
        caseQuery.setStateIds( query.getStates().stream()
                .map( (state)->{ return new Long(state.getId()).intValue();} )
                .collect( Collectors.toList() )
        );
        caseQuery.setProductId( query.getDirectionId() );

        List<CaseObject> projects = caseObjectDAO.listByQuery( caseQuery );
        projects.forEach( (project)->{
            iterateAllLocations( project, (location)->{
                applyCaseToProjectInfo( project, location, regionToProjectMap );
            } );
        } );

        return new CoreResponse<Map<String, List<ProjectInfo>>>().success( regionToProjectMap );
    }

    @Override
    public CoreResponse<ProjectInfo> getProject( Long id ) {
        CaseObject caseObject = caseObjectDAO.get( id );
        helper.fillAll( caseObject );

        return new CoreResponse<ProjectInfo>().success( ProjectInfo.fromCaseObject( caseObject ) );
    }

    @Override
    @Transactional
    public CoreResponse saveProject( ProjectInfo project ) {
        CaseObject caseObject = caseObjectDAO.get( project.getId() );
        helper.fillAll( caseObject );

        caseObject.setName( project.getName() );
        caseObject.setInfo( project.getDetails() );
        caseObject.setStateId( project.getState().getId() );

        if ( project.getProductDirection() == null ) {
            caseObject.setProductId( null );
        }
        else {
            caseObject.setProductId( project.getProductDirection().getId() );
        }

        updateManagers( caseObject, project.getHeadManager(), project.getManagers() );
        updateLocations( caseObject, project.getRegion() );

        caseObjectDAO.merge( caseObject );

        return new CoreResponse().success( null );
    }

    @Override
    @Transactional
    public CoreResponse< Long > createProject( Long creatorId ) {
        CaseType type = caseTypeDAO.get( new Long( En_CaseType.PROJECT.getId() ) );
        Long id = type.getNextId();
        type.setNextId( id+1 );
        caseTypeDAO.merge( type );

        CaseObject caseObject = new CaseObject();
        caseObject.setCaseNumber( id );
        caseObject.setTypeId( En_CaseType.PROJECT.getId() );
        caseObject.setCreated( new Date() );
        caseObject.setName( "Новый проект" );
        caseObject.setInfo( "" );
        caseObject.setStateId( En_RegionState.UNKNOWN.getId() );
        caseObject.setCreatorId( creatorId );

        Long newId = caseObjectDAO.persist( caseObject );
        return new CoreResponse<Long>().success( newId );
    }

    private void updateManagers( CaseObject caseObject, PersonShortView headManager, List<PersonShortView> deployManagers ) {
        for ( CaseMember member : caseObject.getMembers() ) {
            if ( member.getRole().equals( En_DevUnitPersonRoleType.HEAD_MANAGER ) ) {
                if ( headManager == null ) {
                    caseMemberDAO.removeByKey( member.getId() );
                    continue;
                }
                else {
                    member.setMemberId( headManager.getId() );
                    caseMemberDAO.merge( member );
                    continue;
                }
            }
            else if ( member.getRole().equals( En_DevUnitPersonRoleType.DEPLOY_MANAGER ) ) {
                int nPos = deployManagers.indexOf( PersonShortView.fromPerson( member.getMember() ) );
                if ( nPos == -1 ) {
                    caseMemberDAO.removeByKey( member.getId() );
                }
                else {
                    deployManagers.remove( nPos );
                }
            }
        }

        for ( PersonShortView deployManager : deployManagers ) {
            caseMemberDAO.persist( CaseMember.makeDeployManagerOf( caseObject, deployManager ) );
        }
    }

    private void updateLocations( CaseObject caseObject, EntityOption location ) {
        boolean locationFound = false;

        if ( caseObject.getLocations() != null ) {
            for ( CaseLocation loc : caseObject.getLocations() ) {
                if ( loc.getLocationId().equals( location.getId() ) ) {
                    locationFound = true;
                    continue;
                }

                caseLocationDAO.removeByKey( loc.getId() );
            }
        }

        if ( locationFound ) {
            return;
        }

        if ( location == null ) {
            return;
        }

        caseLocationDAO.persist( CaseLocation.makeLocationOf( caseObject, location ) );

    }

    private void iterateAllLocations( CaseObject project, Consumer<Location> handler ) {
        if ( project == null ) {
            return;
        }

        helper.fillAll( project );

        List<CaseLocation> locations = project.getLocations();
        if ( locations == null ) {
            return;
        }

        locations.forEach( (location)->{
            handler.accept( location.getLocation() );
        } );
    }

    private void applyCaseToRegionInfo( CaseObject project, Location location, Map<Long, RegionInfo> regions ) {
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

    private void applyCaseToProjectInfo( CaseObject project, Location location, Map<String, List<ProjectInfo>> projects ) {
        if ( location == null ) {
            return;
        }

        List<ProjectInfo> projectInfos = projects.get( location.getName() );
        if ( projectInfos == null ) {
            projectInfos = new ArrayList<>();
            projects.put( location.getName(), projectInfos );
        }

        ProjectInfo projectInfo = ProjectInfo.fromCaseObject( project );
        projectInfos.add( projectInfo );
    }
}
