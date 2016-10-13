package ru.protei.portal.ui.product.server.service;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    public List<DevUnit> getProductList(String param, Boolean state, En_SortField sortField, Boolean sortDir) throws RequestFailedException {

        log.info (" getProductList : param = " + param + " showDeprecated = " + state + " sortField = " + sortField.getFieldName() + " order " + sortDir.toString());

        productQuery = new ProductQuery();
        productQuery.setSearchString(param);
        productQuery.setState(state ? null : En_DevUnitState.ACTIVE);
        productQuery.setSortField(sortField);
        productQuery.setSortDir(sortDir ? En_SortDir.ASC : En_SortDir.DESC);

        HttpListResult<DevUnit> result = productService.list(productQuery);

        return result.items;

   }

    @Override
    public DevUnit getProductById(Long productId) throws RequestFailedException {

        log.info (" getProductById : id = " + productId);

        return null;
    }


    @Autowired
    ru.protei.portal.core.service.dict.ProductService productService;

    private ProductQuery productQuery;

    private static final Logger log = LoggerFactory.getLogger( "web" );
}