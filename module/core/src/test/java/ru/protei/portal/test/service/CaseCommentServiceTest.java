package ru.protei.portal.test.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.portal.config.MainTestsConfiguration;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.dao.CaseStateDAO;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.CaseStateService;
import ru.protei.portal.core.service.PersonService;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class, JdbcConfigurationContext.class, MainTestsConfiguration.class})
public class CaseCommentServiceTest {

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
    public void getCaseCommentsTest() throws Exception {
        assertNotNull(caseService);
        List<CaseShortView> all = checkResultAndGetData(caseService.caseObjectList(TEST_AUTH_TOKEN, new CaseQuery()));
        assertNotNull(all);
    }

    @Test
    public void getCaseCommentsDaoTest() throws Exception {
        Company company = new Company();
        company.setCname("Test_Company");
        company.setCategory(new CompanyCategory(2L));
        company.setId(companyDAO.persist(company));

        Person person = new Person();
        person.setCreated(new Date());
        person.setCreator("TEST");
        person.setCompanyId(company.getId());
        person.setDisplayName("Test_Person");
        person.setGender(En_Gender.MALE);
        person.setId(personDAO.persist(person));

        CaseObject caseObject = new CaseObject();
        caseObject.setCaseType(En_CaseType.TASK);
        caseObject.setName("Test_Case_Name");
        caseObject.setState(En_CaseState.CREATED);
        caseObject = caseService.saveCaseObject(TEST_AUTH_TOKEN, caseObject, person ).getData();

        CaseComment comment = new CaseComment("test_comment");
        comment.setCreated(new Date());
        comment.setCaseId(caseObject.getId());
        comment.setAuthorId(person.getId());
        comment.setText("Test_Comment_Text");

        CaseTimeLog caseTimeLog = new CaseTimeLog();
        caseTimeLog.setWorkTime(2L);
        caseTimeLog.setCreated(new Date());
        caseTimeLog.setCaseId(caseObject.getId());
        caseTimeLog.setPersonId(person.getId());

        comment.setCaseTimeLog(caseTimeLog);

        assertNotNull(caseCommentDAO);
        Long commentId = caseCommentDAO.persist(comment);
        CaseComment caseComment = caseCommentDAO.get(commentId);
//        jdbcManyRelationsHelper.fill(caseComment, "caseTimeLog");
//        List<CaseComment> all = caseCommentDAO.getAll();
        int stop = 0;
    }


    public static void checkResult(CoreResponse result) {
        assertNotNull("Expected result", result);
        assertTrue("Expected ok result", result.isOk());
    }

    public static <T> T checkResultAndGetData(CoreResponse<T> result)  {
        checkResult(result);
        return result.getData();
    }
}
