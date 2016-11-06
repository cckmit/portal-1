package ru.protei.portal.test.service;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.core.service.ContactService;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.List;

/**
 * Created by michael on 11.10.16.
 */
public class ContactServiceTest {

    static ApplicationContext ctx;

    @BeforeClass
    public static void init () {
         ctx = new AnnotationConfigApplicationContext (CoreConfigurationContext.class, JdbcConfigurationContext.class, MainConfiguration.class);
    }


    @Test
    public void testGetById () {
        ContactService service = ctx.getBean(ContactService.class);

        Assert.assertNotNull(service);


        CoreResponse<Person> response = service.getContact(1001L);

        Assert.assertTrue(response.isOk());

        Assert.assertNotNull(response.getData());
        Assert.assertNotNull(response.getData().getCompany());

        System.out.println(response.getData().getCompany());
    }

    @Test
    public void testGetByFilter() {

        ContactService service = ctx.getBean(ContactService.class);

        Assert.assertNotNull(service);

        CoreResponse<List<Person>> result = service.contactList(new ContactQuery((Long)null, "Михаил", En_SortField.person_full_name, En_SortDir.ASC));

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isOk());
        Assert.assertNotNull(result.getData());
        Assert.assertTrue(result.getData().size() > 0);

        for (Person person : result.getData()) {
            CoreResponse<Person> x = service.getContact( person.getId() );
            Assert.assertTrue(x.isOk());
            Assert.assertEquals(person.getId(), x.getData().getId());
            Assert.assertEquals(person.getDisplayName(), x.getData().getDisplayName());

//            Assert.assertNull(person.getCompany());
            Assert.assertNotNull(x.getData().getCompany());
        }
    }
}
