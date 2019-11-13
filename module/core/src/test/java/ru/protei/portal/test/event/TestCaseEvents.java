package ru.protei.portal.test.event;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.DatabaseConfiguration;
import ru.protei.portal.config.IntegrationTestsConfiguration;
import ru.protei.portal.config.TestEventConfiguration;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.service.CaseCommentService;
import ru.protei.portal.core.service.CaseControlService;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.test.service.BaseServiceTest;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.Collections;
import java.util.Date;

/**
 * Created by michael on 04.05.17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        CoreConfigurationContext.class, JdbcConfigurationContext.class, DatabaseConfiguration.class,
        IntegrationTestsConfiguration.class, TestEventConfiguration.class
})
public class TestCaseEvents extends BaseServiceTest {

    private static final String JUNIT_EVENT_PUB_01 = "junit-event-pub-01";
    private static final En_CaseType caseType = En_CaseType.CRM_SUPPORT;

    @Test
//    @Ignore
    public void test001 () throws Exception {

        Company company = makeCustomerCompany();
        Person person = makePerson(company);

        CaseObject object = new CaseObject();
        object.setPrivateCase(false);
        object.setCaseNumber(1L);
        object.setCaseType(caseType);
        object.setInitiatorCompany(company);
        object.setInitiator(person);
        object.setState(En_CaseState.CREATED);
        object.setCreated(new Date());
        object.setCreatorInfo("junit-test-events");
        object.setName("Event-publisher test");
        object.setExtAppType("junit-test");
        object.setImpLevel(En_ImportanceLevel.BASIC.getId());
//        object.setExtAppCaseId(JUNIT_EVENT_PUB_01);

        Result<CaseObject> response = service.createCaseObject(getAuthToken(), object, person);
        Assert.assertTrue(response.isOk());

        // wait for async event
        Thread.sleep(2000);

        CaseComment comment = new CaseComment();
        comment.setCaseId(response.getData().getId());
        comment.setCreated(new Date());
        comment.setClientIp("-");
        comment.setCaseStateId(response.getData().getStateId());
        comment.setAuthorId(response.getData().getInitiatorId());
        comment.setText("A new comment, publishing test");
        comment.setCaseAttachments(Collections.emptyList());

        Result<CaseComment> r2 = caseCommentService.addCaseComment(getAuthToken(), caseType, comment, person);

        Assert.assertTrue(r2.isOk());

        // wait for async event
        Thread.sleep(2000);

        Assert.assertEquals(2, evRegistry.assembledCaseEvents.size());
        Assert.assertEquals(1, evRegistry.commentEvents.size());

        Assert.assertTrue(removeCaseObjectAndComments(object));
        Assert.assertTrue(personDAO.remove(person));
        Assert.assertTrue(companyDAO.remove(company));
    }

    @Before
    @After
    public void cleanup () {
        caseControlService.deleteByExtAppId(JUNIT_EVENT_PUB_01);
    }

    @Autowired
    CaseControlService caseControlService;
    @Autowired
    EventHandlerRegistry evRegistry;
    @Autowired
    CaseService service;
    @Autowired
    CaseCommentService caseCommentService;
}
