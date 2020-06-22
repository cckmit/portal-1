package ru.protei.portal.test.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.config.IntegrationTestsConfiguration;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.service.autoopencase.AutoOpenCaseService;
import ru.protei.portal.core.service.autoopencase.AutoOpenCaseServiceImpl;
import ru.protei.portal.embeddeddb.DatabaseConfiguration;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.Collections;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableScheduling
@EnableTransactionManagement
@ContextConfiguration(classes = {CoreConfigurationContext.class,
        JdbcConfigurationContext.class, DatabaseConfiguration.class,
        IntegrationTestsConfiguration.class,
        AutoOpenCaseServiceImplTest.AutoOpenConfiguration.class})
public class AutoOpenCaseServiceImplTest extends BaseServiceTest {

    @Configuration
    static class AutoOpenConfiguration {
        @Bean
        @Qualifier("TEST")
        public AutoOpenCaseService getAutoOpenCaseService() {
            return new AutoOpenCaseServiceImpl();
        }
    }

    @Test
    public void createTask() {
        Company customerCompany = createNewCustomerCompany();
        customerCompany.setAutoOpenIssue(true);
        companyDAO.persist(customerCompany);

        Person customerPerson = createNewPerson(customerCompany);
        personDAO.persist(customerPerson);

        Company homeCompany = companyDAO.get(1L);
        Person commonManager = createNewPerson(homeCompany);
        commonManager.setGender(En_Gender.UNDEFINED);

        personDAO.persist(commonManager);

        DevUnit product = createProduct("AutoOpenCaseServiceProduct");
        product.setCommonManagerId(commonManager.getId());
        product.setType(En_DevUnitType.PRODUCT);
        devUnitDAO.persist(product);

        CaseObject project = createNewCaseObject(customerPerson);
        project.setName("AutoOpenCaseServiceProject");
        project.setStateId(CrmConstants.State.OPENED);
        project.setInitiatorCompanyId(customerCompany.getId());
        project.setType(En_CaseType.PROJECT);
        project.setProductId(product.getId());

        Long projectId = caseObjectDAO.persist(project);

        Platform platform = new Platform();
        platform.setName("AutoOpenCaseServicePlatform");
        platform.setCompanyId(customerCompany.getId());
        platform.setProjectId(projectId);

        ProjectToProduct projectToProduct = new ProjectToProduct(projectId, product.getId());
        projectToProductDAO.persist(projectToProduct);

        platformDAO.persist(platform);

        CaseObject newCaseObject = createNewCaseObject(customerPerson);
        newCaseObject.setInitiatorCompanyId(customerCompany.getId());
        newCaseObject.setInitiatorId(customerPerson.getId());
        newCaseObject.setManagerCompanyId(homeCompany.getId());
        newCaseObject.setProductId(product.getId());
        newCaseObject.setProducts(Collections.singleton(product));
        newCaseObject.setPlatformId(platform.getId());
        newCaseObject.setType(En_CaseType.CRM_SUPPORT);
        newCaseObject.setStateId(CrmConstants.State.CREATED);

        CaseObjectCreateRequest request = new CaseObjectCreateRequest();
        request.setCaseObject(newCaseObject);

        caseObjectDAO.persist(newCaseObject);

        try {
            service.createTask(newCaseObject.getId(), 0)
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        CaseObject caseObject = caseObjectDAO.get(newCaseObject.getId());
        Assert.assertEquals(caseObject.getStateId(), CrmConstants.State.OPENED);
        Assert.assertEquals(caseObject.getManagerId(), commonManager.getId());

        // non transaction, remove manually
        caseCommentDAO.removeByCondition("case_id = ? or case_id = ?", newCaseObject.getId(), project.getId());
        platformDAO.remove(platform);
        projectToProductDAO.removeByCondition("project_id = ? and product_id = ?", project.getId(), product.getId());
        caseObjectDAO.remove(newCaseObject);
        caseObjectDAO.remove(project);
        personDAO.remove(customerPerson);
        personDAO.remove(commonManager);
        companyDAO.remove(customerCompany);
    }

    @Test
    @Transactional
    public void getCaseIdToAutoOpenState() {

        caseObjectDAO.getCaseIdToAutoOpen();

        Company customerCompanyAutoOpen = createNewCustomerCompany();
        customerCompanyAutoOpen.setAutoOpenIssue(true);
        companyDAO.persist(customerCompanyAutoOpen);

        Person customerPerson = createNewPerson(customerCompanyAutoOpen);
        personDAO.persist(customerPerson);

        Company homeCompany = companyDAO.get(1L);
        Person commonManager = createNewPerson(homeCompany);
        commonManager.setGender(En_Gender.UNDEFINED);

        personDAO.persist(commonManager);

        DevUnit product = createProduct("AutoOpenCaseServiceProduct");
        product.setCommonManagerId(commonManager.getId());

        devUnitDAO.persist(product);

        CaseObject openNoCommonManagerCase = createNewCaseObject(customerPerson);
        openNoCommonManagerCase.setInitiatorCompanyId(customerCompanyAutoOpen.getId());
        openNoCommonManagerCase.setInitiatorId(customerPerson.getId());
        openNoCommonManagerCase.setManagerCompanyId(homeCompany.getId());
        openNoCommonManagerCase.setManagerId(commonManager.getId());
        openNoCommonManagerCase.setProductId(product.getId());
        openNoCommonManagerCase.setProducts(Collections.singleton(product));
        openNoCommonManagerCase.setStateId(CrmConstants.State.OPENED);
        openNoCommonManagerCase.setType(En_CaseType.CRM_SUPPORT);

        caseObjectDAO.persist(openNoCommonManagerCase);


        CaseObject createNoCommonManagerCase = createNewCaseObject(customerPerson);
        createNoCommonManagerCase.setInitiatorCompanyId(customerCompanyAutoOpen.getId());
        createNoCommonManagerCase.setInitiatorId(customerPerson.getId());
        createNoCommonManagerCase.setManagerCompanyId(homeCompany.getId());
        createNoCommonManagerCase.setProductId(product.getId());
        createNoCommonManagerCase.setProducts(Collections.singleton(product));
        openNoCommonManagerCase.setStateId(CrmConstants.State.CREATED);
        openNoCommonManagerCase.setType(En_CaseType.CRM_SUPPORT);

        caseObjectDAO.persist(createNoCommonManagerCase);


        List<Long> caseIdToAutoOpen = caseObjectDAO.getCaseIdToAutoOpen();

        Assert.assertEquals(1, caseIdToAutoOpen.size());
        Assert.assertEquals(createNoCommonManagerCase.getId(), caseIdToAutoOpen.get(0));
    }

    @Test
    @Transactional
    public void getCaseIdToAutoOpenFlag() {

        Company customerCompanyAutoOpen = createNewCustomerCompany();
        customerCompanyAutoOpen.setAutoOpenIssue(true);
        companyDAO.persist(customerCompanyAutoOpen);
        Person customerPersonAutoOpen = createNewPerson(customerCompanyAutoOpen);
        personDAO.persist(customerPersonAutoOpen);

        Company customerCompany = createNewCustomerCompany();
        customerCompany.setAutoOpenIssue(false);
        companyDAO.persist(customerCompany);
        Person customerPerson = createNewPerson(customerCompany);
        personDAO.persist(customerPerson);

        Company homeCompany = companyDAO.get(1L);
        Person commonManager = createNewPerson(homeCompany);
        commonManager.setGender(En_Gender.UNDEFINED);
        personDAO.persist(commonManager);

        DevUnit product = createProduct("AutoOpenCaseServiceProduct");
        product.setCommonManagerId(commonManager.getId());
        devUnitDAO.persist(product);

        CaseObject noAutoOpenCase = createNewCaseObject(customerPerson);
        noAutoOpenCase.setInitiatorCompanyId(customerCompany.getId());
        noAutoOpenCase.setInitiatorId(customerPerson.getId());
        noAutoOpenCase.setManagerCompanyId(homeCompany.getId());
        noAutoOpenCase.setProductId(product.getId());
        noAutoOpenCase.setProducts(Collections.singleton(product));
        noAutoOpenCase.setType(En_CaseType.CRM_SUPPORT);

        caseObjectDAO.persist(noAutoOpenCase);

        CaseObject autoOpenCase = createNewCaseObject(customerPerson);
        autoOpenCase.setInitiatorCompanyId(customerCompanyAutoOpen.getId());
        autoOpenCase.setInitiatorId(customerPersonAutoOpen.getId());
        autoOpenCase.setManagerCompanyId(homeCompany.getId());
        autoOpenCase.setProductId(product.getId());
        autoOpenCase.setProducts(Collections.singleton(product));
        autoOpenCase.setType(En_CaseType.CRM_SUPPORT);

        caseObjectDAO.persist(autoOpenCase);

        List<Long> caseIdToAutoOpen = caseObjectDAO.getCaseIdToAutoOpen();

        Assert.assertEquals(1, caseIdToAutoOpen.size());
        Assert.assertEquals(autoOpenCase.getId(), caseIdToAutoOpen.get(0));
    }

    @Autowired
    @Qualifier("TEST")
    AutoOpenCaseService service;
}
