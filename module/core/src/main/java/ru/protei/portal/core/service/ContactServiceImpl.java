package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.CompanySubscriptionDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dao.UserLoginDAO;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.core.model.struct.ContactItem;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.ok;
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
    public Result<Person> saveContact( AuthToken token, Person p ) {
        if (personDAO.isEmployee(p)) {
            log.warn("person with id = {} is employee",p.getId());
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        if (HelperFunc.isEmpty(p.getFirstName()) || HelperFunc.isEmpty(p.getLastName())
                || p.getCompanyId() == null)
            return error(En_ResultStatus.VALIDATION_ERROR);

        // prevent change of isfired and isdeleted attrs via ContactService.saveContact() method
        // to change that attrs, follow ContactService.fireContact() and ContactService.removeContact() methods
        if (p.getId() != null) {
            Person personOld = personDAO.getContact(p.getId());
            if (personOld.isFired() != p.isFired()) {
                log.warn("prevented change of person.isFired attr, person with id = {}", p.getId());
                return error(En_ResultStatus.VALIDATION_ERROR);
            }
            if (personOld.isDeleted() != p.isDeleted()) {
                log.warn("prevented change of person.isDeleted attr, person with id = {}", p.getId());
                return error(En_ResultStatus.VALIDATION_ERROR);
            }
        }

        if (HelperFunc.isEmpty(p.getDisplayName())) {
            p.setDisplayName(p.getLastName() + " " + p.getFirstName());
        }

        if (HelperFunc.isEmpty(p.getDisplayShortName())) {
            StringBuilder b = new StringBuilder();
            b.append (p.getLastName()).append(" ")
                    .append (p.getFirstName().substring(0,1).toUpperCase()).append(".")
            ;

            if (!p.getSecondName().isEmpty()) {
                b.append(" ").append(p.getSecondName().substring(0,1).toUpperCase()).append(".");
            }

            p.setDisplayShortName(b.toString());
        }

        if (p.getCreated() == null)
            p.setCreated(new Date());

        if (p.getCreator() == null)
            p.setCreator("service");

        if (p.getGender() == null)
            p.setGender(En_Gender.UNDEFINED);

        if (personDAO.saveOrUpdate(p)) {
            return ok(p);
        }

        return error(En_ResultStatus.INTERNAL_ERROR);
    }

    @Override
    public Result<Boolean> fireContact( AuthToken token, long id) {

        Person person = personDAO.getContact(id);

        if (person == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        person.setFired(true);

        boolean result = personDAO.merge(person);

        if (result) {
            removePersonEmailsFromCompany(person);
        }

        return ok(result);
    }

    @Override
    public Result<Boolean> removeContact( AuthToken token, long id) {

        Person person = personDAO.getContact(id);

        if (person == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        person.setDeleted(true);

        boolean result = personDAO.merge(person);

        if (result) {
            removePersonEmailsFromCompany(person);
        }

        return ok(result);
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
