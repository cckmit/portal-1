package ru.protei.portal.test.legacy;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.portal.tools.migrate.sybase.LegacySystemDAO;
import ru.protei.portal.tools.migrate.struct.ExternalCompany;
import ru.protei.portal.tools.migrate.struct.ExternalPerson;
import ru.protei.portal.tools.migrate.struct.ExternalProduct;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.sql.SQLException;

public class LegacyDAO_Test {
    static ApplicationContext ctx;

    static long TEST_PERSON_EXISTS = 18L;
    static long TEST_PRODUCT_EXISTS = 1L;

    @BeforeClass
    public static void init () {
        ctx = new AnnotationConfigApplicationContext(CoreConfigurationContext.class, JdbcConfigurationContext.class, MainConfiguration.class);
    }

    @Test
    public void testGet001 () throws SQLException {

        LegacySystemDAO dao = ctx.getBean(LegacySystemDAO.class);

        Assert.assertTrue(dao.isExistsPerson(TEST_PERSON_EXISTS));
        ExternalPerson person = dao.getExternalPerson(TEST_PERSON_EXISTS);

        Assert.assertNotNull(person);
        System.out.println(person);

        ExternalCompany company = dao.getExternalCompany(person.getCompanyId());
        Assert.assertNotNull(company);
        System.out.println(company);

        ExternalProduct product = dao.getExternalProduct(TEST_PRODUCT_EXISTS);

        Assert.assertNotNull(product);
        System.out.println(product);
    }

}
