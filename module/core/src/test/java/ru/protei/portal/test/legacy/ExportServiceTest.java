package ru.protei.portal.test.legacy;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanyCategory;
import ru.protei.portal.tools.migrate.export.ExportDataService;
import ru.protei.portal.tools.migrate.struct.ExternalCompany;
import ru.protei.portal.tools.migrate.sybase.LegacySystemDAO;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.Date;

public class ExportServiceTest {
    static ApplicationContext ctx;

    static CompanyDAO companyDAO;

    static ExportDataService exportService;
    static LegacySystemDAO legacySystemDAO;


    static long EXISTING_COMPANY_ID = 2L;

    @BeforeClass
    public static void init () {
        ctx = new AnnotationConfigApplicationContext(CoreConfigurationContext.class, JdbcConfigurationContext.class, MainConfiguration.class);
        exportService = ctx.getBean(ExportDataService.class);
        legacySystemDAO = ctx.getBean(LegacySystemDAO.class);
        companyDAO = ctx.getBean(CompanyDAO.class);
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
        newCompany.setCname("junit-test");
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
