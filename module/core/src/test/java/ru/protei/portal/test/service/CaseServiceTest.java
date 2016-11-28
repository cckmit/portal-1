package ru.protei.portal.test.service;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.CompanyService;
import ru.protei.portal.core.service.ContactService;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.Date;
import java.util.List;

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

        long textCaseId = 377321;

        List<CaseComment> comments = ctx.getBean(CaseCommentDAO.class).getCaseComments(textCaseId);
        Assert.assertNotNull(comments);
        System.out.println( "case " + textCaseId + " comment list size = " +  comments.size() );


        CaseService service = ctx.getBean(CaseService.class);
        Assert.assertNotNull(service);
        CoreResponse<List<CaseComment>> result = service.getCaseCommentList(textCaseId);

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
}
