package ru.protei.portal.test.service;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.api.struct.HttpListResult;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.portal.core.model.dao.CompanyGroupItemDAO;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.core.model.ent.CompanyGroupItem;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.service.dict.CompanyService;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

/**
 * Created by michael on 11.10.16.
 */
public class CompanyServiceTest {

    static ApplicationContext ctx;

    @BeforeClass
    public static void init () {
         ctx = new AnnotationConfigApplicationContext (CoreConfigurationContext.class, JdbcConfigurationContext.class, MainConfiguration.class);
    }


    @Test
    public void testGetCompanyList () {

        CompanyService service = ctx.getBean(CompanyService.class);

        Assert.assertNotNull(service);

        HttpListResult<Company> result = service.list(new CompanyQuery());

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getItems());
        Assert.assertTrue(result.getTotalSize() > 0);

//        for (Company company : result.getItems())
//            System.out.println(company.getCname());
    }


    @Test
    public void testCompanyGroups () {

        try {
            CompanyService service = ctx.getBean(CompanyService.class);

            CoreResponse<CompanyGroup> resp = service.createGroup("Test-Group", "A group for test");

            Assert.assertTrue(resp.isOk());
            Assert.assertNotNull(resp.getData());

            CompanyGroup testGroup = resp.getData();
            Company proteiCompany = service.getProfile(1L);

            Assert.assertNotNull(proteiCompany);

            CoreResponse<CompanyGroupItem> linkResult = service.addCompanyToGroup(testGroup.getId(), proteiCompany.getId());

            Assert.assertTrue(linkResult.isOk());
            Assert.assertNotNull(linkResult.getData());

            proteiCompany = service.getProfile(1L);

            Assert.assertNotNull(proteiCompany.getGroups());
            Assert.assertTrue(proteiCompany.getGroups().size() > 0);

            System.out.println(proteiCompany.getGroups().get(0).getName());
        }
        finally {
            ctx.getBean(CompanyGroupItemDAO.class).removeByCondition("company_id=?", 1L);
        }
    }

}
