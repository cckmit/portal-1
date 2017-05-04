package ru.protei.portal.wsapi.test;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.service.CaseControlService;
import ru.protei.portal.core.wsapi.SupportTicketRequest;
import ru.protei.portal.core.wsapi.WSCaseModule;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.Date;
import java.util.List;

/**
 * Created by Mike on 03.05.2017.
 */
public class LocalInstanceTest {

    public static final String JUNIT001_1_case_ext_id = "junit001-1";
    static WSCaseModule caseModule;
    static ApplicationContext ctx;

    @BeforeClass
    public static void init() {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(WSCaseModule.class);
        factory.setAddress("http://localhost:8080/portal.wsapi/api/ws/WSCaseModule");

        caseModule = (WSCaseModule) factory.create();


        ctx = new AnnotationConfigApplicationContext(CoreConfigurationContext.class, JdbcConfigurationContext.class, MainConfiguration.class);
    }

    @Test
    public void test001 () {

        String pingResponse = caseModule.ping(new Date());
        System.out.println(pingResponse);
        Assert.assertNotNull(pingResponse);

        CaseObject noExistingObj = caseModule.getCaseObject(-1L);
        Assert.assertNull(noExistingObj);


        SupportTicketRequest request = new SupportTicketRequest();
        request.setCompanyName("WS-API-Company-Test");
        request.setContactEmail("zavedeev@protei.ru");
        request.setDescription("junit-test-001");
        request.setSubject("junit-auto-test");
        request.setExtId(JUNIT001_1_case_ext_id);
        request.setProductName("junit");
        request.setPersonName("Zavedeev Michael");

        CaseObject caseObject = caseModule.createSupportTicket(request);

        Assert.assertNotNull(caseObject);
        Assert.assertNotNull(caseObject.getId());
        Assert.assertEquals(JUNIT001_1_case_ext_id, caseObject.getExtAppCaseId());

        System.out.println(caseObject);

        Assert.assertNotNull(caseModule.getCaseObject(caseObject.getId()));

        CaseComment caseComment1 = caseModule.addComment(caseObject.getId(), "Blah blah, test comment");

        Assert.assertNotNull(caseComment1);
        System.out.println(caseComment1);

        Assert.assertNotNull(caseModule.closeCase(caseObject.getId(), "closed"));

        CaseObject finObj = caseModule.getCaseObjectExtId(JUNIT001_1_case_ext_id);

        Assert.assertNotNull(finObj);

        List<CaseComment> comments = caseModule.getCaseComments(finObj.getId());

        Assert.assertNotNull(comments);
        Assert.assertTrue(comments.size() > 0);

        comments.forEach(c -> System.out.println(c));
    }


    @After
    @Before
    public void cleanup () {
        ctx.getBean(CaseControlService.class).deleteByExtAppId(JUNIT001_1_case_ext_id);
    }
}
