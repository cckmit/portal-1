package ru.protei.portal.test.model;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.test.model.config.TestConfiguration;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.Date;

/**
 * Created by Mike on 03.11.2016.
 */
public class PersonDAO_Test {

    static ApplicationContext ctx;

    @BeforeClass
    public static void init () {
        ctx = new AnnotationConfigApplicationContext(CoreConfigurationContext.class, JdbcConfigurationContext.class, TestConfiguration.class);
    }

    @Test
    public void testGetPerson () {

        PersonDAO dao = ctx.getBean(PersonDAO.class);

        //  System.out.println(dao.getPojoColumns());

        Person person = dao.get(18L);
        Assert.assertNotNull(person);
        Assert.assertNotNull(person.getCompany());
    }


    @Test
    public void testGetPlainPerson () {

        PersonDAO dao = ctx.getBean(PersonDAO.class);

      //  System.out.println(dao.getPojoColumns());

        Person person = dao.plainGet(18L);
        Assert.assertNotNull(person);
        Assert.assertNull(person.getCompany());

    }


    @Test
    public void testInsert () {
        PersonDAO dao = ctx.getBean(PersonDAO.class);
        Person p = new Person();
        p.setCompanyId(1L);
        p.setFirstName("Test Create Person");
        p.setLastName("Test");
        p.setDisplayName("Test insert");
        p.setCreated(new Date());
        p.setCreator("");
        p.setGender(En_Gender.MALE);

        p.getContactInfo().addEmail("junit@test.org", "test-email");
        p.getContactInfo().addFax("999-22-33-11", "work fax");
        p.getContactInfo().addMobilePhone("+7-921-555-44-33", "main phone");
        p.getContactInfo().addPhone("8(812)-4494727", "protei");
        p.getContactInfo().icq = "00000000001";
        p.getContactInfo().jabber = "dev@jabber.protei.ru";
        p.getContactInfo().webSite = "http://www.protei.ru";

        Long id = dao.persist(p);
        Assert.assertNotNull(id);
        Assert.assertTrue(id > 0);

        Assert.assertNotNull(p.getId());

        Person readPerson = dao.get(p.getId());

        Assert.assertNotNull(readPerson);
        Assert.assertNotNull(readPerson.getCompany());
        Assert.assertNotNull(readPerson.getContactInfo());
        Assert.assertEquals("junit@test.org", readPerson.getContactInfo().defaultEmail());
        Assert.assertEquals("dev@jabber.protei.ru", readPerson.getContactInfo().jabber);
        Assert.assertEquals("8(812)-4494727", readPerson.getContactInfo().defaultWorkPhone());
        Assert.assertEquals("+7-921-555-44-33", readPerson.getContactInfo().defaultMobilePhone());

        dao.remove(p);
    }

}
