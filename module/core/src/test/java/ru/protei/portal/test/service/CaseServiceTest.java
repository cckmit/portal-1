package ru.protei.portal.test.service;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.UserRole;
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

    @BeforeClass
    public static void init () {
         ctx = new AnnotationConfigApplicationContext (CoreConfigurationContext.class, JdbcConfigurationContext.class, MainConfiguration.class);
    }

    @Test
    public void testGetCaseCommentList () {

        long testCaseId = 377321;

        List<CaseComment> comments = ctx.getBean(CaseCommentDAO.class).getCaseComments(testCaseId);
        Assert.assertNotNull(comments);
        System.out.println( "case " + testCaseId + " comment list size = " +  comments.size() );

        CaseService service = ctx.getBean(CaseService.class);
        Assert.assertNotNull(service);
        CoreResponse<List<CaseComment>> result = service.getCaseCommentList(testCaseId);

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

        Set< UserRole > roles = new HashSet<>();
        UserRole role = new UserRole();
        role.setId( 1L );
        Set<En_Privilege> privileges = new HashSet<>();
        privileges.add( En_Privilege.ISSUE_VIEW );
        privileges.add( En_Privilege.ISSUE_EDIT );
        role.setPrivileges( privileges );

        CaseService service = ctx.getBean(CaseService.class);

        CoreResponse<CaseComment> result = service.addCaseComment( comment, null, roles );
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isOk());
        Assert.assertNotNull(result.getData());

        System.out.println( result.getData() );

        CoreResponse<List<CaseComment>> resultList = service.getCaseCommentList(testCaseId);
        System.out.println(" size after add = " + resultList.getData().size());

        // update
        comment = result.getData();

        if (comment != null) {
            comment.setText( "Unit-test - тестовый комментарий (update)" );

            result = service.updateCaseComment( comment, null, roles );
            Assert.assertNotNull( result );
            Assert.assertTrue( result.isOk() );
            Assert.assertNotNull( result.getData() );

            System.out.println( result.getData() );
        }
        resultList = service.getCaseCommentList(testCaseId);
        System.out.println(" size after update = " + resultList.getData().size());

        // delete
        if (comment != null) {
            result = service.removeCaseComment( comment, personId, roles );
            Assert.assertNotNull( result );
            Assert.assertTrue( result.isOk() );
            Assert.assertNotNull( result.getData() );

            System.out.println( result.getData() );
        }
        resultList = service.getCaseCommentList(testCaseId);
        System.out.println(" size after remove = " + resultList.getData().size());

    }

}
