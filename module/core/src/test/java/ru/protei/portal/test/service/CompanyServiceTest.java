package ru.protei.portal.test.service;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.api.struct.HttpListResult;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.dao.CompanyGroupDAO;
import ru.protei.portal.core.model.dao.CompanyGroupItemDAO;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.core.model.ent.CompanyGroupItem;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.service.dict.CompanyService;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.Date;

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

        HttpListResult<Company> result = service.companyList(new CompanyQuery());

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
            Company proteiCompany = service.getCompanyById(1L).getData();

            Assert.assertNotNull(proteiCompany);

            CoreResponse<CompanyGroupItem> linkResult = service.addCompanyToGroup(testGroup.getId(), proteiCompany.getId());

            Assert.assertTrue(linkResult.isOk());
            Assert.assertNotNull(linkResult.getData());

            proteiCompany = service.getCompanyById(1L).getData();

            Assert.assertNotNull(proteiCompany.getGroups());
            Assert.assertTrue(proteiCompany.getGroups().size() > 0);

            System.out.println(proteiCompany.getGroups().get(0).getName());
        }
        finally {
            ctx.getBean(CompanyGroupItemDAO.class).removeByCondition("company_id=?", 1L);
        }
    }

    @Test
    public void testCompanies () {

        Long companyId = null;
        Long groupId = null;

        try {

            CompanyService service = ctx.getBean(CompanyService.class);

            Company company = new Company();
            company.setCreated(new Date());
            company.setCname("Тестовая компания");
            company.setAddressDejure("Тестовый адрес");
            company.setAddressFact("Тестовый адрес");

            Company dupCompany = ctx.getBean(CompanyDAO.class).getCompanyByName(company.getCname());
            Assert.assertNull(dupCompany);

            CompanyGroup group = ctx.getBean(CompanyGroupDAO.class).get(new Long(1));

            CoreResponse<Company> response = service.createCompany(company, group);
            Assert.assertTrue(response.isOk());
            Assert.assertNotNull(response.getData());

            System.out.println(company.getId());

            CompanyGroup newGroup = new CompanyGroup();
            newGroup.setCreated(new Date());
            newGroup.setName("Моя тестовая группа");

            dupCompany = ctx.getBean(CompanyDAO.class).getCompanyByName(company.getCname());
            if (company.getId() == null) {
                Assert.assertNull(dupCompany);
            } else {
                Assert.assertEquals(dupCompany.getId(), company.getId());
            }

            response = service.getCompanyById(company.getId());
            Assert.assertNotNull(response.getData());

            company.setCname("Моя тестовая компания");
            response =  service.updateCompany(company, newGroup);
            Assert.assertTrue(response.isOk());
            Assert.assertNotNull(response.getData());

            companyId = company.getId();
            groupId = newGroup.getId();

        } finally {
            if (companyId != null && groupId != null) {
                ctx.getBean(CompanyGroupItemDAO.class).getCompanyToGroupLinks(companyId, null).forEach(item -> {
                    ctx.getBean(CompanyGroupItemDAO.class).remove(item);
                });
            }
            if (companyId != null)
                ctx.getBean(CompanyDAO.class).removeByCondition("id=?", companyId);
            if (groupId != null)
                ctx.getBean(CompanyGroupDAO.class).removeByCondition("id=?", groupId);

        }
    }
}
