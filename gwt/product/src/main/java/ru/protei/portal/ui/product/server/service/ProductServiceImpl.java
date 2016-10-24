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

        log.info (" getProductList : param = " + param + " showDeprecated = " + state + " sortField = " + sortField.getFieldName() + " order " + sortDir.toString());

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

        log.info(" getProductById : id = " + productId);

        CoreResponse<DevUnit> result = productService.getProductById(productId);

        if (result.isOk()) {
            return result.getData();
        }
        else
            throw new RequestFailedException( result.getErrCode() );
    }

    @Override
    public Boolean saveProduct (DevUnit product) throws RequestFailedException {

        log.info(" saveProduct");

        if (product.getId() == null) {
            CoreResponse<Long> result = productService.createProduct(product);

            if (result.isOk()) {
                log.info( "createProduct: Status={}", result.isOk() );
                return result.getData() != null;
            }
            else
                throw new RequestFailedException( result.getErrCode() );
        }
        else
        {
            CoreResponse<Boolean> result = productService.updateProduct(product);

            if (result.isOk()) {
                log.info( "updateProduct: Status={}", result.isOk() );
                return result.getData() != null;
            }
            else
                throw new RequestFailedException( result.getErrCode() );
        }
    }


    @Autowired
    ru.protei.portal.core.service.dict.ProductService productService;

    private ProductQuery productQuery;

    private static final Logger log = LoggerFactory.getLogger( "web" );
}