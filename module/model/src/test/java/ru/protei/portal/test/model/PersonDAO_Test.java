package ru.protei.portal.test.model;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_ContactDataAccess;
import ru.protei.portal.core.model.dict.En_ContactItemType;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.struct.ContactItem;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
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

        ContactItem item = person.getContactInfo().findFirst(En_ContactItemType.EMAIL, En_ContactDataAccess.PUBLIC);

        System.out.println(item.comment());
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

        p.getContactInfo().addItem(En_ContactItemType.EMAIL).modify("junit@test.org", "test-email");
        p.getContactInfo().addItem(En_ContactItemType.FAX).modify("999-22-33-11", "work fax");
        p.getContactInfo().addItem(En_ContactItemType.MOBILE_PHONE).modify("+7-921-555-44-33", "main phone");
        p.getContactInfo().addItem(En_ContactItemType.GENERAL_PHONE).modify("8(812)-4494727", "protei");
        p.getContactInfo().addItem(En_ContactItemType.ICQ).modify("00000000001");
        p.getContactInfo().addItem(En_ContactItemType.JABBER).modify("dev@jabber.protei.ru");
        p.getContactInfo().addItem(En_ContactItemType.WEB_SITE).modify("http://www.protei.ru");

        Long id = dao.persist(p);
        Assert.assertNotNull(id);
        Assert.assertTrue(id > 0);

        Assert.assertNotNull(p.getId());

        Person readPerson = dao.get(p.getId());

        Assert.assertNotNull(readPerson);
        Assert.assertNotNull(readPerson.getCompany());
        Assert.assertNotNull(readPerson.getContactInfo());

        PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(readPerson.getContactInfo());

        Assert.assertEquals("junit@test.org", infoFacade.getEmail());
        Assert.assertEquals("dev@jabber.protei.ru", infoFacade.getJabber());
        Assert.assertEquals("8(812)-4494727", infoFacade.getWorkPhone());
        Assert.assertEquals("+7-921-555-44-33", infoFacade.getMobilePhone());

        dao.remove(readPerson);
    }

}
