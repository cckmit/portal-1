package ru.protei.portal.test.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.config.DatabaseConfiguration;
import ru.protei.portal.config.MainTestsConfiguration;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.dao.CompanyGroupDAO;
import ru.protei.portal.core.model.dao.CompanyGroupItemDAO;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.service.CompanyService;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.Date;
import java.util.List;

/**
 * Created by michael on 11.10.16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class, JdbcConfigurationContext.class, DatabaseConfiguration.class, MainTestsConfiguration.class})
public class CompanyServiceTest extends BaseServiceTest {

    @Test
    public void testGetCompanyList () {

        CoreResponse<List<Company>> result = companyService.companyList(getAuthToken(), new CompanyQuery());

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getData());
    }

//    @Test
//    public void testCompanyGroups () {
//
//        try {
//            CompanyService service = ctx.getBean(CompanyService.class);
//
//            CoreResponse<CompanyGroup> resp = service.createGroup("Test-Group", "A group for test");
//
//            Assert.assertTrue(resp.isOk());
//            Assert.assertNotNull(resp.getData());
//
//            CompanyGroup testGroup = resp.getData();
//            Company proteiCompany = service.getCompany( 1L ).getData();
//
//            service.updateCompany(proteiCompany, testGroup);
//
//            Assert.assertNotNull(proteiCompany);
//
//            proteiCompany = service.getCompany( 1L ).getData();
//
//            Assert.assertNotNull(proteiCompany.getCompanyGroup());
////            Assert.assertTrue(proteiCompany.getGroups().size() > 0);
//
//            System.out.println(proteiCompany.getCompanyGroup().getName());
//        }
//        finally {
//            ctx.getBean(CompanyGroupItemDAO.class).removeByCondition("company_id=?", 1L);
//        }
//    }

    @Test
    public void testCompanies () {

        Long companyId = null;

        try {

            Company company = new Company();
            company.setCreated(new Date());
            company.setCname("Тестовая компания");

            PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(company.getContactInfo());
            infoFacade.setLegalAddress("Тестовый адрес");
            infoFacade.setFactAddress("Тестовый адрес");

            Company dupCompany = companyDAO.getCompanyByName(company.getCname());
            Assert.assertNull(dupCompany);

            CompanyGroup group = new CompanyGroup();
            group.setId(1L);
            group.setCreated(new Date());
            group.setName("test");
            group.setInfo("test");
            companyGroupDAO.persist(group);
            company.setGroupId(group.getId());

            CoreResponse<Company> response = companyService.createCompany(getAuthToken(), company);
            Assert.assertTrue(response.isOk());
            Assert.assertNotNull(response.getData());
            log.info("Company id = {}", company.getId());

            dupCompany = companyDAO.getCompanyByName(company.getCname());
            if (company.getId() == null) {
                Assert.assertNull(dupCompany);
            } else {
                Assert.assertEquals(dupCompany.getId(), company.getId());
            }

            response = companyService.getCompany(getAuthToken(), company.getId());
            Assert.assertNotNull(response.getData());

            company.setCname("Моя тестовая компания");
            response = companyService.updateCompany(getAuthToken(), company);
            Assert.assertTrue(response.isOk());
            Assert.assertNotNull(response.getData());

            companyId = company.getId();

        } finally {
            if (companyId != null) {
                companyGroupItemDAO.getCompanyToGroupLinks(companyId, null)
                        .forEach(item -> companyGroupItemDAO.remove(item));
            }
            if (companyId != null) {
                companyDAO.removeByCondition("id=?", companyId);
            }
        }
    }

    @Autowired
    CompanyService companyService;
    @Autowired
    CompanyDAO companyDAO;
    @Autowired
    CompanyGroupDAO companyGroupDAO;
    @Autowired
    CompanyGroupItemDAO companyGroupItemDAO;

    private static final Logger log = LoggerFactory.getLogger(CompanyServiceTest.class);
}
