package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.OfficialQuery;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Реализация сервиса управления должностными лицами
 */
public class OfficialServiceImpl implements OfficialService {

    @Override
    public CoreResponse<Map<String, List<Official>>> listOfficialsByRegions(AuthToken authToken, OfficialQuery query) {

        Map<String, List<Official>> officialsByRegions = new HashMap<>();
        CaseQuery caseQuery = new CaseQuery();
        caseQuery.setSearchString(query.getSearchString());
        caseQuery.setFrom(query.getFrom());
        caseQuery.setTo(query.getTo());
        caseQuery.setType( En_CaseType.OFFICIAL );
        caseQuery.setProductId( query.getProductId() );
        List< CaseObject > caseObjects = caseObjectDAO.listByQuery( caseQuery );
        caseObjects.forEach( ( caseObject ) -> {
            iterateAllLocations( caseObject, ( location ) -> {
                applyCaseToOfficial( caseObject, location, officialsByRegions );
            } );
        } );
        return new CoreResponse<Map<String, List<Official>>>().success(officialsByRegions);
    }

    @Override
    public CoreResponse<Official> getOfficial(AuthToken authToken, Long id) {

        CaseObject caseObject = caseObjectDAO.get(id);
        helper.fillAll( caseObject );

        return new CoreResponse<Official>().success(Official.fromCaseObject(caseObject));
    }


    private void iterateAllLocations( CaseObject official, Consumer< Location > handler ) {
        if ( official == null ) {
            return;
        }

        helper.fillAll( official );

        List<CaseLocation> locations = official.getLocations();
        if ( locations == null || locations.isEmpty() ) {
            handler.accept( null );
            return;
        }

        locations.forEach( ( location ) -> {
            handler.accept( location.getLocation() );
        } );
    }


    private void applyCaseToOfficial(CaseObject currentOfficial, Location location, Map< String, List<Official> > officialsByRegions ) {

        String locationName = ""; // name for empty location
        if ( location != null ) {
            locationName = location.getName();
        }

        List< Official > officialList = officialsByRegions.get( locationName );
        if ( officialList == null ) {
            officialList = new ArrayList<>();
            officialsByRegions.put( locationName, officialList );
        }

        Official official = Official.fromCaseObject(currentOfficial);
        officialList.add(official);
    }



    @Autowired
    CaseObjectDAO caseObjectDAO;
    @Autowired
    JdbcManyRelationsHelper helper;
}
