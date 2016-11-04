package ru.protei.portal.test.model;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.test.model.config.TestConfiguration;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

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

        Long id = dao.persist(p);
        Assert.assertNotNull(id);
        Assert.assertTrue(id > 0);

        Assert.assertNotNull(p.getId());

        dao.remove(p);
    }

}
