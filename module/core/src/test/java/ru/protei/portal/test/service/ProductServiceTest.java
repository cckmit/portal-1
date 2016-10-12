package ru.protei.portal.test.service;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.api.struct.HttpListResult;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.portal.core.model.dao.DevUnitDAO;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.service.dict.ProductService;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.Date;

/**
 * Created by michael on 11.10.16.
 */
public class ProductServiceTest {

    static ApplicationContext ctx;

//    static DevUnit activeProduct;

    @BeforeClass
    public static void init () {
         ctx = new AnnotationConfigApplicationContext (CoreConfigurationContext.class, JdbcConfigurationContext.class, MainConfiguration.class);


    }

    @Test
    public void testCreateAndGetProduct () {

        DevUnit product = new DevUnit();

        product.setName("Test Product");
        product.setCreated(new Date());
        product.setCreatorId(1L);
        product.setInfo("Unit-test");
        product.setStateId(En_DevUnitState.ACTIVE.getId());
        product.setTypeId(En_DevUnitType.PRODUCT.getId());

        Assert.assertNotNull(ctx.getBean(DevUnitDAO.class).persist(product));


        HttpListResult<DevUnit> result = ctx.getBean(ProductService.class).list("%",null,En_SortField.prod_name,"asc");

        Assert.assertNotNull(result);
        Assert.assertTrue(result.getTotalSize() > 0);

        Assert.assertNotNull(result.getItems());
        Assert.assertTrue(result.getItems().size() > 0);

        System.out.println(result.getItems().get(0).getName());

        Assert.assertNotNull(ctx.getBean(DevUnitDAO.class).remove(product));
    }

}
