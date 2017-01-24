package ru.protei.portal.ui.region.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.query.DistrictQuery;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.struct.DistrictInfo;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.core.model.struct.RegionInfo;
import ru.protei.portal.core.service.LocationService;
import ru.protei.portal.core.service.ProjectService;
import ru.protei.portal.ui.common.client.service.RegionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Реализация сервиса управления продуктами
 */
@Service( "RegionService" )
public class RegionServiceImpl implements RegionService {

    @Override
    public List< RegionInfo > getRegionList( ProjectQuery query ) throws RequestFailedException {
        log.debug( "getRegionList(): search={} | showDeprecated={} | sortField={} | order={}",
            query.getSearchString(), query.getStates(), query.getSortField(), query.getSortDir() );

        CoreResponse<List<RegionInfo>> response = projectService.listByRegions( query );
        if ( response.isError() )
            throw new RequestFailedException( response.getStatus() );

        return response.getData();
    }

    @Override
    public List<DistrictInfo> getDistrictList() throws RequestFailedException {

        CoreResponse<List<DistrictInfo>> result = locationService.districtList( new DistrictQuery() );

        if ( result.isError() )
            throw new RequestFailedException( result.getStatus() );

        return result.getData();
    }

    @Override
    public Map<String, List<ProjectInfo>> getProjectsByRegions( ProjectQuery query ) {
        Map<String, List<ProjectInfo>> result = new TreeMap<>();
        List<ProjectInfo> list = new ArrayList<>();
        result.put( "Ставропольский край", list );
        list.add( ProjectInfo.make( 6532L, "Разработка и настройка системы 112 для правительства ставропольского края", "Развитие проекта, поддержка", En_RegionState.SUPPORT, "Система 112", "Богомолов Д.", "Магомедова Е." ) );
        list.add( ProjectInfo.make( 6538L, "Очередное длинное государственное название проекта", "Развитие проекта, поддержка", En_RegionState.SUPPORT, "Система оповещения", "Богомолов Д.", "Магомедова Е.", "Соломко С." ) );
        list.add( ProjectInfo.make( 7228L, "Поставка решения обеспечивающего безопасность", "Развитие проекта, поддержка", En_RegionState.SUPPORT, "АПК БГ", "Богомолов Д.", "Магомедова Е.", "Магомедова Е." ) );

        list = new ArrayList<>();
        result.put( "Кемеровская область", list );
        list.add( ProjectInfo.make( 2365L, "Разработка единой дежурно-диспетчерской службы города Междуреченск", "Поддержка активности", En_RegionState.TALK, "ЕДДС", "Богомолов Д.", "Магомедова Е." ) );
        return null;
    }

    @Autowired
    LocationService locationService;

    @Autowired
    ProjectService projectService;


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