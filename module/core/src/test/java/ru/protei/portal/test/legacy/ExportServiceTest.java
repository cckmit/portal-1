package ru.protei.portal.test.legacy;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.config.DatabaseConfiguration;
import ru.protei.portal.config.MainTestsConfiguration;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.dao.DevUnitDAO;
import ru.protei.portal.core.model.dao.ExportSybEntryDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.tools.migrate.export.ExportDataService;
import ru.protei.portal.tools.migrate.struct.ExternalCompany;
import ru.protei.portal.tools.migrate.struct.ExternalPerson;
import ru.protei.portal.tools.migrate.struct.ExternalPersonExtension;
import ru.protei.portal.tools.migrate.struct.ExternalProduct;
import ru.protei.portal.tools.migrate.sybase.LegacySystemDAO;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class ExportServiceTest {
    public static final String JUNIT_TEST_NAME = "junit-test";
    public static final String EXPORT_INSTANCE_ID = "junit";
    static ApplicationContext ctx;

    static CompanyDAO companyDAO;
    static ExportDataService exportService;
    static LegacySystemDAO legacySystemDAO;
    static ExportSybEntryDAO exportSybEntryDAO;

    static DevUnitDAO devUnitDAO;
    static PersonDAO personDAO;


    static long EXISTING_COMPANY_ID = 2L;
    static long EXISTING_PRODUCT_ID = 1L;
    static long EXISTING_PERSON_ID = 1001L;

    @BeforeClass
    public static void init () throws Exception {
        ctx = new AnnotationConfigApplicationContext(CoreConfigurationContext.class, JdbcConfigurationContext.class, DatabaseConfiguration.class, MainTestsConfiguration.class);
        exportService = ctx.getBean(ExportDataService.class);
        legacySystemDAO = ctx.getBean(LegacySystemDAO.class);
        companyDAO = ctx.getBean(CompanyDAO.class);
        personDAO = ctx.getBean(PersonDAO.class);
        devUnitDAO = ctx.getBean(DevUnitDAO.class);
        exportSybEntryDAO = ctx.getBean(ExportSybEntryDAO.class);
        cleanUp();
    }

    @AfterClass
    public static void cleanUp () throws Exception {
        devUnitDAO.removeByCondition("UTYPE_ID=? and UNIT_NAME=?", En_DevUnitType.PRODUCT.getId(), JUNIT_TEST_NAME);
        companyDAO.removeByCondition("cname=?", JUNIT_TEST_NAME);
        personDAO.removeByCondition("company_id=? and displayname=?", EXISTING_COMPANY_ID, JUNIT_TEST_NAME);
        exportSybEntryDAO.removeByCondition("instance_id=?", EXPORT_INSTANCE_ID);

        legacySystemDAO.runAction(transaction -> {
            transaction.dao(ExternalCompany.class).delete("strName=?", JUNIT_TEST_NAME);
            transaction.dao(ExternalProduct.class).delete("strValue=?", JUNIT_TEST_NAME);

            List<ExternalPerson> extPersons = transaction.dao(ExternalPerson.class).list("nCompanyID=? and strFirstName=?", EXISTING_COMPANY_ID, JUNIT_TEST_NAME);

            for (ExternalPerson e : extPersons)
                transaction.dao(ExternalPersonExtension.class).delete(e.getId());

            transaction.dao(ExternalPerson.class).delete(extPersons);

            transaction.commit();
            return true;
        });
    }


    @Test
    public void testExportEntrySerialization () {
        Person person = personDAO.get(EXISTING_PERSON_ID);
        ExportSybEntry entry = new ExportSybEntry(person, EXPORT_INSTANCE_ID);

        Assert.assertTrue(exportSybEntryDAO.persist(entry) > 0L);

        entry = exportSybEntryDAO.get(entry.getId());
        Assert.assertNotNull(entry);
        Assert.assertNotNull(entry.getEntry());
        Assert.assertTrue(entry.getEntry() instanceof Person);
    }

    @Test
    public void testExportPerson_new () throws SQLException {
        Company company = companyDAO.get(EXISTING_COMPANY_ID);
        Assert.assertNotNull("precondition", company);

        Person person = new Person();
        person.setCompany(company);

        person.setFirstName(JUNIT_TEST_NAME);
        person.setLastName(JUNIT_TEST_NAME);
        person.setPosition("position");
        person.setBirthday(new Date());
        person.setCreated(new Date());
        person.setCreator("junit");
        person.setDisplayName(JUNIT_TEST_NAME);
        person.setDisplayShortName(JUNIT_TEST_NAME);
        person.setGender(En_Gender.MALE);
        person.setPassportInfo("passport");
        person.setIpAddress("127.0.0.1");

        Assert.assertNotNull(personDAO.persist(person));
        Assert.assertEquals(En_ResultStatus.OK, exportService.exportPerson(person));
        Assert.assertNotNull(person.getOldId());

        ExternalPerson externalPerson = legacySystemDAO.getExternalPerson(person.getOldId());

        Assert.assertNotNull(externalPerson);
        Assert.assertEquals(JUNIT_TEST_NAME, externalPerson.getFirstName());
        Assert.assertEquals(JUNIT_TEST_NAME, externalPerson.getLastName());
        Assert.assertEquals("passport", externalPerson.getPassportInfo());
        Assert.assertEquals(En_Gender.MALE, externalPerson.getGender());
        Assert.assertNotNull(externalPerson.getCreated());
        Assert.assertNotNull(externalPerson.getBirthday());
    }

    @Test
    public void testExportPerson_existing () throws SQLException {
        Person person = personDAO.get(EXISTING_PERSON_ID);
        Assert.assertNotNull("precondition", person);

        String origName = person.getFirstName();
        String modifiedName = origName + "_modified";

        person.setFirstName(modifiedName);
        Assert.assertEquals(En_ResultStatus.OK, exportService.exportPerson(person));

        ExternalPerson externalPerson = legacySystemDAO.getExternalPerson(EXISTING_PERSON_ID);
        Assert.assertNotNull(externalPerson);
        Assert.assertEquals(modifiedName, externalPerson.getFirstName());

        // restore
        person.setFirstName(origName);
        Assert.assertEquals(En_ResultStatus.OK, exportService.exportPerson(person));
    }

    @Test
    public void testExportProduct_new () throws SQLException {
        DevUnit product = new DevUnit(En_DevUnitType.PRODUCT, JUNIT_TEST_NAME, "junit-test-info");
        devUnitDAO.persist(product);

        Assert.assertEquals(En_ResultStatus.OK, exportService.exportProduct(product));
        Assert.assertNotNull(product.getOldId());

        ExternalProduct externalProduct = legacySystemDAO.getExternalProduct(product.getOldId());
        Assert.assertNotNull(externalProduct);
        Assert.assertEquals(product.getName(), externalProduct.getName());
    }


    @Test
    public void textExportProduct_update () throws SQLException {
        DevUnit product = devUnitDAO.getByLegacyId(En_DevUnitType.PRODUCT, EXISTING_PRODUCT_ID);
        Assert.assertNotNull("precondition", product);

        String origName = product.getName();

        String modifiedName = origName + "_modified";

        product.setName(modifiedName);

        Assert.assertEquals(En_ResultStatus.OK, exportService.exportProduct(product));

        ExternalProduct externalProduct = legacySystemDAO.getExternalProduct(EXISTING_PRODUCT_ID);

        Assert.assertEquals(modifiedName, externalProduct.getName());

        // restore
        product.setName(origName);
        Assert.assertEquals(En_ResultStatus.OK, exportService.exportProduct(product));

        externalProduct = legacySystemDAO.getExternalProduct(EXISTING_PRODUCT_ID);
        Assert.assertEquals(origName, externalProduct.getName());
    }

    @Test
    public void testExportExistingCompany () throws Exception {
        Company company = companyDAO.get(EXISTING_COMPANY_ID);
        Assert.assertNotNull(company);

        ExternalCompany extern = legacySystemDAO.getExternalCompany(company.getOldId());
        Assert.assertNotNull(extern);

        System.out.println(company.getCname());

        String origName = company.getCname();

        String modifiedName = origName + "_modified";
        company.setCname(modifiedName);

        En_ResultStatus resultStatus = exportService.exportCompany(company);
        Assert.assertTrue(resultStatus == En_ResultStatus.OK);

        extern = legacySystemDAO.getExternalCompany(company.getOldId());

        Assert.assertEquals(modifiedName, extern.getName());

        company.setCname(origName);

        Assert.assertEquals(En_ResultStatus.OK, exportService.exportCompany(company));
    }


    @Test
    public void testExportNewCompany () throws Exception {


        Company newCompany = new Company();
        newCompany.setCname(JUNIT_TEST_NAME);
        newCompany.setCreated(new Date());
        newCompany.setInfo("junit-test-info");
        newCompany.setCategory(new CompanyCategory(En_CompanyCategory.CUSTOMER.getId()));

        companyDAO.persist(newCompany);

        Assert.assertEquals(En_ResultStatus.OK, exportService.exportCompany(newCompany));

        newCompany = companyDAO.get(newCompany.getId());

        Assert.assertNotNull(newCompany.getOldId());

        ExternalCompany extern = legacySystemDAO.getExternalCompany(newCompany.getOldId());

        Assert.assertNotNull(extern);
    }
}
