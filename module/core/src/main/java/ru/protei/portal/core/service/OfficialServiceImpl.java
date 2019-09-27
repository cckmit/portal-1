package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.OfficialQuery;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;
import java.util.function.Consumer;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
/**
 * Реализация сервиса управления должностными лицами
 */
public class OfficialServiceImpl implements OfficialService {

    @Override
    public Result<Map<String, List<Official>>> listOfficialsByRegions( AuthToken authToken, OfficialQuery query) {

        Map<String, List<Official>> officialsByRegions = new HashMap<>();
        CaseQuery caseQuery = new CaseQuery();
        caseQuery.setSearchString(query.getSearchString());
        caseQuery.setCreatedFrom(query.getFrom());
        caseQuery.setCreatedTo(query.getTo());
        caseQuery.setType( En_CaseType.OFFICIAL );
        Set<Long> productIds = null;
        if (query.getProductId() != null){
            productIds = new HashSet<>();
            productIds.add( query.getProductId() );
        }
        caseQuery.setProductIds( productIds );

        List< CaseObject > caseObjects = caseObjectDAO.listByQuery( caseQuery );
        caseObjects.forEach( ( caseObject ) -> {
            iterateAllLocations( caseObject, ( location ) -> {
                applyCaseToOfficial( caseObject, location, officialsByRegions );
            } );
        } );
        return ok(officialsByRegions);
    }

    @Override
    public Result<Official> getOfficial( AuthToken authToken, Long id) {

        CaseObject caseObject = caseObjectDAO.get(id);
        helper.fillAll( caseObject );

        return ok(Official.fromCaseObject(caseObject));
    }

    @Override
    public Result<OfficialMember> getOfficialMember( AuthToken authToken, Long id) {
        CaseMember caseMember = caseMemberDAO.get(id);
        return ok(OfficialMember.fromCaseMember(caseMember));
    }

    @Override
    public Result<Long> createOfficialMember( AuthToken authToken, OfficialMember officialMember, Long parentId) {
        CaseObject caseObject = caseObjectDAO.get(parentId);

        CaseMember caseMember = new CaseMember();
        caseMember.setCaseId(caseObject.getId());

        Person person = new Person();
        person.setRelations(officialMember.getRelations());
        person.setCreated(new Date());
        person.setCreator("Service");
        person.setCompanyId(officialMember.getCompany().getId());
        person.setSecondName(officialMember.getSecondName());
        person.setFirstName(officialMember.getFirstName());
        person.setLastName(officialMember.getLastName());
        person.setPosition(officialMember.getPosition());
        person.setInfo(officialMember.getComments());
        person.setGender(En_Gender.UNDEFINED);
        person.setCreator("Service");
        person.setDisplayName(officialMember.getLastName() + " " + officialMember.getFirstName()
        + officialMember.getSecondName());
        Long newPersonId = personDAO.persist(person);

        caseMember.setMemberId(newPersonId);
        caseMember.setRole(officialMember.getAmplua());
        caseMemberDAO.persist(caseMember);

        return ok();
    }

    @Override
    public Result<OfficialMember> saveOfficialMember( AuthToken authToken, OfficialMember officialMember) {

        CaseMember caseMember = caseMemberDAO.get(officialMember.getId());
        Person person = caseMember.getMember();
        person.setFirstName(officialMember.getFirstName());
        person.setPosition(officialMember.getPosition());
        person.setCompanyId(officialMember.getCompany().getId());
        person.setLastName(officialMember.getLastName());
        person.setSecondName(officialMember.getSecondName());
        person.setRelations(officialMember.getRelations());
        person.setInfo(officialMember.getComments());

        boolean isUpdatedPerson = personDAO.merge(person);

        caseMember.setRole(officialMember.getAmplua());
        boolean isUpdatesMember = caseMemberDAO.merge(caseMember);
        if (!isUpdatedPerson || !isUpdatesMember)
            return error(En_ResultStatus.NOT_UPDATED);

        return ok(OfficialMember.fromCaseMember(caseMember));
    }

    @Override
    public Result<Official> updateOfficial( AuthToken authToken, Official official) {
        CaseObject caseObject = caseObjectDAO.get(official.getId());
        helper.fillAll(caseObject);
        caseObject.setProductId(official.getProduct().getId());
        caseObject.setInfo(official.getInfo());
        caseObjectDAO.merge(caseObject);
        CaseLocation location = caseLocationDAO.get(caseObject.getLocations().get(0).getId());
        location.setLocationId(official.getRegion().getId());
        caseLocationDAO.merge(location);

        return ok(official);
    }

    @Override
    public Result<Long> createOfficial( AuthToken authToken, Official official, Long creatorId) {
//        CaseType type = caseTypeDAO.get( new Long( En_CaseType.OFFICIAL.getId() ) );
//        Long id = type.getNextId();
//        type.setNextId( id + 1 );
//        caseTypeDAO.merge( type );

        CaseObject caseObject = new CaseObject();
        caseObject.setCaseType(En_CaseType.OFFICIAL);
        caseObject.setCaseNumber( caseTypeDAO.generateNextId(En_CaseType.OFFICIAL) );
        caseObject.setCreated( new Date() );
        caseObject.setName( "Новое должностное лицо" );
        caseObject.setInfo( official.getInfo() );
        caseObject.setProductId(official.getProduct().getId());
        caseObject.setCreatorId( creatorId );
        caseObject.setState(En_CaseState.CREATED);
        Long caseId = caseObjectDAO.persist( caseObject );

        CaseLocation caseLocation = new CaseLocation();
        caseLocation.setCaseId(caseId);
        caseLocation.setLocationId(official.getRegion().getId());
        caseLocationDAO.persist(caseLocation);

        caseObject.setLocations(Arrays.asList(caseLocation));
        caseObjectDAO.merge(caseObject);

        return ok(caseId );
    }

    @Override
    public Result<Boolean> removeOfficial( AuthToken authToken, Long id) {
        CaseObject caseObject = caseObjectDAO.get(id);
        helper.fillAll(caseObject);
        removeRelatedObjects(caseObject);

        boolean isRemoving = caseObjectDAO.remove(caseObject);
        return ok(isRemoving);
    }

    @Override
    public Result<Boolean> removeOfficialMember( AuthToken authToken, Long id) {
        boolean isRemoving = caseMemberDAO.removeByKey(id);

        return ok(isRemoving);
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


    private void removeRelatedObjects(CaseObject caseObject) {
        for (CaseLocation location: caseObject.getLocations()){
            caseLocationDAO.remove(location);
        }

        for (CaseMember member: caseObject.getMembers()) {
            CaseMember caseMember = caseMemberDAO.get(member.getId());
            Long memberId = caseMember.getMemberId();
            caseMemberDAO.remove(member);
            personDAO.removeByKey(memberId);
        }

        List<CaseComment> caseComments = caseCommentDAO.getCaseComments(new CaseCommentQuery(caseObject.getId()));
        for (CaseComment comment: caseComments) {
            caseCommentDAO.remove(comment);
        }
    }


    @Autowired
    PersonDAO personDAO;
    @Autowired
    CaseObjectDAO caseObjectDAO;
    @Autowired
    CaseTypeDAO caseTypeDAO;
    @Autowired
    CaseMemberDAO caseMemberDAO;
    @Autowired
    CaseLocationDAO caseLocationDAO;
    @Autowired
    CaseCommentDAO caseCommentDAO;
    @Autowired
    JdbcManyRelationsHelper helper;

    private static Logger log = LoggerFactory.getLogger(OfficialServiceImpl.class);
}
