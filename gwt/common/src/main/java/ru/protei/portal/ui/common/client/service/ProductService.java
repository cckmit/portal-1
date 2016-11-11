package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Сервис управления продуктами
 */
@RemoteServiceRelativePath( "springGwtServices/ProductService" )
public interface ProductService extends RemoteService {

    List<DevUnit> getProductList (ProductQuery query) throws RequestFailedException;

    DevUnit getProductById (Long productId) throws RequestFailedException;

    Boolean saveProduct(DevUnit product) throws RequestFailedException;

    boolean isNameUnique(String name, Long exceptId) throws RequestFailedException;
}
