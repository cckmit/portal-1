package ru.protei.portal.test.legacy;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.config.IntegrationTestsConfiguration;
import ru.protei.portal.core.model.ent.LegacyEntity;
import ru.protei.portal.tools.migrate.struct.*;
import ru.protei.portal.tools.migrate.sybase.LegacySystemDAO;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@Ignore
public class LegacyDAO_Test {
    private static final Logger log = LoggerFactory.getLogger(LegacyDAO_Test.class);
    static ApplicationContext ctx;

    static long TEST_PERSON_EXISTS = 18L;
    static long TEST_PRODUCT_EXISTS = 1L;

    @BeforeClass
    public static void init () {
        ctx = new AnnotationConfigApplicationContext(CoreConfigurationContext.class, JdbcConfigurationContext.class, IntegrationTestsConfiguration.class);
    }

    @Test
    public void testGet001 () throws SQLException {

        LegacySystemDAO dao = ctx.getBean(LegacySystemDAO.class);

        Assert.assertTrue(dao.isExistsPerson(TEST_PERSON_EXISTS));
        ExternalPerson person = dao.getExternalPerson(TEST_PERSON_EXISTS);

        Assert.assertNotNull(person);
        log.info("{}", person);

        ExternalCompany company = dao.getExternalCompany(person.getCompanyId());
        Assert.assertNotNull(company);
        log.info("{}", company);

        ExternalProduct product = dao.getExternalProduct(TEST_PRODUCT_EXISTS);

        Assert.assertNotNull(product);
        log.info("{}", product);
    }

    @Test
    public void testGetAllEnt () throws SQLException {
        LegacySystemDAO dao = ctx.getBean(LegacySystemDAO.class);

        List<Class<? extends LegacyEntity>> typesOnTest = Arrays.asList(
                        ExternalPerson.class, ExternalPersonExtension.class, ExternalProduct.class,
                ExternalCompany.class,
                ExtCrmComment.class,
                ExtCrmSession.class,
                ExtCompanyEmailSubs.class,
                ExtContactLogin.class,
                ExternalSubnet.class,
                ExternalReservedIp.class
        );

        dao.runAction(transaction -> {
            for (Class<? extends LegacyEntity> t : typesOnTest) {
                List<?> res = transaction.dao(t).list(100);
                Assert.assertNotNull(res);
                Assert.assertFalse(res.isEmpty());
                System.out.println(t.getName() + ", got list of " + res.size() );
            }

            return true;
        });
    }
}
