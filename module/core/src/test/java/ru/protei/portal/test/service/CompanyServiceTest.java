package ru.protei.portal.test.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.embeddeddb.DatabaseConfiguration;
import ru.protei.portal.config.IntegrationTestsConfiguration;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.dao.CompanyGroupDAO;
import ru.protei.portal.core.model.dao.CompanyGroupItemDAO;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.service.CompanyService;
import ru.protei.sn.remote_services.configuration.RemoteServiceFactory;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.http.HttpConfigurationContext;
import ru.protei.winter.http.client.factory.HttpClientFactory;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.Date;

/**
 * Created by michael on 11.10.16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        CoreConfigurationContext.class, JdbcConfigurationContext.class,
        DatabaseConfiguration.class, IntegrationTestsConfiguration.class, RemoteServiceFactory.class,
        HttpClientFactory.class, HttpConfigurationContext.class})
public class CompanyServiceTest extends BaseServiceTest {

    @Test
    public void testGetCompanyList () {

        Result<SearchResult<Company>> result = companyService.getCompanies(getAuthToken(), new CompanyQuery());

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getData());
        Assert.assertNotNull(result.getData().getResults());
    }

    @Test
    public void createdCompanyIsNotArchived () {
        Company company = createNewCustomerCompany();
        Assert.assertEquals("Expected company created by createNewCustomerCompany() is not archived", company.isArchived(), false);
    }

    @Test
    public void newCompanyIsNotArchived () {
        Assert.assertEquals("Expected new company is not archived", new Company().isArchived(), false);
    }


    @Test
    public void testCompanies () {

        Long companyId = null;
        Long companyGroupId = null;

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

            companyGroupId = companyGroupDAO.persist( group );
            company.setGroupId(group.getId());

            Result<Company> response = companyService.createCompany(getAuthToken(), company);
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
            if(companyGroupId!=null){
                companyGroupDAO.removeByKey( companyGroupId );
            }
        }
    }

    @Test
    public void testCompanyUpdateState() {
        Long companyId = null;

        try {
            Company company = new Company();
            company.setCreated(new Date());
            company.setCname("Тестовая компания " + new Date().getTime());

            Result<Company> response = companyService.createCompany(getAuthToken(), company);
            Company companyFromService = response.getData();
            companyId = companyFromService.getId();

            boolean startState = companyFromService.isArchived();

            companyService.updateState(getAuthToken(), companyFromService.getId(), !startState);

            boolean endState = companyService.getCompany(getAuthToken(), companyFromService.getId()).getData().isArchived();

            Assert.assertNotEquals(startState, endState);

            companyService.updateState(getAuthToken(), companyFromService.getId(), !endState);

            boolean changedEndState = companyService.getCompany(getAuthToken(), companyFromService.getId()).getData().isArchived();

            Assert.assertEquals(startState, changedEndState);
        } finally {
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
