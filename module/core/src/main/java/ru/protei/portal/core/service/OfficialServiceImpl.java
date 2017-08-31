package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.CaseLocationDAO;
import ru.protei.portal.core.model.dao.CaseMemberDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.OfficialQuery;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;
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

    @Override
    public CoreResponse<OfficialMember> getOfficialMember(AuthToken authToken, Long id) {
        CaseMember caseMember = caseMemberDAO.get(id);
        return new CoreResponse<OfficialMember>().success(OfficialMember.fromCaseMember(caseMember));
    }

    @Override
    public CoreResponse<OfficialMember> saveOfficialMember(AuthToken authToken, OfficialMember officialMember) {

        CaseMember caseMember = caseMemberDAO.get(officialMember.getId());
        Person member = caseMember.getMember();
        member.setFirstName(officialMember.getFirstName());
        member.setPosition(officialMember.getPosition());
        member.setCompanyId(officialMember.getCompany().getId());
        member.setLastName(officialMember.getLastName());
        member.setSecondName(officialMember.getSecondName());
        member.setAmplua(officialMember.getAmplua());
        member.setRelations(officialMember.getRelations());

        boolean isUpdated = personDAO.merge(member);

        if (!isUpdated)
            return new CoreResponse().error(En_ResultStatus.NOT_UPDATED);

        return new CoreResponse<OfficialMember>().success(OfficialMember.fromCaseMember(caseMember));
    }

    @Override
    public CoreResponse<Official> saveOfficial(AuthToken authToken, Official official) {
        CaseObject caseObject = caseObjectDAO.get(official.getId());
        helper.fillAll(caseObject);
        log.debug("caseObject " + caseObject.toString() );
        log.debug("regionId " + official.getRegion().getId() );
        caseObject.setProductId(official.getProduct().getId());
        caseObject.setInfo(official.getInfo());
        caseObjectDAO.merge(caseObject);
        CaseLocation location = caseLocationDAO.get(caseObject.getLocations().get(0).getId());
        location.setLocationId(official.getRegion().getId());
        caseLocationDAO.merge(location);


        return new CoreResponse<Official>().success(official);
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
    PersonDAO personDAO;
    @Autowired
    CaseObjectDAO caseObjectDAO;
    @Autowired
    CaseMemberDAO caseMemberDAO;
    @Autowired
    CaseLocationDAO caseLocationDAO;
    @Autowired
    JdbcManyRelationsHelper helper;

    private static Logger log = LoggerFactory.getLogger(OfficialServiceImpl.class);
}
