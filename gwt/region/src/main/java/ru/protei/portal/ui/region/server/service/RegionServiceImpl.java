package ru.protei.portal.ui.region.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.model.query.DistrictQuery;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.struct.DistrictInfo;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.core.model.struct.RegionInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.service.LocationService;
import ru.protei.portal.core.service.ProjectService;
import ru.protei.portal.ui.common.client.service.RegionService;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Реализация сервиса управления продуктами
 */
@Service( "RegionService" )
public class RegionServiceImpl implements RegionService {

    @Override
    public List< RegionInfo > getRegionList( ProjectQuery query ) throws RequestFailedException {
        log.debug( "getRegionList(): search={} | showDeprecated={} | sortField={} | order={}",
                query.getSearchString(), query.getStates(), query.getSortField(), query.getSortDir() );

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse< List< RegionInfo > > response = projectService.listRegions( query, descriptor.getLogin().getRoles() );
        if ( response.isError() )
            throw new RequestFailedException( response.getStatus() );

        return response.getData();
    }

    @Override
    public List< DistrictInfo > getDistrictList() throws RequestFailedException {

        //TODO используется в Селектор состояния региона DistrictBtnGroupMulti, считаю что привилегия REGION_VIEW не для этого

        CoreResponse< List< DistrictInfo > > result = locationService.districtList( new DistrictQuery() );

        if ( result.isError() )
            throw new RequestFailedException( result.getStatus() );

        return result.getData();
    }

    @Override
    public List< EntityOption > getRegionList() throws RequestFailedException {

        //TODO используется в селекторе регионов RegionButtonSelector, считаю что привилегия REGION_VIEW не для этого
        CoreResponse< List< EntityOption > > result = locationService.regionShortList( );

        if ( result.isError() )
            throw new RequestFailedException( result.getStatus() );

        return result.getData();
    }

    @Override
    public Map< String, List< ProjectInfo > > getProjectsByRegions( ProjectQuery query ) throws RequestFailedException {
        log.debug( "getProjectsByRegions(): search={} | showDeprecated={} | sortField={} | order={}",
                query.getSearchString(), query.getStates(), query.getSortField(), query.getSortDir() );

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse< Map< String, List< ProjectInfo > > > response = projectService.listProjectsByRegions( query, descriptor.getLogin().getRoles() );
        if ( response.isError() )
            throw new RequestFailedException( response.getStatus() );

        return response.getData();
    }

    @Override
    public ProjectInfo getProject( Long id ) throws RequestFailedException {
        log.debug( "getProject(): id={}", id );

        //TODO используется для отображения карточки проекта, думаю проверка роли PROJECT_VIEW логична
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse< ProjectInfo > response = projectService.getProject( id, descriptor.getLogin().getRoles() );
        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }

        return response.getData();
    }

    @Override
    public void saveProject( ProjectInfo project ) throws RequestFailedException {
        log.debug( "saveProject(): project={}", project );

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse response = projectService.saveProject( project, descriptor.getLogin().getRoles() );
        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }

        return;
    }

    @Override
    public long createNewProject() throws RequestFailedException {
        log.debug( "createNewProject()" );

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();
        Long personId = sessionService.getUserSessionDescriptor( httpServletRequest ).getPerson().getId();

        CoreResponse< Long > response = projectService.createProject( personId, descriptor.getLogin().getRoles() );
        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }

        return response.getData();
    }

    private UserSessionDescriptor getDescriptorAndCheckSession() throws RequestFailedException {
        UserSessionDescriptor descriptor = sessionService.getUserSessionDescriptor( httpServletRequest );
        log.info( "userSessionDescriptor={}", descriptor );
        if ( descriptor == null ) {
            throw new RequestFailedException( En_ResultStatus.SESSION_NOT_FOUND );
        }

        return descriptor;
    }

    @Autowired
    LocationService locationService;

    @Autowired
    ProjectService projectService;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpServletRequest;


//        CaseQuery caseQuery = new CaseQuery();
//        caseQuery.setType(  En_CaseType.PROJECT );
//        caseQuery.setStateIds( query.getStates().stream().map( En_RegionState::getId ).collect( Collectors.toList() ) );
//    CoreResponse<List<CaseShortView>> projectResults = caseService.caseObjectList( caseQuery );
//
//    String[] names = new String[]{
//            "Алтайский край", "Амурская область", "Архангельская область", "Астраханская область",
//            "Белгородская область", "Брянская область", "Владимирская область", "Волгоградская область"
//    };
//
//    Integer[] numbers = new Integer[] {
//            22, 28, 29, 30, 31, 32, 33, 34
//    };
//
//    En_RegionState[] states = new En_RegionState[] {
//            En_RegionState.UNKNOWN, En_RegionState.RIVAL, En_RegionState.TALK, En_RegionState.PROJECTING,
//            En_RegionState.DEVELOPMENT, En_RegionState.DEPLOYMENT, En_RegionState.SUPPORT, En_RegionState.SUPPORT_FINISHED
//    };
//
//    List<RegionInfo> result = new ArrayList<>();
//        for ( int i = 0; i < 8; i++ ) {
//        RegionInfo info = new RegionInfo();
//        info.id = new Long( i );
//        info.name = names[i];
//        info.state = states[i];
//        info.number = numbers[i];
//
//        if ( info.state.equals( En_RegionState.RIVAL ) ) {
//            info.details = "Сфера";
//        }
//        else if ( info.state.equals( En_RegionState.DEPLOYMENT ) ) {
//            info.details = "Сертификация";
//        }
//
//        if ( query.getStates() == null || query.getStates().isEmpty() ) {
//            result.add( info );
//        }
//        else {
//            if ( query.getStates().contains( info.state ) ) {
//                result.add( info );
//            }
//        }
//    }


    private static final Logger log = LoggerFactory.getLogger( "web" );
}