package ru.protei.portal.test.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.config.MainTestsConfiguration;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.service.CaseService;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class, JdbcConfigurationContext.class, MainTestsConfiguration.class})
public class CaseServiceServiceTest {

    public static final AuthToken TEST_AUTH_TOKEN = new AuthToken("TEST_SID", "127.0.0.1");

    @Inject
    private CaseCommentDAO caseCommentDAO;
    @Inject
    CompanyDAO companyDAO;
    @Inject
    PersonDAO personDAO;
    @Inject
    private CaseService caseService;

    @Inject
    private JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Test
    public void getCaseObjectsTest() throws Exception {
        assertNotNull(caseService);
        List<CaseShortView> all = checkResultAndGetData(caseService.caseObjectList(TEST_AUTH_TOKEN, new CaseQuery()));
        assertNotNull(all);
    }


    public static CaseComment createNewComment(Person person, CaseObject caseObject, String text) {
        CaseComment comment = new CaseComment(text);
        comment.setCreated(new Date());
        comment.setCaseId(caseObject.getId());
        comment.setAuthorId(person.getId());
        comment.setText(text);
        comment.setCaseAttachments(Collections.emptyList());
        return comment;
    }

    public static CaseObject createNewCaseObject(Person person) {
        CaseObject caseObject = new CaseObject();
        caseObject.setCaseType(En_CaseType.TASK);
        caseObject.setName("Test_Case_Name");
        caseObject.setState(En_CaseState.CREATED);
        caseObject.setCaseType(En_CaseType.CRM_SUPPORT);
        return caseObject;
    }

    private CaseObject makeCaseObject(Person person) {
        return checkResultAndGetData(
                caseService.saveCaseObject(TEST_AUTH_TOKEN, createNewCaseObject(person), person)
        );
    }

    public static Person createNewPerson(Company company) {
        Person person = new Person();
        person.setCreated(new Date());
        person.setCreator("TEST");
        person.setCompanyId(company.getId());
        person.setDisplayName("Test_Person");
        person.setGender(En_Gender.MALE);
        return person;
    }

    private Person makePerson(Company company) {
        Person person = createNewPerson(company);
        person.setId(personDAO.persist(person));
        return person;
    }

    public static Company createNewCompany(CompanyCategory category) {
        Company company = new Company();
        company.setCname("Test_Company");
        company.setCategory(category);
        return company;
    }

    private Company makeCompany(CompanyCategory category) {
        Company company = createNewCompany(category);
        company.setId(companyDAO.persist(company));
        return company;
    }

    public static void checkResult(CoreResponse result) {
        assertNotNull("Expected result", result);
        assertTrue("Expected ok result", result.isOk());
    }

    public static <T> T checkResultAndGetData(CoreResponse<T> result) {
        checkResult(result);
        return result.getData();
    }

    long MINUTE = 1L;
}
