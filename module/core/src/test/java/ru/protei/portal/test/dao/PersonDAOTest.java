package ru.protei.portal.test.dao;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.config.DaoTestsConfiguration;
import ru.protei.portal.embeddeddb.DatabaseConfiguration;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_ContactDataAccess;
import ru.protei.portal.core.model.dict.En_ContactItemType;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.struct.ContactItem;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.Date;

/**
 * Created by Mike on 03.11.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class, JdbcConfigurationContext.class,
        DatabaseConfiguration.class, DaoTestsConfiguration.class})
public class PersonDAOTest {

    public Person createTestPerson () {
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

        personDAO.persist(p);
        return p;
    }

    @Test
    public void testGetPerson () {

        Long testId  = createTestPerson().getId();
        try {
            Person person = personDAO.get(testId);

            Assert.assertNotNull(person);
            Assert.assertNotNull(person.getCompany());

            ContactItem item = person.getContactInfo().findFirst(En_ContactItemType.EMAIL, En_ContactDataAccess.PUBLIC);

            log.info(item.comment());
        } finally {
            personDAO.removeByKey(testId);
        }
    }

    @Test
    public void testGetPlainPerson () {

        Long testId  = createTestPerson().getId();
        try {
            Person person = personDAO.plainGet(testId);
            Assert.assertNotNull(person);
            Assert.assertNull(person.getCompany());
        } finally {
            personDAO.removeByKey(testId);
        }
    }

    @Test
    public void testInsert () {

        Person p = createTestPerson();
        Long id = p.getId();

        Assert.assertNotNull(id);
        Assert.assertTrue(id > 0);

        Assert.assertNotNull(p.getId());

        Person readPerson = personDAO.get(p.getId());

        Assert.assertNotNull(readPerson);
        Assert.assertNotNull(readPerson.getCompany());
        Assert.assertNotNull(readPerson.getContactInfo());

        PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(readPerson.getContactInfo());

        Assert.assertEquals("junit@test.org", infoFacade.getEmail());
        Assert.assertEquals("dev@jabber.protei.ru", infoFacade.getJabber());
        Assert.assertEquals("8(812)-4494727", infoFacade.getWorkPhone());
        Assert.assertEquals("+7-921-555-44-33", infoFacade.getMobilePhone());

        personDAO.remove(readPerson);
    }

    @Autowired
    PersonDAO personDAO;

    private static final Logger log = LoggerFactory.getLogger(PersonDAOTest.class);
}
