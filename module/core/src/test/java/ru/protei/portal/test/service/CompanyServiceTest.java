package ru.protei.portal.test.service;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.api.struct.HttpListResult;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.service.dict.CompanyService;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

/**
 * Created by michael on 11.10.16.
 */
public class CompanyServiceTest {

    static ApplicationContext ctx;

    @BeforeClass
    public static void init () {
         ctx = new AnnotationConfigApplicationContext (CoreConfigurationContext.class, JdbcConfigurationContext.class, MainConfiguration.class);
    }


    @Test
    public void testGetCompanyList () {

        CompanyService service = ctx.getBean(CompanyService.class);

        Assert.assertNotNull(service);

        HttpListResult<Company> result = service.list("%", En_SortField.comp_name,"asc");

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getItems());
        Assert.assertTrue(result.getTotalSize() > 0);

//        for (Company company : result.getItems())
//            System.out.println(company.getCname());
    }


}
