package ru.protei.portal.test.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.IntegrationTestsConfiguration;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.core.service.ContactService;
import ru.protei.portal.embeddeddb.DatabaseConfiguration;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

/**
 * Created by michael on 11.10.16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class, JdbcConfigurationContext.class,
        DatabaseConfiguration.class, IntegrationTestsConfiguration.class
})
public class ContactServiceTest extends BaseServiceTest {

    @Test
    public void testGetById() {

        Company company = createNewCustomerCompany();
        company.setId(companyDAO.persist(company));
        Person person = createNewPerson(company);
        person.setId(personDAO.persist(person));

        Assert.assertNotNull(person.getId());

        Result<Person> response = service.getContact(getAuthToken(), person.getId());

        Assert.assertTrue(response.isOk());
        Assert.assertNotNull(response.getData());

        Assert.assertTrue(personDAO.remove(person));
        Assert.assertTrue(companyDAO.remove(company));
    }

    @Test
    public void testGetByFilter() {

        Company company = createNewCompany( En_CompanyCategory.PARTNER );
        company.setId(companyDAO.persist(company));
        Person person = createNewPerson(company);
        person.setId(personDAO.persist(person));

        Assert.assertNotNull(person.getId());

        ContactQuery query = new ContactQuery((Long) null, null, person.getDisplayName(), En_SortField.person_full_name, En_SortDir.ASC);
        Result<SearchResult<Person>> result = service.getContactsSearchResult(getAuthToken(), query);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isOk());
        Assert.assertNotNull(result.getData());
        Assert.assertNotNull(result.getData().getResults());
        Assert.assertTrue(result.getData().getResults().size() > 0);

        for (Person p : result.getData()) {
            Result<Person> x = service.getContact(getAuthToken(), p.getId());
            Assert.assertTrue(x.isOk());
            Assert.assertEquals(p.getId(), x.getData().getId());
            Assert.assertEquals(p.getDisplayName(), x.getData().getDisplayName());
        }

        Assert.assertTrue(personDAO.remove(person));
        Assert.assertTrue(companyDAO.remove(company));
    }

    @Autowired
    ContactService service;
}
