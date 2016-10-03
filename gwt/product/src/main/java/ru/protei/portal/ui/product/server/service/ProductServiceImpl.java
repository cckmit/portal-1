package ru.protei.portal.ui.product.server.service;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.HttpListResult;
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
    public List<Product> getProductList(String name) throws RequestFailedException {

        log.info (" getProductList : ");
        HttpListResult<Product> result = productService.list(name);

        return result.items;
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