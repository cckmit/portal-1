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
import ru.protei.portal.core.model.dao.DevUnitDAO;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.service.ProductService;
import ru.protei.portal.embeddeddb.DatabaseConfiguration;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.Date;

/**
 * Created by michael on 11.10.16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class, JdbcConfigurationContext.class,
        DatabaseConfiguration.class, IntegrationTestsConfiguration.class
})
public class ProductServiceTest {

    @Test
    public void testCreateAndGetProduct () {

        DevUnit product = createTestProduct();

        Assert.assertNotNull(devUnitDAO.persist(product));

        Result<SearchResult<DevUnit>> result = productService.getProducts( null, new ProductQuery() );

        Assert.assertNotNull(result);

        Assert.assertNotNull(result.getData());
        Assert.assertNotNull(result.getData().getResults());
        Assert.assertTrue(result.getData().getResults().size() > 0);

        log.info(result.getData().getResults().get(0).getName());

        Assert.assertTrue(devUnitDAO.remove(product));
    }

    @Test
    public void testUniqueProductByName () {

        String name = "Billing";


        DevUnit product = devUnitDAO.checkExistsByName(En_DevUnitType.PRODUCT, name);
        Assert.assertNull(product);

        log.info(" product with " + name + " is not exist | product " + product);

        Result<Boolean> result = productService.checkUniqueProductByName( null, name, En_DevUnitType.PRODUCT, 1L);

        Assert.assertFalse(result.isError());
        Assert.assertTrue(result.isOk());

        log.info(" product name " + name + " is uniq = " + result.getData());


        name = "OMS3456";

        product = devUnitDAO.checkExistsByName(En_DevUnitType.PRODUCT, name);
        Assert.assertNull(product);

        log.info(" product with " + name + " is not exist");

        result = productService.checkUniqueProductByName( null, name, En_DevUnitType.PRODUCT,null);

        Assert.assertFalse(result.isError());
        Assert.assertTrue(result.isOk());

        log.info(" product name " + name + " is uniq = " + result.getData());

    }

    @Test
    public void testChangeProductState(){
        DevUnit product = createTestProduct();

        Assert.assertNotNull(devUnitDAO.persist(product));

        product.setStateId(En_DevUnitState.DEPRECATED.getId());
        Result toDeprecated = productService.updateState(null, product.getId(), En_DevUnitState.DEPRECATED);
        DevUnit productDeprecated =  devUnitDAO.get(product.getId());

        Assert.assertNotNull(toDeprecated);
        Assert.assertEquals(En_DevUnitState.DEPRECATED, productDeprecated.getState());


        product.setStateId(En_DevUnitState.ACTIVE.getId());
        Result toActive = productService.updateState(null, product.getId(), En_DevUnitState.ACTIVE);
        DevUnit productActive =  devUnitDAO.get(product.getId());

        Assert.assertNotNull(toActive);
        Assert.assertEquals(En_DevUnitState.ACTIVE, productActive.getState());
    }


    private DevUnit createTestProduct(){
        DevUnit product = new DevUnit();

        product.setName("Test Product");
        product.setCreated(new Date());
        product.setCreatorId(1L);
        product.setInfo("Unit-test");
        product.setStateId(En_DevUnitState.ACTIVE.getId());
        product.setType(En_DevUnitType.PRODUCT);

        return product;
    }

    @Autowired
    ProductService productService;
    @Autowired
    DevUnitDAO devUnitDAO;

    private static final Logger log = LoggerFactory.getLogger(ProductServiceTest.class);
}
