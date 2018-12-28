package ru.protei.portal.test.service;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.service.CaseCommentService;
import ru.protei.portal.core.service.CaseService;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by michael on 11.10.16.
 */
public class CaseServiceTest {

    static ApplicationContext ctx;
    static En_CaseType caseType;

    @BeforeClass
    public static void init () {
         ctx = new AnnotationConfigApplicationContext (CoreConfigurationContext.class, JdbcConfigurationContext.class, MainConfiguration.class);
         caseType = En_CaseType.CRM_SUPPORT;
    }

    @Test
    public void testGetCaseCommentList () {

        long testCaseId = 377321;

        List<CaseComment> comments = ctx.getBean(CaseCommentDAO.class).getCaseComments(testCaseId);
        Assert.assertNotNull(comments);
        System.out.println( "case " + testCaseId + " comment list size = " +  comments.size() );

        CaseCommentService service = ctx.getBean(CaseCommentService.class);
        Assert.assertNotNull(service);
        CoreResponse<List<CaseComment>> result = service.getCaseCommentList(null, caseType, testCaseId);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isOk());
        Assert.assertNotNull(result.getData());
        System.out.println(" size = " + result.getData().size());
        Assert.assertTrue(result.getData().size() > 0);

        for (CaseComment comment : result.getData()) {
            System.out.println(comment.toString());
            System.out.println("----------------------");
        }

    }

    @Test
    public void testCRUD_CaseComment () {

        long testCaseId = 377321;
        String userIp = "192.168.100.156";
        long personId = 46;

        // create
        CaseComment comment = new CaseComment();

        comment.setCaseId( testCaseId );
        comment.setAuthorId( personId );
        comment.setClientIp( userIp );
        comment.setCreated( new Date() );
        comment.setText( "Unit-test - тестовый комментарий" );

        CaseCommentService service = ctx.getBean(CaseCommentService.class);

        CoreResponse<CaseComment> result = service.addCaseComment( null, caseType, comment, null );
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isOk());
        Assert.assertNotNull(result.getData());

        System.out.println( result.getData() );

        CoreResponse<List<CaseComment>> resultList = service.getCaseCommentList(null, caseType, testCaseId);
        System.out.println(" size after add = " + resultList.getData().size());

        // update
        comment = result.getData();

        if (comment != null) {
            comment.setText( "Unit-test - тестовый комментарий (update)" );

            result = service.updateCaseComment( null, caseType, comment, null );
            Assert.assertNotNull( result );
            Assert.assertTrue( result.isOk() );
            Assert.assertNotNull( result.getData() );

            System.out.println( result.getData() );
        }
        resultList = service.getCaseCommentList(null, caseType, testCaseId);
        System.out.println(" size after update = " + resultList.getData().size());

        // delete
        if (comment != null) {
            CoreResponse<Boolean> result2 = service.removeCaseComment(null, caseType, comment, personId);
            Assert.assertNotNull( result2 );
            Assert.assertTrue( result2.isOk() );

            System.out.println( result2.getData() );
        }
        resultList = service.getCaseCommentList(null, caseType, testCaseId);
        System.out.println(" size after remove = " + resultList.getData().size());

    }

}
