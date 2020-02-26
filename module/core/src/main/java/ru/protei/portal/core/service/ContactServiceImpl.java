package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.CompanySubscriptionDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dao.UserLoginDAO;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.core.model.struct.ContactItem;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.api.struct.Result.error;

/**
 * Реализация сервиса управления контактами
 */
public class ContactServiceImpl implements ContactService {

    private static Logger log = LoggerFactory.getLogger(CompanyServiceImpl.class);

    @Autowired
    PersonDAO personDAO;

    @Autowired
    UserLoginDAO userLoginDAO;

    @Autowired
    CompanySubscriptionDAO companySubscriptionDAO;

    @Autowired
    PolicyService policyService;

    @Override
    public Result<SearchResult<Person>> getContactsSearchResult( AuthToken token, ContactQuery query) {
        SearchResult<Person> sr = personDAO.getContactsSearchResult(query);
        return ok(sr);
    }

    @Override
    public Result<List<PersonShortView>> shortViewList( AuthToken token, ContactQuery query) {
        List<Person> list = personDAO.getContacts(query);

        if (list == null)
            return error(En_ResultStatus.GET_DATA_ERROR);

        List<PersonShortView> result = list.stream().map(Person::toShortNameShortView ).collect(Collectors.toList());

        return ok(result);
    }

    @Override
    public Result<Person> getContact( AuthToken token, long id ) {

        Person person = personDAO.getContact(id);

        return person != null ? ok( person)
                : error( En_ResultStatus.NOT_FOUND);
    }

    @Override
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

        if (personDAO.saveOrUpdate(person)) {
            return ok(person);
        }

        return error(En_ResultStatus.INTERNAL_ERROR);
    }

    @Override
    @Transactional
    public Result<Boolean> fireContact( AuthToken token, long id) {

        Person person = personDAO.getContact(id);

        if (person == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        person.setFired(true);

        boolean result = personDAO.merge(person);

        if (result) {
            removePersonEmailsFromCompany(person);
            userLoginDAO.removeByPersonId(id);
        }

        return ok(result);
    }

    @Override
    @Transactional
    public Result<Boolean> removeContact( AuthToken token, long id) {

        Person person = personDAO.getContact(id);

        if (person == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        person.setDeleted(true);

        boolean result = personDAO.merge(person);

        if (result) {
            removePersonEmailsFromCompany(person);
            userLoginDAO.removeByPersonId(id);
        }

        return ok(result);
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

        if (personDAO.isEmployee(person)) {
            log.warn("person with id = {} is employee",person.getId());
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
            Person personOld = personDAO.getContact(person.getId());
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
}
