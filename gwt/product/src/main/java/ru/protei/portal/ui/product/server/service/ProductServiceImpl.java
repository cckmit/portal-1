package ru.protei.portal.ui.product.server.service;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.core.model.view.ProductView;
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
    public List<ProductView> getProductList(String param, boolean showDepricated) throws RequestFailedException {

        log.info (" getProductList : param = " + param + " showDepricated = " + showDepricated);

        //HttpListResult<ProductView> result = productService.list(param);
        //return result.items;

        // временная заглушка вместо получения списка из БД
        List <ProductView> products = new ArrayList<ProductView>();
        ProductView pr = new ProductView();
        pr.setName("EACD4");
        pr.setActive(true);
        products.add(pr);

        pr = new ProductView();
        pr.setName("WelcomeSMS");
        pr.setActive(true);
        products.add(pr);

        pr = new ProductView();
        pr.setName("SMS_Firewall");
        pr.setActive(true);
        products.add(pr);

        if (showDepricated)
        {
            pr = new ProductView();
            pr.setName("CWS");
            pr.setActive(false);
            products.add(pr);
        }

        if (param != null && !param.trim().isEmpty())
        {
            List<ProductView> flt_products = new ArrayList<ProductView>();
            for (ProductView p : products)
            {
                if (p.getName().contains(param))
                    flt_products.add(p);
            }
            return flt_products;
        }

        return products;
   }

    @Override
    public ProductView getProductById(Long productId) throws RequestFailedException {

        log.info (" getProductById : id = " + productId);

        return null;
    }


    @Autowired
    ru.protei.portal.core.service.dict.ProductService productService;

    private static final Logger log = LoggerFactory.getLogger( "web" );
}