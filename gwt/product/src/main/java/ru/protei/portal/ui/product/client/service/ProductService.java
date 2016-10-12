package ru.protei.portal.ui.product.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Сервис управления продуктами
 */
@RemoteServiceRelativePath( "springGwtServices/ProductService" )
public interface ProductService extends RemoteService {

    List<DevUnit> getProductList(String param, boolean showDepricated ) throws RequestFailedException;

    DevUnit getProductById (Long productId) throws RequestFailedException;

}
