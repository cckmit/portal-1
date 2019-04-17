package ru.protei.portal.test.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.config.DatabaseConfiguration;
import ru.protei.portal.config.MainTestsConfiguration;
import ru.protei.portal.core.model.dao.DevUnitDAO;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.service.ProductService;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.Date;
import java.util.List;

/**
 * Created by michael on 11.10.16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class, JdbcConfigurationContext.class, DatabaseConfiguration.class, MainTestsConfiguration.class})
public class ProductServiceTest {

    @Test
    public void testCreateAndGetProduct () {

        DevUnit product = new DevUnit();

        product.setName("Test Product");
        product.setCreated(new Date());
        product.setCreatorId(1L);
        product.setInfo("Unit-test");
        product.setStateId(En_DevUnitState.ACTIVE.getId());
        product.setTypeId(En_DevUnitType.PRODUCT.getId());

        Assert.assertNotNull(devUnitDAO.persist(product));

        CoreResponse<List<DevUnit>> result = productService.productList( null, new ProductQuery() );

        Assert.assertNotNull(result);
        Assert.assertTrue(result.getDataAmountTotal() > 0);

        Assert.assertNotNull(result.getData());
        Assert.assertTrue(result.getData().size() > 0);

        System.out.println(result.getData().get(0).getName());

        Assert.assertTrue(devUnitDAO.remove(product));
    }

    @Test
    public void testUniqueProductByName () {

        String name = "Billing";


        DevUnit product = devUnitDAO.checkExistsByName(En_DevUnitType.PRODUCT, name);
        Assert.assertNull(product);

        System.out.println(" product with " + name + " is not exist | product " + product);

        CoreResponse<Boolean> result = productService.checkUniqueProductByName( null, name, 1L);

        Assert.assertFalse(result.isError());
        Assert.assertTrue(result.isOk());

        System.out.println(" product name " + name + " is uniq = " + result.getData());


        name = "OMS3456";

        product = devUnitDAO.checkExistsByName(En_DevUnitType.PRODUCT, name);
        Assert.assertNull(product);

        System.out.println(" product with " + name + " is not exist");

        result = productService.checkUniqueProductByName( null, name, null);

        Assert.assertFalse(result.isError());
        Assert.assertTrue(result.isOk());

        System.out.println(" product name " + name + " is uniq = " + result.getData());

    }

    @Autowired
    ProductService productService;
    @Autowired
    DevUnitDAO devUnitDAO;
}
