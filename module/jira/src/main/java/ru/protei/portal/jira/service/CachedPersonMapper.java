package ru.protei.portal.jira.service;

import com.atlassian.jira.rest.client.api.domain.User;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.ent.JiraEndpoint;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CachedPersonMapper implements PersonMapper {

//    private JiraIntegrationService jiraIntegrationService;
    private PersonDAO personDAO;
    private final JiraEndpoint endpoint;
    private Person defaultEntryPointUser;
    private final Map<String, Person> index;

    public CachedPersonMapper(PersonDAO personDAO, JiraEndpoint endpoint, Person defUser) {
        this.personDAO = personDAO;
        this.endpoint = endpoint;
        this.defaultEntryPointUser = defUser;
        this.index = new HashMap<>();
    }


    private String emailKey (User user) {
        return HelperFunc.isNotEmpty(user.getEmailAddress()) ? "email:" + user.getEmailAddress() : "N";
    }

    private String nameKey (User user) {
        return HelperFunc.isNotEmpty(user.getDisplayName()) ? "name:" + user.getDisplayName() : "N";
    }

    @Override
    public Person toProteiPerson(User jiraUser) {
        if (jiraUser == null) {
            if (defaultEntryPointUser == null) {
                defaultEntryPointUser = personDAO.get(endpoint.getPersonId());
            }
            return defaultEntryPointUser;
        }

        Person person = index.getOrDefault(emailKey(jiraUser), index.get(nameKey(jiraUser)));

        if (person == null) {
            person = mapPerson(endpoint, jiraUser);
            if (HelperFunc.isNotEmpty(jiraUser.getEmailAddress()))
                index.put(emailKey(jiraUser), person);

            if (HelperFunc.isNotEmpty(jiraUser.getDisplayName()))
                index.put(nameKey(jiraUser), person);
        }

        return person;
    }


    private Person mapPerson (JiraEndpoint endpoint, User jiraUser) {
        Person person = null;

        if (HelperFunc.isNotEmpty(jiraUser.getEmailAddress())) {
            person = personDAO.findContactByEmail(endpoint.getCompanyId(), jiraUser.getEmailAddress());
        }

        if (person == null) {
            person = personDAO.findContactByName(endpoint.getCompanyId(), jiraUser.getDisplayName());
        }

        if (person == null) {
            person = createPersonForJiraUser(endpoint, jiraUser);
        }

        return person;
    }

    private Person createPersonForJiraUser(JiraEndpoint endpoint, User jiraUser) {
        Person person;// create one
        person = new Person();
        person.setCompanyId(endpoint.getCompanyId());
        person.setCreated(new Date());
        person.setCreator("jira-integration-service");
        person.setDeleted(false);
        person.setGender(En_Gender.UNDEFINED);
        person.setDisplayName(jiraUser.getDisplayName());
        person.setDisplayShortName(jiraUser.getDisplayName());
        person.setLocale("ru");

        PlainContactInfoFacade contactInfoFacade = new PlainContactInfoFacade();
        contactInfoFacade.setEmail(jiraUser.getEmailAddress());
        person.setContactInfo(contactInfoFacade.editInfo());

        personDAO.saveOrUpdate(person);
        return person;
    }
}
