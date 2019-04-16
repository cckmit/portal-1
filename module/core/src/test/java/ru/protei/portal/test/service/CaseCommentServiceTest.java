package ru.protei.portal.test.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.config.DatabaseConfiguration;
import ru.protei.portal.config.MainTestsConfiguration;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.service.CaseCommentService;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class, JdbcConfigurationContext.class, DatabaseConfiguration.class, MainTestsConfiguration.class})
public class CaseCommentServiceTest extends BaseServiceTest {


    private En_CaseType caseType = En_CaseType.CRM_SUPPORT;

    @Inject
    CaseCommentService caseCommentService;


    @Test
    public void getCaseObjectsTest() throws Exception {
        assertNotNull(caseService);
        List<CaseShortView> all = checkResultAndGetData(caseService.caseObjectList(TEST_AUTH_TOKEN, new CaseQuery()));
        assertNotNull(all);
    }

    @Test
    public void getCaseCommentsDaoTest() throws Exception {
        Company company = makeCompany(new CompanyCategory(2L));
        Person person = makePerson(company);
        CaseObject caseObject = makeCaseObject(person);

        CaseComment comment = createNewComment(person, caseObject, "Test_Comment");
        comment.setTimeElapsed(2 * MINUTE);

        Long commentId = caseCommentDAO.persist(comment);
        CaseComment caseComment = caseCommentDAO.get(commentId);
        assertNotNull(caseComment);
        assertNotNull(caseComment.getTimeElapsed());
    }

    @Test
    public void changeTimeElapsedTest() throws Exception {
        Company company = makeCompany(new CompanyCategory(2L));
        Person person = makePerson(company);
        CaseObject caseObject = makeCaseObject(person);

        CaseComment comment1 = createNewComment(person, caseObject, "Comment1");
        Long timeElapsed1 = 4 * MINUTE;
        comment1.setTimeElapsed(timeElapsed1);

        CaseComment saved = checkResultAndGetData(caseCommentService.addCaseComment(TEST_AUTH_TOKEN, caseType, comment1, person));
        CaseComment fromDb = caseCommentDAO.get(saved.getId());

        assertEquals("Expected elapsed time for " + comment1.getText(), timeElapsed1, fromDb.getTimeElapsed());

        CaseObject caseObjectWithComment = checkResultAndGetData(caseService.getCaseObject(TEST_AUTH_TOKEN, caseObject.getCaseNumber()));
        assertEquals("Expected elapsed time of case object", timeElapsed1, caseObjectWithComment.getTimeElapsed());

        // Comment 2
        CaseComment comment2 = createNewComment(person, caseObject, "Comment2");
        Long timeElapsed2 = 5 * MINUTE;
        comment2.setTimeElapsed(timeElapsed2);

        saved = checkResultAndGetData(caseCommentService.addCaseComment(TEST_AUTH_TOKEN, caseType, comment2, person));
        fromDb = caseCommentDAO.get(saved.getId());

        assertEquals("Expected elapsed time for " + comment2.getText(), timeElapsed2, fromDb.getTimeElapsed());

        caseObjectWithComment = checkResultAndGetData(caseService.getCaseObject(TEST_AUTH_TOKEN, caseObject.getCaseNumber()));
        assertEquals("Expected elapsed time of case object", new Long(timeElapsed1 + timeElapsed2), caseObjectWithComment.getTimeElapsed());

        //  Change comment 1
        Long timeElapsed1Changed = 18 * MINUTE;
        comment1.setTimeElapsed(timeElapsed1Changed);
        saved = checkResultAndGetData(caseCommentService.updateCaseComment(TEST_AUTH_TOKEN, caseType, comment1, person));
        fromDb = caseCommentDAO.get(saved.getId());

        assertEquals("Expected elapsed time for " + comment1.getText(), timeElapsed1Changed, fromDb.getTimeElapsed());

        caseObjectWithComment = checkResultAndGetData(caseService.getCaseObject(TEST_AUTH_TOKEN, caseObject.getCaseNumber()));
        assertEquals("Expected elapsed time of case object", new Long(timeElapsed1Changed + timeElapsed2), caseObjectWithComment.getTimeElapsed());
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

    long MINUTE = 1L;
}
