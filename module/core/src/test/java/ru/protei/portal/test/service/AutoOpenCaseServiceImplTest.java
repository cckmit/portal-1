package ru.protei.portal.test.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.config.IntegrationTestsConfiguration;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.service.autoopencase.AutoOpenCaseService;
import ru.protei.portal.embeddeddb.DatabaseConfiguration;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class,
        JdbcConfigurationContext.class, DatabaseConfiguration.class,
        IntegrationTestsConfiguration.class})
public class AutoOpenCaseServiceImplTest extends BaseServiceTest {

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

        devUnitDAO.persist(product);

        CaseObject newCaseObject = createNewCaseObject(customerPerson);
        newCaseObject.setInitiatorCompanyId(customerCompany.getId());
        newCaseObject.setInitiatorId(customerPerson.getId());
        newCaseObject.setManagerCompanyId(homeCompany.getId());
        newCaseObject.setProductId(product.getId());
        newCaseObject.setProducts(Collections.singleton(product));

        CaseObjectCreateRequest request = new CaseObjectCreateRequest();
        request.setCaseObject(newCaseObject);

        caseObjectDAO.persist(newCaseObject);

        try {
            service.createTask(newCaseObject.getId(), new Random(0), 0)
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        CaseObject caseObject = caseObjectDAO.get(newCaseObject.getId());
        Assert.assertEquals(caseObject.getStateId(), CrmConstants.State.OPENED);
        Assert.assertEquals(caseObject.getManagerId(), commonManager.getId());
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
        openNoCommonManagerCase.setStateId(2L);

        caseObjectDAO.persist(openNoCommonManagerCase);


        CaseObject createNoCommonManagerCase = createNewCaseObject(customerPerson);
        createNoCommonManagerCase.setInitiatorCompanyId(customerCompanyAutoOpen.getId());
        createNoCommonManagerCase.setInitiatorId(customerPerson.getId());
        createNoCommonManagerCase.setManagerCompanyId(homeCompany.getId());
        createNoCommonManagerCase.setProductId(product.getId());
        createNoCommonManagerCase.setProducts(Collections.singleton(product));

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

        caseObjectDAO.persist(noAutoOpenCase);

        CaseObject autoOpenCase = createNewCaseObject(customerPerson);
        autoOpenCase.setInitiatorCompanyId(customerCompanyAutoOpen.getId());
        autoOpenCase.setInitiatorId(customerPersonAutoOpen.getId());
        autoOpenCase.setManagerCompanyId(homeCompany.getId());
        autoOpenCase.setProductId(product.getId());
        autoOpenCase.setProducts(Collections.singleton(product));

        caseObjectDAO.persist(autoOpenCase);

        List<Long> caseIdToAutoOpen = caseObjectDAO.getCaseIdToAutoOpen();

        Assert.assertEquals(1, caseIdToAutoOpen.size());
        Assert.assertEquals(autoOpenCase.getId(), caseIdToAutoOpen.get(0));
    }

    @Autowired
    AutoOpenCaseService service;
}
