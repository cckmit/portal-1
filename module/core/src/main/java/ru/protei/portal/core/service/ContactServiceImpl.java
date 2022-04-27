package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.ent.UserLoginShortView;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.core.model.query.UserLoginShortViewQuery;
import ru.protei.portal.core.model.struct.ContactItem;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;
import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

/**
 * Реализация сервиса управления контактами
 */
public class ContactServiceImpl implements ContactService {

    private static Logger log = LoggerFactory.getLogger(CompanyServiceImpl.class);

    @Autowired
    PersonDAO personDAO;
    @Autowired
    PersonShortViewDAO personShortViewDAO;
    @Autowired
    UserLoginDAO userLoginDAO;
    @Autowired
    CompanySubscriptionDAO companySubscriptionDAO;
    @Autowired
    ContactItemDAO contactItemDAO;
    @Autowired
    PolicyService policyService;
    @Autowired
    CompanyGroupHomeDAO groupHomeDAO;
    @Autowired
    CompanyGroupHomeDAO companyGroupHomeDAO;
    @Autowired
    UserLoginShortViewDAO userLoginShortViewDAO;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;


    @Override
    public Result<SearchResult<Person>> getContactsSearchResult( AuthToken token, ContactQuery query) {
        SearchResult<Person> sr = personDAO.getContactsSearchResult(query);
        List<Person> persons = sr.getResults();

        UserLoginShortViewQuery userLoginsQuery = new UserLoginShortViewQuery();
        userLoginsQuery.setPersonIds(persons.stream().map(Person::getId).collect(toSet()));
        List<UserLoginShortView> userLoginShortViews = userLoginShortViewDAO.listByQuery(userLoginsQuery);

        persons.forEach(person -> {
            person.setLogins(userLoginShortViews.stream()
                            .filter(userLoginShortView -> userLoginShortView.getPersonId().equals(person.getId()))
                            .map(UserLoginShortView::getUlogin)
                            .collect(Collectors.toList()));
            person.setTimezoneOffset(person.getBirthday() == null ? null : person.getBirthday().getTimezoneOffset());
        });

        jdbcManyRelationsHelper.fill(persons, Person.Fields.CONTACT_ITEMS);
        return ok(sr);
    }

    @Override
    public Result<List<PersonShortView>> shortViewList( AuthToken token, ContactQuery query ) {
        if (personIsEmployee( query.getCompanyId() )) {
            return ok(Collections.emptyList());
        }

        List<PersonShortView> persons  = personShortViewDAO.getContacts( query );

        if (persons == null)
            return error(En_ResultStatus.GET_DATA_ERROR);

        return ok(persons);
    }

    @Override
    public Result<Person> getContact( AuthToken token, long id ) {

        Person person = personDAO.get(id);
        if(person == null) return error( En_ResultStatus.NOT_FOUND);

        if (personIsEmployee( person.getCompanyId() )) {
            return error( En_ResultStatus.NOT_AVAILABLE);
        }

        List<UserLogin> userLogins = userLoginDAO.findByPersonId(person.getId());
        if (CollectionUtils.isNotEmpty(userLogins)) {
            person.setLogins(userLogins.stream().map(UserLogin::getUlogin)
                  .collect(Collectors.toList()));
        }

        jdbcManyRelationsHelper.fill(person, Person.Fields.CONTACT_ITEMS);

        person.setTimezoneOffset(person.getBirthday() == null ? null : person.getBirthday().getTimezoneOffset());

        return  ok( person);
    }

    @Override
    @Transactional
    public Result<Person> saveContact( AuthToken token, Person person ) {
        if (person == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (!validatePerson(person)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        if (HelperFunc.isEmpty(person.getDisplayName())) {
            person.setDisplayName(person.getLastName() + " " + person.getFirstName());
        }

        if (HelperFunc.isEmpty(person.getDisplayShortName())) {
            StringBuilder b = new StringBuilder();
            b.append (person.getLastName()).append(" ")
                    .append (person.getFirstName().substring(0,1).toUpperCase()).append(".")
            ;

            if (!person.getSecondName().isEmpty()) {
                b.append(" ").append(person.getSecondName().substring(0,1).toUpperCase()).append(".");
            }

            person.setDisplayShortName(b.toString());
        }

        if (person.getCreated() == null)
            person.setCreated(new Date());

        if (person.getCreator() == null)
            person.setCreator("service");

        if (person.getGender() == null)
            person.setGender(En_Gender.UNDEFINED);


        if (!personDAO.saveOrUpdate(person)) {
            return error(En_ResultStatus.INTERNAL_ERROR);
        }

        contactItemDAO.saveOrUpdateBatch(person.getContactItems());
        jdbcManyRelationsHelper.persist(person, Person.Fields.CONTACT_ITEMS);

        return ok(person);
    }

    @Override
    @Transactional
    public Result<Boolean> fireContact( AuthToken token, long id) {

        Person person = personDAO.get(id);

        if (person == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        if (personIsEmployee( person.getCompanyId() )) {
            return error( En_ResultStatus.NOT_AVAILABLE);
        }

        person.setFired(true);

        boolean result = personDAO.merge(person);

        if (result) {
            jdbcManyRelationsHelper.fill(person, Person.Fields.CONTACT_ITEMS);
            removePersonEmailsFromCompany(person);
            userLoginDAO.removeByPersonId(id);
        }

        return ok(result);
    }

    @Override
    @Transactional
    public Result<Long> removeContact(AuthToken token, long id) {

        Person person = personDAO.get(id);

        if (person == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        if (personIsEmployee( person.getCompanyId() )) {
            return error(En_ResultStatus.NOT_AVAILABLE);
        }

        person.setDeleted(true);

        boolean result = personDAO.merge(person);

        if (!result) {
            return error(En_ResultStatus.NOT_UPDATED);
        }

        jdbcManyRelationsHelper.fill(person, Person.Fields.CONTACT_ITEMS);
        removePersonEmailsFromCompany(person);
        userLoginDAO.removeByPersonId(id);

        return ok(id);
    }

    private boolean validatePerson(Person person) {
        if (person.isFired()) {
            log.warn("avoid to update fired person with id = {}", person.getId());
            return false;
        }

        if (person.isDeleted()) {
            log.warn("avoid to update deleted person with id = {}", person.getId());
            return false;
        }

        if (personIsEmployee( person.getCompanyId() )) {
            log.warn("person with id = {} is employee", person.getId());
            return false;
        }

        if (StringUtils.isBlank(person.getFirstName())) {
            return false;
        }

        if (StringUtils.isBlank(person.getLastName())) {
            return false;
        }

        if (person.getCompanyId() == null) {
            return false;
        }

        // prevent change of isfired and isdeleted attrs via ContactService.saveContact() method
        // to change that attrs, follow ContactService.fireContact() and ContactService.removeContact() methods
        if (person.getId() != null) {
            Person personOld = personDAO.get(person.getId());
            if (personOld.isFired() != person.isFired()) {
                log.warn("prevented change of person.isFired attr, person with id = {}", person.getId());
                return false;
            }

            if (personOld.isDeleted() != person.isDeleted()) {
                log.warn("prevented change of person.isDeleted attr, person with id = {}", person.getId());
                return false;
            }
        }

        return true;
    }

    private void removePersonEmailsFromCompany(Person person) {

        if (person == null || person.getCompanyId() == null) {
            return;
        }

        List<String> emails = new PlainContactInfoFacade(person.getContactInfo())
                .emailsStream()
                .map(ContactItem::value)
                .collect(Collectors.toList());

        if (emails.isEmpty()) {
            return;
        }

        int removed = companySubscriptionDAO.removeByCondition(
                "company_id = ? and email_addr in " +
                HelperFunc.makeInArg(emails),
                person.getCompanyId()
        );

        if (removed > 0) {
            log.debug("person({}) : removed email(s) from company with id {}", person.getDisplayName(), person.getCompanyId());
        }
    }

    private Boolean personIsEmployee( Long personCompanyId ) {
        return companyGroupHomeDAO.isHomeCompany( personCompanyId );
    }
}
