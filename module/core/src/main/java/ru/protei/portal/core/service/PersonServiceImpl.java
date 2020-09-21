package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

/**
 * Сервис управления person
 */
public class PersonServiceImpl implements PersonService {

    @Autowired
    PersonDAO personDAO;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Override
    public Result<Person> getPerson(AuthToken token, Long personId) {
        Person person = personDAO.get(personId);
        jdbcManyRelationsHelper.fill(person, Person.Fields.CONTACT_ITEMS);
        // RESET PRIVACY INFO
        person.resetPrivacyInfo();
        return ok(person);
    }

    @Override
    public Result<PersonShortView> getPersonShortView(AuthToken token, Long personId) {

        if (personId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Person person = personDAO.get(personId);
        if(person==null) return error(En_ResultStatus.NOT_FOUND);
        return ok(person.toFullNameShortView());
    }

    @Override
    public Result< List< PersonShortView > > shortViewList( AuthToken authToken, PersonQuery query) {
        query = processQueryByPolicyScope(authToken, query);
        return makeListPersonShortView(personDAO.getPersons( query ));
    }

    @Override
    public Result<List<Person>> getPersonsByIds(AuthToken token, Collection<Long> ids) {
        List<Person> persons = personDAO.getListByKeys(ids);
        jdbcManyRelationsHelper.fill(persons, Person.Fields.CONTACT_ITEMS);
        return ok(persons);
    }

    @Override
    public Result<List<PersonShortView>> shortViewListByIds( List<Long> ids ) {
        return makeListPersonShortView(personDAO.getListByKeys( ids ));
    }

    @Override
    public Result<Map<Long, String>> getPersonNames(AuthToken token, Collection<Long> ids) {
        Collection<Person> list = personDAO.partialGetListByKeys(ids, "id", "displayname");

        if ( list == null )
            return error(En_ResultStatus.GET_DATA_ERROR );

        Map<Long, String> names = new HashMap<>(list.size());
        list.forEach(a -> names.put(a.getId(), a.getDisplayName()));
        return ok(names );
    }

    @Override
    public Result<Person> getCommonManagerByProductId(AuthToken authToken, Long productId) {
        if (productId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Person person = personDAO.getCommonManagerByProductId(productId);
        jdbcManyRelationsHelper.fill(person, Person.Fields.CONTACT_ITEMS);
        return ok(person);
    }

    private PersonQuery processQueryByPolicyScope(AuthToken token, PersonQuery personQuery ) {
        Set<UserRole> roles = token.getRoles();
        if (policyService.hasGrantAccessFor( roles, En_Privilege.COMPANY_VIEW )) {
            return personQuery;
        }

        if (personQuery.getCompanyIds() != null) {
            personQuery.getCompanyIds().retainAll(token.getCompanyAndChildIds());
        }

        log.info("processQueryByPolicyScope(): PersonQuery modified: {}", personQuery);
        return personQuery;
    }

    private Result<List<PersonShortView>> makeListPersonShortView(List<Person> persons) {
        if ( persons == null )
            return error(En_ResultStatus.GET_DATA_ERROR );

        List< PersonShortView > result = persons.stream().map( Person::toFullNameShortView ).collect( Collectors.toList() );

        return ok(result);
    }

    @Autowired
    AuthService authService;
    @Autowired
    PolicyService policyService;

    private static final Logger log = LoggerFactory.getLogger(PersonServiceImpl.class);

}
