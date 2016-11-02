package ru.protei.portal.test.service;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.api.struct.HttpListResult;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.dao.CompanyGroupDAO;
import ru.protei.portal.core.model.dao.CompanyGroupItemDAO;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanyGroup;
import ru.protei.portal.core.model.ent.CompanyGroupItem;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.BaseQuery;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.core.service.dict.CompanyService;
import ru.protei.portal.core.service.dict.ContactService;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.Date;
import java.util.List;

/**
 * Created by michael on 11.10.16.
 */
public class ContactServiceTest {

    static ApplicationContext ctx;

    @BeforeClass
    public static void init () {
         ctx = new AnnotationConfigApplicationContext (CoreConfigurationContext.class, JdbcConfigurationContext.class, MainConfiguration.class);
    }


    @Test
    public void testGet () {

        ContactService service = ctx.getBean(ContactService.class);

        Assert.assertNotNull(service);

        HttpListResult<Person> result = service.contactList(new ContactQuery("Михаил", En_SortField.person_full_name, En_SortDir.ASC));

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getItems());
        Assert.assertTrue(result.getTotalSize() > 0);
    }
}
