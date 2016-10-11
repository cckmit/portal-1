package ru.protei.portal.ui.product.server.service;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.product.client.service.ProductService;

import java.util.ArrayList;
import java.util.List;

/**
 * Реализация сервиса управления продуктами
 */
@Service( "ProductService" )
public class ProductServiceImpl extends RemoteServiceServlet implements ProductService {

    @Override
    public List<DevUnit> getProductList(String param, boolean showDepricated) throws RequestFailedException {

        log.info (" getProductList : param = " + param + " showDepricated = " + showDepricated);

        //HttpListResult<ProductView> result = productService.list(param);
        //return result.items;

        // временная заглушка вместо получения списка из БД
        List <DevUnit> products = new ArrayList<DevUnit>();
        DevUnit pr = new DevUnit();
        pr.setName("EACD4");
        products.add(pr);

        pr = new DevUnit();
        pr.setName("WelcomeSMS");
        products.add(pr);

        if (showDepricated)
        {
            pr = new DevUnit();
            pr.setName("CWS");
            products.add(pr);
        }

        return products;
   }

    @Override
    public DevUnit getProductById(Long productId) throws RequestFailedException {

        log.info (" getProductById : id = " + productId);

        return null;
    }


    @Autowired
    ru.protei.portal.core.service.dict.ProductService productService;

    private static final Logger log = LoggerFactory.getLogger( "web" );
}