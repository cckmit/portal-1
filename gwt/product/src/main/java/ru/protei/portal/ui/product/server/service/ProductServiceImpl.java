package ru.protei.portal.ui.product.server.service;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.api.struct.HttpListResult;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.product.client.service.ProductService;

import java.util.List;

/**
 * Реализация сервиса управления продуктами
 */
@Service( "ProductService" )
public class ProductServiceImpl extends RemoteServiceServlet implements ProductService {

    @Override
    public List<DevUnit> getProductList(String param, En_DevUnitState state, En_SortField sortField, Boolean sortDir) throws RequestFailedException {

        log.info (" getProductList: param={} | showDeprecated={} | sortField={} | order={}", param, state, sortField.getFieldName(), sortDir.toString());

        productQuery = new ProductQuery();
        productQuery.setSearchString(param);
        productQuery.setState(state);
        productQuery.setSortField(sortField);
        productQuery.setSortDir(sortDir ? En_SortDir.ASC : En_SortDir.DESC);

        HttpListResult<DevUnit> result = productService.list(productQuery);

        return result.items;

   }

    @Override
    public DevUnit getProductById(Long productId) throws RequestFailedException {

        log.info(" getProductById: id={}", productId);

        CoreResponse<DevUnit> result = productService.getProductById(productId);

        if (result.isError())
            throw new RequestFailedException( result.getErrCode() );

        return result.getData();
    }

    @Override
    public Boolean saveProduct (DevUnit product) throws RequestFailedException {

        log.info(" saveProduct: product={}", product);

        if (!isNameUnique(product.getName(), product.getId()))
            throw new RequestFailedException ();

        CoreResponse response = product.getId() == null ?
                productService.createProduct( product ) : productService.updateProduct( product );

        log.info(" saveProduct: response={}", response.isError() );

        if ( response.isError() )
            throw new RequestFailedException( response.getErrCode() );

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
            throw new RequestFailedException(response.getErrCode());

        log.info(" isNameUnique: response={}", response.getData());

        return response.getData();
    }


    @Autowired
    ru.protei.portal.core.service.dict.ProductService productService;

    private ProductQuery productQuery;

    private static final Logger log = LoggerFactory.getLogger( "web" );
}