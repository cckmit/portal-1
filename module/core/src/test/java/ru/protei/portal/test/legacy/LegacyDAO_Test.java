package ru.protei.portal.test.legacy;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.portal.tools.migrate.LegacySystemDAO;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.sql.SQLException;

public class LegacyDAO_Test {
    static ApplicationContext ctx;

    @BeforeClass
    public static void init () {
        ctx = new AnnotationConfigApplicationContext(CoreConfigurationContext.class, JdbcConfigurationContext.class, MainConfiguration.class);
    }


    @Test
    public void testGetPerson () throws SQLException {

        LegacySystemDAO dao = ctx.getBean(LegacySystemDAO.class);

        Assert.assertTrue(dao.isExistsPerson(18L));
        Assert.assertNotNull(dao.getExternalPerson(18L));
        System.out.println(dao.getExternalPerson(18L));
    }
}
