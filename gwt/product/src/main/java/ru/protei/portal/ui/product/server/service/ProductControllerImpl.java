package ru.protei.portal.ui.product.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.query.ProductDirectionQuery;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.ProductController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.size;

/**
 * Реализация сервиса управления продуктами
 */
@Service( "ProductController" )
public class ProductControllerImpl implements ProductController {

    @Override
    public SearchResult< DevUnit > getProductList( ProductQuery productQuery ) throws RequestFailedException {

        log.info( "getProductList(): search={} | showDeprecated={} | sortField={} | order={}",
                productQuery.getSearchString(), productQuery.getState(), productQuery.getSortField(), productQuery.getSortDir() );

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(productService.getProducts(token, productQuery));

   }

    @Override
    public DevUnit getProduct( Long productId ) throws RequestFailedException {

        log.info( "getProduct(): id={}", productId );

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result< DevUnit > response = productService.getProduct( token, productId );

        if ( response.isError() )
            throw new RequestFailedException( response.getStatus() );

        log.info( "getProduct(): id={}", response.getData() );

        return response.getData();
    }

    @Override
    public DevUnit saveProduct( DevUnit product ) throws RequestFailedException {

        log.info( "saveProduct(): product={}", product );

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        if ( product == null || !isNameUnique( product.getName(), product.getType(), product.getId() ) )
            throw new RequestFailedException (En_ResultStatus.INCORRECT_PARAMS);

        Result<DevUnit> response = product.getId() == null
                ? productService.createProduct( token, product )
                : productService.updateProduct( token, product );

        if ( response.isError() )
            throw new RequestFailedException( response.getStatus() );

        log.info( "saveProduct(): response.getData()={}", response.getData() );

        return response.getData();
    }

    @Override
    public Boolean updateState(Long productId, En_DevUnitState state) throws RequestFailedException {

        log.info( "updateState(): productId={} | state={}", productId, state);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<En_DevUnitState> response = productService.updateState(token, productId, state);

        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }

        log.info( "updateState(): response.getData()={}", response.getData() );

        return response.getData() != null;
    }


    @Override
    public boolean isNameUnique(String name, En_DevUnitType type, Long excludeId ) throws RequestFailedException {

        log.info( "isNameUnique(): name={}", name );

        if ( name == null || name.isEmpty() )
            throw new RequestFailedException ();

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        Result< Boolean > response = productService.checkUniqueProductByName( token, name, type, excludeId );

        if ( response.isError() )
            throw new RequestFailedException( response.getStatus() );

        log.info( "isNameUnique(): response={}", response.getData() );

        return response.getData();
    }

    @Override
    public List<ProductShortView> getProductViewList( ProductQuery query ) throws RequestFailedException {

        log.info( "getProductViewList(): searchPattern={} | showDeprecated={} | sortField={} | sortDir={}",
                query.getSearchString(), query.getState(), query.getSortField(), query.getSortDir() );

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        Result< List<ProductShortView> > result = productService.shortViewList( token, query );

        log.info( "result status: {}, data-amount: {}", result.getStatus(), size(result.getData()) );

        if ( result.isError() )
            throw new RequestFailedException( result.getStatus() );

        return result.getData();
    }

    @Override
    public List<ProductShortView> getProductsViewListWithChildren(ProductQuery query) throws RequestFailedException {
        log.info("getProductViewListWithChildren(): ProductQuery={}", query);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        Result<List<ProductShortView>> result = productService.productsShortViewListWithChildren(token, query);

        log.info("result status: {}, data-amount: {}", result.getStatus(), size(result.getData()));

        if (result.isError()) {
            throw new RequestFailedException(result.getStatus());
        }

        return result.getData();
    }

    @Override
    public List<ProductDirectionInfo> getProductDirectionList( ProductDirectionQuery query ) throws RequestFailedException {

        log.info( "getProductDirectionList(): query={}", query );

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        Result< List< ProductDirectionInfo > > result = productService.productDirectionList( token, query );

        if ( result.isError() )
            throw new RequestFailedException( result.getStatus() );

        return result.getData();
    }

    @Autowired
    ru.protei.portal.core.service.ProductService productService;
    @Autowired
    SessionService sessionService;
    @Autowired
    HttpServletRequest httpServletRequest;

    private static final Logger log = LoggerFactory.getLogger(ProductControllerImpl.class);
}
