package ru.protei.portal.ui.product.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.service.ProductService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Реализация сервиса управления продуктами
 */
@Service( "ProductService" )
public class ProductServiceImpl implements ProductService {

    @Override
    public List< DevUnit > getProductList( ProductQuery productQuery ) throws RequestFailedException {

        log.debug( "getProductList(): search={} | showDeprecated={} | sortField={} | order={}",
                productQuery.getSearchString(), productQuery.getState(), productQuery.getSortField(), productQuery.getSortDir() );

        CoreResponse < List< DevUnit > > result = productService.productList( productQuery );

        if ( result.isError() )
            throw new RequestFailedException( result.getStatus() );

        return result.getData();

   }

    @Override
    public DevUnit getProduct( Long productId ) throws RequestFailedException {

        log.debug( "getProduct(): id={}", productId );

        CoreResponse< DevUnit > response = productService.getProduct( productId );

        if ( response.isError() )
            throw new RequestFailedException( response.getStatus() );

        log.debug( "getProduct(): id={}", response.getData() );

        return response.getData();
    }

    @Override
    public Boolean saveProduct( DevUnit product ) throws RequestFailedException {

        log.debug( "saveProduct(): product={}", product );

        if ( !isNameUnique( product.getName(), product.getId() ) )
            throw new RequestFailedException ();

        CoreResponse response = product.getId() == null ?
                productService.createProduct( product ) : productService.updateProduct( product );

        if ( response.isError() )
            throw new RequestFailedException( response.getStatus() );

        log.debug( "saveProduct(): response.getData()={}", response.getData() );

        return response.getData() != null;
    }


    @Override
    public boolean isNameUnique( String name, Long excludeId ) throws RequestFailedException {

        log.debug( "isNameUnique(): name={}", name );

        if ( name == null || name.isEmpty() )
            throw new RequestFailedException ();

        CoreResponse< Boolean > response = productService.checkUniqueProductByName( name, excludeId );

        if ( response.isError() )
            throw new RequestFailedException( response.getStatus() );

        log.debug( "isNameUnique(): response={}", response.getData() );

        return response.getData();
    }

    @Override
    public List<ProductShortView> getProductViewList( ProductQuery query ) throws RequestFailedException {

        log.debug( "getProductViewList(): searchPattern={} | showDeprecated={} | sortField={} | sortDir={}",
                query.getSearchString(), query.getState(), query.getSortField(), query.getSortDir() );

        CoreResponse< List<ProductShortView> > result = productService.shortViewList( query );

        log.debug( "result status: {}, data-amount: {}", result.getStatus(), result.isOk() ? result.getDataAmountTotal() : 0 );

        if ( result.isError() )
            throw new RequestFailedException( result.getStatus() );

        return result.getData();
    }

    @Autowired
    ru.protei.portal.core.service.ProductService productService;

    private static final Logger log = LoggerFactory.getLogger( "web" );
}