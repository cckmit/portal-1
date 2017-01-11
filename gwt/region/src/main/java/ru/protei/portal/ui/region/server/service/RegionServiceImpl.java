package ru.protei.portal.ui.region.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.query.RegionQuery;
import ru.protei.portal.core.model.struct.DistrictInfo;
import ru.protei.portal.core.model.struct.RegionInfo;
import ru.protei.portal.ui.common.client.service.RegionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Реализация сервиса управления продуктами
 */
@Service( "RegionService" )
public class RegionServiceImpl implements RegionService {

    @Override
    public List< RegionInfo > getRegionList( RegionQuery query ) throws RequestFailedException {
        log.debug( "getRegionList(): search={} | showDeprecated={} | sortField={} | order={}",
            query.getSearchString(), query.getStates(), query.getSortField(), query.getSortDir() );

        String[] names = new String[]{
            "Алтайский край", "Амурская область", "Архангельская область", "Астраханская область",
            "Белгородская область", "Брянская область", "Владимирская область", "Волгоградская область"
        };

        Integer[] numbers = new Integer[] {
            22, 28, 29, 30, 31, 32, 33, 34
        };

        En_RegionState[] states = new En_RegionState[] {
            En_RegionState.UNKNOWN, En_RegionState.RIVAL, En_RegionState.TALK, En_RegionState.PROJECTING,
            En_RegionState.DEVELOPMENT, En_RegionState.DEPLOYMENT, En_RegionState.SUPPORT, En_RegionState.SUPPORT_FINISHED
        };

        List<RegionInfo> result = new ArrayList<>();
        for ( int i = 0; i < 8; i++ ) {
            RegionInfo info = new RegionInfo();
            info.id = new Long( i );
            info.name = names[i];
            info.state = states[i];
            info.number = numbers[i];

            if ( info.state.equals( En_RegionState.RIVAL ) ) {
                info.details = "Сфера";
            }
            else if ( info.state.equals( En_RegionState.DEPLOYMENT ) ) {
                info.details = "Сертификация";
            }

            if ( query.getStates() == null || query.getStates().isEmpty() ) {
                result.add( info );
            }
            else {
                if ( query.getStates().contains( info.state ) ) {
                    result.add( info );
                }
            }
        }

        return result;
    }

    @Override
    public List<DistrictInfo> getDistrictList() {
        String[] names = new String[] {
                "Центральный Федеральный Округ", "Северо-Западный Федеральный Округ", "Южный Федеральный Округ",
                "Северо-Кавказский Федеральный Округ", "Поволжский Федеральный Округ", "Уральский Федеральный Округ",
                "Сибирский Фкдкральный Округ", "Дальневосточный Федеральный Округ", "Крымский Федеральный Округ"
        };

        String[] shortNames = new String[]{
                "ЦФО", "СЗФО", "ЮФО", "СКФО", "ПФО", "УФО", "СФО", "ДВФО", "КФО"
        };

        List<DistrictInfo> result = new ArrayList<>();
        for ( int i = 0; i < 9; i++ ) {
            DistrictInfo info = new DistrictInfo();
            info.id = new Long( i );
            info.name = names[ i ];
            info.shortName = shortNames[ i ];

            result.add( info );
        }

        return result;
    }

    private static final Logger log = LoggerFactory.getLogger( "web" );
}