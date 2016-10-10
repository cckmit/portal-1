package ru.protei.portal.ui.product.server.service;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.core.model.ent.Product;
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
    public List<Product> getProductList(String param, boolean showDepricated) throws RequestFailedException {

        log.info (" getProductList : param = " + param + " showDepricated = " + showDepricated);

        //HttpListResult<Product> result = productService.list(param);
        //return result.items;

        // временная заглушка вместо получения списка из БД
        List <Product> products = new ArrayList<Product>();
        Product pr = new Product();
        pr.setPname("EACD4");
        pr.setDepricated(false);
        products.add(pr);

        pr = new Product();
        pr.setPname("WelcomeSMS");
        pr.setDepricated(false);
        products.add(pr);

        pr = new Product();
        pr.setPname("SMS_Firewall");
        pr.setDepricated(false);
        products.add(pr);

        if (showDepricated)
        {
            pr = new Product();
            pr.setPname("CWS");
            pr.setDepricated(true);
            products.add(pr);
        }

        if (param != null && !param.trim().isEmpty())
        {
            List<Product> flt_products = new ArrayList<Product>();
            for (Product p : products)
            {
                if (p.getPname().contains(param))
                    flt_products.add(p);
            }
            return flt_products;
        }

        return products;
   }

    @Override
    public Product getProductById(Long productId) throws RequestFailedException {

        log.info (" getProductById : id = " + productId);

        return null;
    }


    @Autowired
    ru.protei.portal.core.service.dict.ProductService productService;

    private static final Logger log = LoggerFactory.getLogger( "web" );
}