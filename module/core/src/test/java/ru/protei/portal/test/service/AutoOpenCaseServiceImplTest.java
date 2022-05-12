package ru.protei.portal.test.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.config.IntegrationTestsConfiguration;
import ru.protei.portal.core.model.dao.CommonManagerDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.service.autoopencase.AutoOpenCaseService;
import ru.protei.portal.embeddeddb.DatabaseConfiguration;
import ru.protei.sn.remote_services.configuration.RemoteServiceFactory;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.http.HttpConfigurationContext;
import ru.protei.winter.http.client.factory.HttpClientFactory;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static ru.protei.portal.core.model.helper.CollectionUtils.toList;
import static ru.protei.portal.core.model.util.CrmConstants.AutoOpen.NO_DELAY;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableScheduling
@EnableTransactionManagement
@ContextConfiguration(classes = {CoreConfigurationContext.class,
        JdbcConfigurationContext.class, DatabaseConfiguration.class,
        IntegrationTestsConfiguration.class, RemoteServiceFactory.class,
        HttpClientFactory.class, HttpConfigurationContext.class
})
public class AutoOpenCaseServiceImplTest extends BaseServiceTest {

    @Test
    @Transactional
    public void createTaskNoDelayTest() {
        createTask(NO_DELAY);
    }

    @Test
    public void createTaskWithDelayTest() {
        createTask(TimeUnit.SECONDS.toMillis(3));
    }

    public void createTask(long delay) {
        Company customerCompany = createNewCustomerCompany();
        customerCompany.setAutoOpenIssue(true);
        companyDAO.persist(customerCompany);

        companyImportanceItemDAO.persistBatch(
                toList(CrmConstants.ImportanceLevel.commonImportanceLevelIds, importanceLevelId ->
                        new CompanyImportanceItem(customerCompany.getId(), importanceLevelId, 0))
        );

        Person customerPerson = createNewPerson(customerCompany);
        personDAO.persist(customerPerson);

        Company homeCompany = companyDAO.get(1L);
        Person manager = createNewPerson(homeCompany);
        manager.setGender(En_Gender.UNDEFINED);

        personDAO.persist(manager);

        DevUnit product = createProduct("AutoOpenCaseServiceProduct");
        product.setType(En_DevUnitType.PRODUCT);
        devUnitDAO.persist(product);

        // CommonManager
        CommonManager commonManager = new CommonManager();
        commonManager.setProductId(product.getId());
        commonManager.setManagerId(manager.getId());
        commonManager.setCompanyId(customerCompany.getId());
        commonManagerDAO.persist(commonManager);

        CaseObject caseObject = createNewCaseObject(customerPerson);
        caseObject.setName("AutoOpenCaseServiceProject");
        caseObject.setStateId(CrmConstants.State.OPENED);
        caseObject.setInitiatorCompanyId(customerCompany.getId());
        caseObject.setType(En_CaseType.PROJECT);
        caseObject.setProductId(product.getId());

        Long projectId = caseObjectDAO.persist(caseObject);

        Project project = new Project();
        project.setId(projectId);

        projectDAO.persist(project);

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
        newCaseObject.setPlatformId(platform.getId());
        newCaseObject.setType(En_CaseType.CRM_SUPPORT);
        newCaseObject.setStateId(CrmConstants.State.CREATED);

        CaseObjectCreateRequest request = new CaseObjectCreateRequest();
        request.setCaseObject(newCaseObject);

        caseObjectDAO.persist(newCaseObject);

        if (delay == NO_DELAY) {
            service.performCaseOpen(newCaseObject.getId());
        } else {
            try {
                service.createTask(newCaseObject.getId(), delay)
                        .get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        CaseObject caseObjectFromDb = caseObjectDAO.get(newCaseObject.getId());
        Assert.assertEquals(caseObjectFromDb.getStateId(), CrmConstants.State.OPENED);
        Assert.assertEquals(caseObjectFromDb.getManagerId(), manager.getId());

        // non transaction, remove manually
        removeHistoryCaseObject(caseObject.getId());
        removeHistoryCaseObject(newCaseObject.getId());
        caseCommentDAO.removeByCondition("case_id = ? or case_id = ?", newCaseObject.getId(), caseObject.getId());
        platformDAO.remove(platform);
        projectToProductDAO.removeByCondition("project_id = ? and product_id = ?", caseObject.getId(), product.getId());
        caseObjectDAO.remove(newCaseObject);
        caseObjectDAO.remove(caseObject);
        personDAO.remove(customerPerson);
        commonManagerDAO.remove(commonManager);
        personDAO.remove(manager);
        companyDAO.remove(customerCompany);
    }

    @Test
    @Transactional
    public void productAndCompanyTest() {
        Company customerCompany = createNewCustomerCompany();
        customerCompany.setAutoOpenIssue(true);
        companyDAO.persist(customerCompany);

        companyImportanceItemDAO.persistBatch(
                toList(CrmConstants.ImportanceLevel.commonImportanceLevelIds, importanceLevelId ->
                        new CompanyImportanceItem(customerCompany.getId(), importanceLevelId, 0))
        );

        Person customerPerson = createNewPerson(customerCompany);
        personDAO.persist(customerPerson);

        Company homeCompany = companyDAO.get(1L);
        Person manager = createNewPerson(homeCompany);
        manager.setGender(En_Gender.UNDEFINED);

        personDAO.persist(manager);

        DevUnit product = createProduct("AutoOpenCaseServiceProduct");
        product.setType(En_DevUnitType.PRODUCT);
        devUnitDAO.persist(product);

        CommonManager commonManager = new CommonManager();
        commonManager.setProductId(null);
        commonManager.setManagerId(manager.getId());
        commonManager.setCompanyId(customerCompany.getId());
        commonManagerDAO.persist(commonManager);

        CaseObject caseObject = createNewCaseObject(customerPerson);
        caseObject.setName("AutoOpenCaseServiceProject");
        caseObject.setStateId(CrmConstants.State.OPENED);
        caseObject.setInitiatorCompanyId(customerCompany.getId());
        caseObject.setType(En_CaseType.PROJECT);
        caseObject.setProductId(product.getId());

        Long projectId = caseObjectDAO.persist(caseObject);

        Project project = new Project();
        project.setId(projectId);

        projectDAO.persist(project);

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
        newCaseObject.setPlatformId(platform.getId());
        newCaseObject.setType(En_CaseType.CRM_SUPPORT);
        newCaseObject.setStateId(CrmConstants.State.CREATED);

        CaseObjectCreateRequest request = new CaseObjectCreateRequest();
        request.setCaseObject(newCaseObject);

        caseObjectDAO.persist(newCaseObject);

        service.performCaseOpen(newCaseObject.getId());

        CaseObject caseObjectFromDb = caseObjectDAO.get(newCaseObject.getId());
        Assert.assertEquals(caseObjectFromDb.getStateId(), CrmConstants.State.OPENED);
        Assert.assertEquals(caseObjectFromDb.getManagerId(), manager.getId());
    }

    @Test
    @Transactional
    public void productAndCompanyIsNullTest() {
        Company customerCompany = createNewCustomerCompany();
        customerCompany.setAutoOpenIssue(true);
        companyDAO.persist(customerCompany);

        companyImportanceItemDAO.persistBatch(
                toList(CrmConstants.ImportanceLevel.commonImportanceLevelIds, importanceLevelId ->
                        new CompanyImportanceItem(customerCompany.getId(), importanceLevelId, 0))
        );

        Person customerPerson = createNewPerson(customerCompany);
        personDAO.persist(customerPerson);

        Company homeCompany = companyDAO.get(1L);
        Person manager = createNewPerson(homeCompany);
        manager.setGender(En_Gender.UNDEFINED);

        personDAO.persist(manager);

        DevUnit product = createProduct("AutoOpenCaseServiceProduct");
        product.setType(En_DevUnitType.PRODUCT);
        devUnitDAO.persist(product);

        // CommonManager
        CommonManager commonManager = new CommonManager();
        commonManager.setProductId(product.getId());
        commonManager.setManagerId(manager.getId());
        commonManager.setCompanyId(null);
        commonManagerDAO.persist(commonManager);

        CaseObject caseObject = createNewCaseObject(customerPerson);
        caseObject.setName("AutoOpenCaseServiceProject");
        caseObject.setStateId(CrmConstants.State.OPENED);
        caseObject.setInitiatorCompanyId(customerCompany.getId());
        caseObject.setType(En_CaseType.PROJECT);
        caseObject.setProductId(product.getId());

        Long projectId = caseObjectDAO.persist(caseObject);

        Project project = new Project();
        project.setId(projectId);

        projectDAO.persist(project);

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
        newCaseObject.setPlatformId(platform.getId());
        newCaseObject.setType(En_CaseType.CRM_SUPPORT);
        newCaseObject.setStateId(CrmConstants.State.CREATED);

        CaseObjectCreateRequest request = new CaseObjectCreateRequest();
        request.setCaseObject(newCaseObject);

        caseObjectDAO.persist(newCaseObject);

        service.performCaseOpen(newCaseObject.getId());

        CaseObject caseObjectFromDb = caseObjectDAO.get(newCaseObject.getId());
        Assert.assertEquals(caseObjectFromDb.getStateId(), CrmConstants.State.OPENED);
        Assert.assertEquals(caseObjectFromDb.getManagerId(), manager.getId());
    }

    @Test
    @Transactional
    public void productIsNullAndCompanyTest() {
        Company customerCompany = createNewCustomerCompany();
        customerCompany.setAutoOpenIssue(true);
        companyDAO.persist(customerCompany);

        companyImportanceItemDAO.persistBatch(
                toList(CrmConstants.ImportanceLevel.commonImportanceLevelIds, importanceLevelId ->
                        new CompanyImportanceItem(customerCompany.getId(), importanceLevelId, 0))
        );

        Person customerPerson = createNewPerson(customerCompany);
        personDAO.persist(customerPerson);

        Company homeCompany = companyDAO.get(1L);
        Person manager = createNewPerson(homeCompany);
        manager.setGender(En_Gender.UNDEFINED);

        personDAO.persist(manager);

        DevUnit product = createProduct("AutoOpenCaseServiceProduct");
        product.setType(En_DevUnitType.PRODUCT);
        devUnitDAO.persist(product);


        // CommonManager
        CommonManager commonManager = new CommonManager();
        commonManager.setProductId(null);
        commonManager.setCompanyId(customerCompany.getId());
        commonManager.setManagerId(manager.getId());
        commonManagerDAO.persist(commonManager);

        CaseObject caseObject = createNewCaseObject(customerPerson);
        caseObject.setName("AutoOpenCaseServiceProject");
        caseObject.setStateId(CrmConstants.State.OPENED);
        caseObject.setInitiatorCompanyId(customerCompany.getId());
        caseObject.setType(En_CaseType.PROJECT);
        caseObject.setProductId(product.getId());

        Long projectId = caseObjectDAO.persist(caseObject);

        Project project = new Project();
        project.setId(projectId);

        projectDAO.persist(project);

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
        newCaseObject.setPlatformId(platform.getId());
        newCaseObject.setType(En_CaseType.CRM_SUPPORT);
        newCaseObject.setStateId(CrmConstants.State.CREATED);

        CaseObjectCreateRequest request = new CaseObjectCreateRequest();
        request.setCaseObject(newCaseObject);

        caseObjectDAO.persist(newCaseObject);

        service.performCaseOpen(newCaseObject.getId());

        CaseObject caseObjectFromDb = caseObjectDAO.get(newCaseObject.getId());
        Assert.assertEquals(caseObjectFromDb.getStateId(), CrmConstants.State.OPENED);
        Assert.assertEquals(caseObjectFromDb.getManagerId(), manager.getId());
    }

    @Test
    @Transactional
    public void NoCommonManagerTest() {
        Company customerCompany = createNewCustomerCompany();
        customerCompany.setAutoOpenIssue(true);
        companyDAO.persist(customerCompany);

        companyImportanceItemDAO.persistBatch(
                toList(CrmConstants.ImportanceLevel.commonImportanceLevelIds, importanceLevelId ->
                        new CompanyImportanceItem(customerCompany.getId(), importanceLevelId, 0))
        );

        Person customerPerson = createNewPerson(customerCompany);
        personDAO.persist(customerPerson);

        Company homeCompany = companyDAO.get(1L);
        Person manager = createNewPerson(homeCompany);
        manager.setGender(En_Gender.UNDEFINED);

        personDAO.persist(manager);

        DevUnit product = createProduct("AutoOpenCaseServiceProduct");
        product.setType(En_DevUnitType.PRODUCT);
        devUnitDAO.persist(product);

        // CommonManager

        CaseObject caseObject = createNewCaseObject(customerPerson);
        caseObject.setName("AutoOpenCaseServiceProject");
        caseObject.setStateId(CrmConstants.State.OPENED);
        caseObject.setInitiatorCompanyId(customerCompany.getId());
        caseObject.setType(En_CaseType.PROJECT);
        caseObject.setProductId(product.getId());

        Long projectId = caseObjectDAO.persist(caseObject);

        Project project = new Project();
        project.setId(projectId);

        projectDAO.persist(project);

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
        newCaseObject.setPlatformId(platform.getId());
        newCaseObject.setType(En_CaseType.CRM_SUPPORT);
        newCaseObject.setStateId(CrmConstants.State.CREATED);

        CaseObjectCreateRequest request = new CaseObjectCreateRequest();
        request.setCaseObject(newCaseObject);

        caseObjectDAO.persist(newCaseObject);

        service.performCaseOpen(newCaseObject.getId());

        CaseObject caseObjectFromDb = caseObjectDAO.get(newCaseObject.getId());
        Assert.assertEquals(caseObjectFromDb.getStateId(), CrmConstants.State.CREATED);
    }

    @Test
    @Transactional
    public void getCaseIdToAutoOpenState() {

        caseObjectDAO.getCaseIdToAutoOpen();

        Company customerCompanyAutoOpen = createNewCustomerCompany();
        customerCompanyAutoOpen.setAutoOpenIssue(true);
        companyDAO.persist(customerCompanyAutoOpen);

        companyImportanceItemDAO.persistBatch(
                toList(CrmConstants.ImportanceLevel.commonImportanceLevelIds, importanceLevelId ->
                        new CompanyImportanceItem(customerCompanyAutoOpen.getId(), importanceLevelId, 0))
        );

        Person customerPerson = createNewPerson(customerCompanyAutoOpen);
        personDAO.persist(customerPerson);

        Company homeCompany = companyDAO.get(1L);
        Person manager = createNewPerson(homeCompany);
        manager.setGender(En_Gender.UNDEFINED);

        personDAO.persist(manager);

        DevUnit product = createProduct("AutoOpenCaseServiceProduct");

        devUnitDAO.persist(product);

        CaseObject openNoCommonManagerCase = createNewCaseObject(customerPerson);
        openNoCommonManagerCase.setInitiatorCompanyId(customerCompanyAutoOpen.getId());
        openNoCommonManagerCase.setInitiatorId(customerPerson.getId());
        openNoCommonManagerCase.setManagerCompanyId(homeCompany.getId());
        openNoCommonManagerCase.setManagerId(manager.getId());
        openNoCommonManagerCase.setProductId(product.getId());
        openNoCommonManagerCase.setStateId(CrmConstants.State.OPENED);
        openNoCommonManagerCase.setType(En_CaseType.CRM_SUPPORT);

        caseObjectDAO.persist(openNoCommonManagerCase);


        CaseObject createNoCommonManagerCase = createNewCaseObject(customerPerson);
        createNoCommonManagerCase.setInitiatorCompanyId(customerCompanyAutoOpen.getId());
        createNoCommonManagerCase.setInitiatorId(customerPerson.getId());
        createNoCommonManagerCase.setManagerCompanyId(homeCompany.getId());
        createNoCommonManagerCase.setProductId(product.getId());
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

        companyImportanceItemDAO.persistBatch(
                toList(CrmConstants.ImportanceLevel.commonImportanceLevelIds, importanceLevelId ->
                        new CompanyImportanceItem(customerCompanyAutoOpen.getId(), importanceLevelId, 0))
        );

        Person customerPersonAutoOpen = createNewPerson(customerCompanyAutoOpen);
        personDAO.persist(customerPersonAutoOpen);

        Company customerCompany = createNewCustomerCompany();
        customerCompany.setAutoOpenIssue(false);
        companyDAO.persist(customerCompany);
        Person customerPerson = createNewPerson(customerCompany);
        personDAO.persist(customerPerson);

        Company homeCompany = companyDAO.get(1L);
        Person manager = createNewPerson(homeCompany);
        manager.setGender(En_Gender.UNDEFINED);
        personDAO.persist(manager);

        DevUnit product = createProduct("AutoOpenCaseServiceProduct");
        devUnitDAO.persist(product);

        CaseObject noAutoOpenCase = createNewCaseObject(customerPerson);
        noAutoOpenCase.setInitiatorCompanyId(customerCompany.getId());
        noAutoOpenCase.setInitiatorId(customerPerson.getId());
        noAutoOpenCase.setManagerCompanyId(homeCompany.getId());
        noAutoOpenCase.setProductId(product.getId());
        noAutoOpenCase.setType(En_CaseType.CRM_SUPPORT);

        caseObjectDAO.persist(noAutoOpenCase);

        CaseObject autoOpenCase = createNewCaseObject(customerPerson);
        autoOpenCase.setInitiatorCompanyId(customerCompanyAutoOpen.getId());
        autoOpenCase.setInitiatorId(customerPersonAutoOpen.getId());
        autoOpenCase.setManagerCompanyId(homeCompany.getId());
        autoOpenCase.setProductId(product.getId());
        autoOpenCase.setType(En_CaseType.CRM_SUPPORT);

        caseObjectDAO.persist(autoOpenCase);

        List<Long> caseIdToAutoOpen = caseObjectDAO.getCaseIdToAutoOpen();

        Assert.assertEquals(1, caseIdToAutoOpen.size());
        Assert.assertEquals(autoOpenCase.getId(), caseIdToAutoOpen.get(0));
    }

    @Autowired
    AutoOpenCaseService service;
    @Autowired
    CommonManagerDAO commonManagerDAO;
}
