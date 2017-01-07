package ru.protei.portal.ui.region.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.query.RegionQuery;
import ru.protei.portal.core.model.struct.RegionInfo;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.service.ProductService;
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
            query.getSearchString(), query.getState(), query.getSortField(), query.getSortDir() );

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

            result.add( info );
        }

        return result;
    }

    private static final Logger log = LoggerFactory.getLogger( "web" );
}