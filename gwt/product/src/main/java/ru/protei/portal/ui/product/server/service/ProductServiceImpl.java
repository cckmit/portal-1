package ru.protei.portal.ui.product.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.model.query.ProductDirectionQuery;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.service.ProductService;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
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

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse < List< DevUnit > > result = productService.productList( productQuery, descriptor.getLogin().getRoles() );

        if ( result.isError() )
            throw new RequestFailedException( result.getStatus() );

        return result.getData();

   }

    @Override
    public DevUnit getProduct( Long productId ) throws RequestFailedException {

        log.debug( "getProduct(): id={}", productId );

        //TODO используется для отображения карточки продукта, думаю проверка роли PRODUCT_VIEW логична
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse< DevUnit > response = productService.getProduct( productId, descriptor.getLogin().getRoles() );

        if ( response.isError() )
            throw new RequestFailedException( response.getStatus() );

        log.debug( "getProduct(): id={}", response.getData() );

        return response.getData();
    }

    @Override
    public Boolean saveProduct( DevUnit product ) throws RequestFailedException {

        log.debug( "saveProduct(): product={}", product );

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        if ( !isNameUnique( product.getName(), product.getId() ) )
            throw new RequestFailedException ();

        CoreResponse response = product.getId() == null ?
                productService.createProduct( product ) : productService.updateProduct( product, descriptor.getLogin().getRoles() );

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

        //TODO используется в Button селектор с продуктами ProductButtonSelector, считаю что привилегия PRODUCT_VIEW не для этого

        CoreResponse< List<ProductShortView> > result = productService.shortViewList( query );

        log.debug( "result status: {}, data-amount: {}", result.getStatus(), result.isOk() ? result.getDataAmountTotal() : 0 );

        if ( result.isError() )
            throw new RequestFailedException( result.getStatus() );

        return result.getData();
    }

    @Override
    public List<ProductDirectionInfo> getProductDirectionList( ProductDirectionQuery query ) throws RequestFailedException {

        log.debug( "getProductDirectionList(): query={}", query );

        //TODO используется в Button селектор с продуктами ProductDirectionInputSelector, считаю что привилегия PRODUCT_VIEW не для этого

        String[] names = new String[] {
                "Система 112", "Call Center", "Видеонаблюдение", "Видеоаналитика"
        };

        CoreResponse< List< ProductDirectionInfo > > result = productService.productDirectionList( query );

        if ( result.isError() )
            throw new RequestFailedException( result.getStatus() );

        return result.getData();
    }

    private UserSessionDescriptor getDescriptorAndCheckSession() throws RequestFailedException {
        UserSessionDescriptor descriptor = sessionService.getUserSessionDescriptor( httpServletRequest );
        log.info( "userSessionDescriptor={}", descriptor );
        if ( descriptor == null ) {
            throw new RequestFailedException( En_ResultStatus.SESSION_NOT_FOUND );
        }

        return descriptor;
    }

    @Autowired
    ru.protei.portal.core.service.ProductService productService;
    @Autowired
    SessionService sessionService;
    @Autowired
    HttpServletRequest httpServletRequest;

    private static final Logger log = LoggerFactory.getLogger( "web" );
}