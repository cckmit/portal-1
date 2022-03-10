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
import ru.protei.portal.config.IntegrationTestsConfiguration;
import ru.protei.portal.core.model.dict.En_CaseCommentPrivacyType;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.service.CaseCommentService;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.embeddeddb.DatabaseConfiguration;
import ru.protei.portal.mock.AuthServiceMock;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class, JdbcConfigurationContext.class, DatabaseConfiguration.class, IntegrationTestsConfiguration.class})
public class CaseCommentServiceTest extends BaseServiceTest {

    private AuthServiceMock authService;

    @Autowired
    private void authService( AuthService authService ) {
        this.authService = (AuthServiceMock) authService;
    }

    @Test
    public void getCaseObjectsTest() {
        assertNotNull(caseService);
        SearchResult<CaseShortView> all = checkResultAndGetData(caseService.getCaseObjects(getAuthToken(), new CaseQuery()));
        assertNotNull(all);
    }

    @Test
    public void getCaseCommentsDaoTest() {
        Company company = makeCustomerCompany();
        Person person = makePerson(company);
        authService.makeThreadAuthToken(makeUserLogin(person));
        CaseObject caseObject = makeCaseObject(person);

        CaseComment comment = createNewComment(person, caseObject.getId(), "Test_Comment");
        comment.setTimeElapsed(2 * MINUTE);

        Long commentId = caseCommentDAO.persist(comment);
        CaseComment caseComment = caseCommentDAO.get(commentId);
        assertNotNull(caseComment);
        assertNotNull(caseComment.getTimeElapsed());
    }

    @Test
    public void getCaseCommentListTest () {
        Company company = makeCustomerCompany();
        Person person = makePerson(company);
        authService.makeThreadAuthToken(makeUserLogin(person));
        CaseObject caseObject = makeCaseObject(caseType, person);

        makeCaseComment(person, caseObject.getId(), "Test message");

        List<CaseComment> comments = caseCommentDAO.getCaseComments(new CaseCommentQuery(caseObject.getId()));
        Assert.assertNotNull(comments);
        log.info("case " + caseObject.getId() + " comment list size = " + comments.size());

        Result<List<CaseComment>> result = caseCommentService.getCaseCommentList(getAuthToken(), caseType, caseObject.getId());

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isOk());
        Assert.assertNotNull(result.getData());
        log.info(" size = " + result.getData().size());
        Assert.assertTrue(result.getData().size() > 0);

        for (CaseComment comment : result.getData()) {
            log.info(comment.toString());
            log.info("----------------------");
        }

        Assert.assertTrue(removeHistoryCaseObject(caseObject.getId()));
        Assert.assertTrue(removeCaseObjectAndComments(caseObject));
        Assert.assertTrue(personDAO.remove(person));
        Assert.assertTrue(companyDAO.remove(company));
    }

    @Test
    public void CRUDCaseCommentTest () {

        Result<CaseComment> result;
        Result<List<CaseComment>> resultList;

        Company company = makeCustomerCompany();
        Person person = makePerson(company);
        authService.makeThreadAuthToken(makeUserLogin(person));
        CaseObject caseObject = makeCaseObject(caseType, person);

        // create
        CaseComment comment = new CaseComment();
        comment.setCaseId(caseObject.getId());
        comment.setAuthorId(person.getId());
        comment.setClientIp(getAuthToken().getIp());
        comment.setCreated(new Date());
        comment.setText("Unit-test - тестовый комментарий");
        comment.setCaseAttachments(Collections.emptyList());
        comment.setPrivacyType(En_CaseCommentPrivacyType.PUBLIC);

        result = caseCommentService.addCaseComment(getAuthToken(), caseType, comment);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isOk());
        Assert.assertNotNull(result.getData());
        comment = result.getData();
        log.info("{}", comment);

        resultList = caseCommentService.getCaseCommentList(getAuthToken(), caseType, caseObject.getId());
        log.info("Size after add = " + resultList.getData().size());

        // update
        comment.setText("Unit-test - тестовый комментарий (update)");

        result = caseCommentService.updateCaseComment(getAuthToken(), caseType, comment);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isOk());
        Assert.assertNotNull(result.getData());
        comment = result.getData();
        log.info("{}", comment);

        resultList = caseCommentService.getCaseCommentList(getAuthToken(), caseType, caseObject.getId());
        log.info("Size after update = " + resultList.getData().size());

        // delete
        Result<Long> result2 = caseCommentService.removeCaseComment(getAuthToken(), caseType, comment);
        Assert.assertNotNull(result2);
        Assert.assertTrue(result2.isOk());
        log.info("{}", result2.getData());

        resultList = caseCommentService.getCaseCommentList(getAuthToken(), caseType, caseObject.getId());
        log.info("Size after remove = " + resultList.getData().size());

        // cleanup
        Assert.assertTrue(removeHistoryCaseObject(caseObject.getId()));
        Assert.assertTrue(removeCaseObjectAndComments(caseObject));
        Assert.assertTrue(personDAO.remove(person));
        Assert.assertTrue(companyDAO.remove(company));
    }

    @Test
    public void changeTimeElapsedTest() {
        Company company = makeCustomerCompany();
        Person person = makePerson(company);
        authService.makeThreadAuthToken(makeUserLogin(person));
        CaseObject caseObject = makeCaseObject(person);

        CaseComment comment1 = createNewComment(person, caseObject.getId(), "Comment1");
        Long timeElapsed1 = 4 * MINUTE;
        comment1.setTimeElapsed(timeElapsed1);

        CaseComment saved = checkResultAndGetData(caseCommentService.addCaseComment(getAuthToken(), caseType, comment1));
        CaseComment fromDb = caseCommentDAO.get(saved.getId());

        assertEquals("Expected elapsed time for " + comment1.getText(), timeElapsed1, fromDb.getTimeElapsed());

        CaseObject caseObjectWithComment = checkResultAndGetData(caseService.getCaseObjectByNumber(getAuthToken(), caseObject.getCaseNumber()));
        assertEquals("Expected elapsed time of case object", timeElapsed1, caseObjectWithComment.getTimeElapsed());

        // Comment 2
        CaseComment comment2 = createNewComment(person, caseObject.getId(), "Comment2");
        Long timeElapsed2 = 5 * MINUTE;
        comment2.setTimeElapsed(timeElapsed2);

        saved = checkResultAndGetData(caseCommentService.addCaseComment(getAuthToken(), caseType, comment2));
        fromDb = caseCommentDAO.get(saved.getId());

        assertEquals("Expected elapsed time for " + comment2.getText(), timeElapsed2, fromDb.getTimeElapsed());

        caseObjectWithComment = checkResultAndGetData(caseService.getCaseObjectByNumber(getAuthToken(), caseObject.getCaseNumber()));
        assertEquals("Expected elapsed time of case object", new Long(timeElapsed1 + timeElapsed2), caseObjectWithComment.getTimeElapsed());

        //  Change comment 1
        Long timeElapsed1Changed = 18 * MINUTE;
        comment1.setTimeElapsed(timeElapsed1Changed);
        saved = checkResultAndGetData(caseCommentService.updateCaseComment(getAuthToken(), caseType, comment1));
        fromDb = caseCommentDAO.get(saved.getId());

        assertEquals("Expected elapsed time for " + comment1.getText(), timeElapsed1Changed, fromDb.getTimeElapsed());

        caseObjectWithComment = checkResultAndGetData(caseService.getCaseObjectByNumber(getAuthToken(), caseObject.getCaseNumber()));
        assertEquals("Expected elapsed time of case object", new Long(timeElapsed1Changed + timeElapsed2), caseObjectWithComment.getTimeElapsed());
    }

    @Inject
    CaseCommentService caseCommentService;

    private final static long MINUTE = 1L;
    private final static En_CaseType caseType = En_CaseType.CRM_SUPPORT;
    private static final Logger log = LoggerFactory.getLogger(CaseCommentServiceTest.class);
}
