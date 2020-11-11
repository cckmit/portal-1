package ru.protei.portal.redmine.utils;

import com.taskadapter.redmineapi.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.core.model.dao.ContactItemDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static ru.protei.portal.core.model.helper.CollectionUtils.getFirst;
import static ru.protei.portal.core.model.helper.CollectionUtils.setOf;

public class CachedPersonMapper {

    private PersonDAO personDAO;
    private ContactItemDAO contactItemDAO;
    private JdbcManyRelationsHelper jdbcManyRelationsHelper;
    private Long companyId;
    private Long defaultUserLocalId;
    private Person defaultEntryPointUser;
    private final Map<String, Person> index;

    public CachedPersonMapper(PersonDAO personDAO, ContactItemDAO contactItemDAO, JdbcManyRelationsHelper jdbcManyRelationsHelper, Long companyId, Long defaultUserLocalId, Person defUser) {
        this.personDAO = personDAO;
        this.contactItemDAO = contactItemDAO;
        this.jdbcManyRelationsHelper = jdbcManyRelationsHelper;
        this.companyId = companyId;
        this.defaultUserLocalId = defaultUserLocalId;
        this.defaultEntryPointUser = defUser;
        this.index = new HashMap<>();
    }

    public Person toProteiPerson(User user) {
        if (user == null) {
            if (defaultEntryPointUser == null) {
                defaultEntryPointUser = personDAO.get(defaultUserLocalId);
                jdbcManyRelationsHelper.fill(defaultEntryPointUser, Person.Fields.CONTACT_ITEMS);
            }
            return defaultEntryPointUser;
        }

        Person person = index.getOrDefault(emailKey(user), index.get(nameKey(user)));

        if (person == null) {
            person = mapPerson(companyId, user);
            if (HelperFunc.isNotEmpty(user.getMail()))
                index.put(emailKey(user), person);

            if (HelperFunc.isNotEmpty(user.getFullName()))
                index.put(nameKey(user), person);
        }

        if (person.getId() != null) {
            jdbcManyRelationsHelper.fill(person, Person.Fields.CONTACT_ITEMS);
        }

        return person;
    }

    public static boolean isTechUser(Integer defaultUserID, User user) {
        return user != null && Objects.equals(defaultUserID, user.getId());
    }

    private String emailKey (User user) {
        return HelperFunc.isNotEmpty(user.getMail()) ? "email:" + user.getMail() : "N";
    }

    private String nameKey (User user) {
        return HelperFunc.isNotEmpty(user.getFullName()) ? "name:" + user.getFullName() : "N";
    }

    private Person mapPerson (Long companyId, User user) {
        Person person = null;

        if (HelperFunc.isNotEmpty(user.getMail())) {
            // email не уникальное поле. Может быть несколько записей контактов с одним email.
            PersonQuery personQuery = new PersonQuery();
            personQuery.setCompanyIds( setOf( companyId ) );
            personQuery.setEmail( user.getMail() );
            personQuery.setLimit( 1 );
            person = getFirst( personDAO.getPersons( personQuery ) );
        }

        if (person == null) {
            person = personDAO.findContactByName(companyId, user.getFullName());
        }

        if (person == null) {
            person = createPersonForRedmineUser(companyId, user);
        }

        return person;
    }

    private Person createPersonForRedmineUser(Long companyId, User user) {

        Person person = null;

        if (HelperFunc.isNotEmpty(user.getMail())) {
            // try find by e-mail
            // email не уникальное поле. Может быть несколько записей контактов с одним email.
            PersonQuery personQuery = new PersonQuery();
            personQuery.setCompanyIds( setOf( companyId ) );
            personQuery.setEmail( user.getMail() );
            personQuery.setLimit( 1 );
            person = getFirst( personDAO.getPersons( personQuery ) );
        }

        if (person == null && HelperFunc.isNotEmpty(user.getFullName())) {
            // try find by name
            person = personDAO.findContactByName(companyId, user.getFullName());
        }

        if (person != null) {
            logger.debug("contact found: {} (id={})", person.getDisplayName(), person.getId());
        } else {
            logger.debug("unable to find contact person : email={}, company={}, create new one", user.getMail(), companyId);

            person = new Person();
            person.setCreated(new Date());
            person.setCreator("redmine");
            person.setCompanyId(companyId);
            if (HelperFunc.isEmpty(user.getFirstName()) && HelperFunc.isEmpty(user.getLastName())) {
                person.setFirstName(STUB_NAME);
                person.setLastName(STUB_NAME);
                person.setSecondName(STUB_NAME);
                person.setDisplayName(STUB_NAME);
                person.setDisplayShortName(STUB_NAME);
            } else {
                String[] np = user.getFullName().split("\\s+");
                person.setLastName(np[0]);
                person.setFirstName(np.length > 1 ? np[1] : STUB_NAME);
                person.setSecondName(np.length > 2 ? np[2] : "");
                person.setDisplayName(user.getFullName());
                person.setDisplayShortName(getDisplayShortName(person));
            }

            PlainContactInfoFacade contactInfoFacade = new PlainContactInfoFacade();
            if (user.getMail() != null) {
                contactInfoFacade.setEmail(user.getMail());
            }
            person.setContactInfo(contactInfoFacade.editInfo());

            person.setGender(En_Gender.UNDEFINED);
            person.setDeleted(false);
            person.setFired(false);
            personDAO.persist(person);
            contactItemDAO.saveOrUpdateBatch(person.getContactItems());
            jdbcManyRelationsHelper.persist(person, Person.Fields.CONTACT_ITEMS);
        }

        return person;
    }

    private String getDisplayShortName(Person person) {
        return person.getLastName() + " "
                + getShortName(person.getFirstName())
                + getShortName(person.getSecondName());
    }
    private String getShortName(String name) {
        if (HelperFunc.isEmpty(name) || name.equals(STUB_NAME))
            return "";
        if (name.length() >= 1 ) {
            return name.substring(0, 1) + ". ";
        } else {
            return name + " ";
        }
    }

    private final static String STUB_NAME = "?";
    private final static Logger logger = LoggerFactory.getLogger(CachedPersonMapper.class);
}
