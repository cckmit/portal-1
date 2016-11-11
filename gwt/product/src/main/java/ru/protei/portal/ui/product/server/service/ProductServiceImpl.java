package ru.protei.portal.ui.product.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.ui.common.client.service.ProductService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Реализация сервиса управления продуктами
 */
@Service( "ProductService" )
public class ProductServiceImpl implements ProductService {

    @Override
    public List<DevUnit> getProductList(ProductQuery productQuery) throws RequestFailedException {

        log.info (" getProductList: search={} | showDeprecated={} | sortField={} | order={}",
                productQuery.getSearchString(), productQuery.getState(), productQuery.getSortField(), productQuery.getSortDir());

        CoreResponse<List<DevUnit>> result = productService.list(productQuery);

        if (result.isError())
            throw new RequestFailedException(result.getStatus());

        return result.getData();

   }

    @Override
    public DevUnit getProductById(Long productId) throws RequestFailedException {

        log.info(" getProductById: id={}", productId);

        CoreResponse<DevUnit> response = productService.getProductById(productId);

        if (response.isError())
            throw new RequestFailedException( response.getStatus() );

        log.info(" getProductById: id={}", response.getData());

        return response.getData();
    }

    @Override
    public Boolean saveProduct (DevUnit product) throws RequestFailedException {

        log.info(" saveProduct: product={}", product);

        if (!isNameUnique(product.getName(), product.getId()))
            throw new RequestFailedException ();

        CoreResponse response = product.getId() == null ?
                productService.createProduct( product ) : productService.updateProduct( product );

        if ( response.isError() )
            throw new RequestFailedException( response.getStatus() );

        log.info(" saveProduct: response.getData()={}", response.getData() );

        return response.getData() != null;
    }


    @Override
    public boolean isNameUnique(String name, Long excludeId) throws RequestFailedException {

        log.info(" isNameUnique: name={}", name);

        if (name == null || name.isEmpty())
            throw new RequestFailedException ();

        CoreResponse<Boolean> response = productService.checkUniqueProductByName(name, excludeId);

        if (response.isError())
            throw new RequestFailedException(response.getStatus());

        log.info(" isNameUnique: response={}", response.getData());

        return response.getData();
    }


    @Autowired
    ru.protei.portal.core.service.ProductService productService;

    private ProductQuery productQuery;

    private static final Logger log = LoggerFactory.getLogger( "web" );
}