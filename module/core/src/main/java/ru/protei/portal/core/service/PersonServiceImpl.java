package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.CommonManagerToNotifyListDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.dao.PersonShortViewDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CommonManagerToNotifyList;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;

/**
 * Сервис управления person
 */
public class PersonServiceImpl implements PersonService {
    private static final Logger log = LoggerFactory.getLogger(PersonServiceImpl.class);
    @Autowired
    PersonDAO personDAO;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;
    @Autowired
    AuthService authService;
    @Autowired
    PolicyService policyService;
    @Autowired
    CompanyService companyService;
    @Autowired
    PersonShortViewDAO personShortViewDAO;
    @Autowired
    CommonManagerToNotifyListDAO commonManagerToNotifyListDAO;

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

        PersonShortView person = personShortViewDAO.get(personId);
        if(person==null) return error(En_ResultStatus.NOT_FOUND);

        return ok(person);
    }

    @Override
    public Result< List< PersonShortView > > shortViewList(AuthToken authToken, PersonQuery query) {
        Result<PersonQuery> fillQueryByScopeResult = fillQueryByScope(authToken, query);
        if (fillQueryByScopeResult.isError()) {
            return error(En_ResultStatus.INTERNAL_ERROR);
        }
        List<PersonShortView> personList = personShortViewDAO.getPersonsShortView(fillQueryByScopeResult.getData());
        return ok(personList);
    }

    @Override
    public Result<List<Person>> getPersonsByIds(AuthToken token, Collection<Long> ids) {
        List<Person> persons = personDAO.getListByKeys(ids);
        jdbcManyRelationsHelper.fill(persons, Person.Fields.CONTACT_ITEMS);
        persons.forEach(Person::resetPrivacyInfo);
        return ok(persons);
    }

    @Override
    public Result<List<PersonShortView>> shortViewListByIds( List<Long> ids ) {
        return ok(personShortViewDAO.getListByKeys( ids ));
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

    @Override
    public Result<Long> getNotifyListIdByCommonManagerId(Long commonManagerId) {
        log.info("getNotifyListIdByCommonManagerId(): commonManagerId = {}", commonManagerId);
        if (commonManagerId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        CommonManagerToNotifyList commonManagerToNotifyList = commonManagerToNotifyListDAO.get(commonManagerId);
        return ok(commonManagerToNotifyList.getNotifyListId());
    }

    @Override
    public Result<List<CommonManagerToNotifyList>> getNotifyListIdsByCommonManagerIds(List<Long> commonManagerIds) {
        log.info("getNotifyListIdsByCommonManagerIds(): commonManagerIds = {}", StringUtils.join(commonManagerIds,","));
        if (isEmpty(commonManagerIds)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        List<CommonManagerToNotifyList> listByKeys = commonManagerToNotifyListDAO.getByManagersIds(commonManagerIds);
        return ok(listByKeys);
    }

    @Override
    public Result<CommonManagerToNotifyList> updateCommonManagerToNotifyList(Long managerId, Long notifyListId) {
        log.info("updateCommonManagerToNotifyList(): managerId = {}, notifyListId {}", managerId, notifyListId);
        if (managerId == null || notifyListId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        CommonManagerToNotifyList commonManagerToNotifyList = commonManagerToNotifyListDAO.getByManagerId(managerId);
        if (commonManagerToNotifyList == null){
            commonManagerToNotifyList = new CommonManagerToNotifyList(managerId, notifyListId);
            commonManagerToNotifyListDAO.persist(commonManagerToNotifyList);
        } else {
            commonManagerToNotifyList.setNotifyListId(notifyListId);
            commonManagerToNotifyListDAO.updateNotifyList(commonManagerToNotifyList);
        }
        return ok(commonManagerToNotifyList);
    }

    private Result<PersonQuery> fillQueryByScope(AuthToken token, PersonQuery personQuery) {
        if (policyService.hasSystemScope(token.getRoles())) {
            return ok(personQuery);
        }

        Company company = companyService.getCompanyOmitPrivileges(token, token.getCompanyId()).getData();
        Result<List<EntityOption>> result = company.getCategory() == En_CompanyCategory.SUBCONTRACTOR ?
                companyService.companyOptionListBySubcontractorIds(token, token.getCompanyAndChildIds(), false) :
                companyService.subcontractorOptionListByCompanyIds(token, token.getCompanyAndChildIds(), false);
        if (result.isError()) {
            log.error("fillQueryByScope(): failed to get companies with result = {}", result);
            return error(result.getStatus());
        }

        Set<Long> allowedCompanies = stream(new HashSet<Long>() {{
            addAll(token.getCompanyAndChildIds());
            addAll(result.getData().stream().map(EntityOption::getId).collect(Collectors.toSet()));
        }}).collect(Collectors.toSet());

        if (personQuery.getCompanyIds() != null) {
            personQuery.getCompanyIds().retainAll(allowedCompanies);
        }

        log.info("fillQueryByScope(): PersonQuery modified: {}", personQuery);
        return ok(personQuery);
    }
}
