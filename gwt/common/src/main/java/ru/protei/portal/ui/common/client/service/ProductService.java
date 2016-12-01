package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Сервис управления продуктами
 */
@RemoteServiceRelativePath( "springGwtServices/ProductService" )
public interface ProductService extends RemoteService {

    List<DevUnit> getProductList (ProductQuery query) throws RequestFailedException;

    DevUnit getProduct( Long productId ) throws RequestFailedException;

    Boolean saveProduct(DevUnit product) throws RequestFailedException;

    boolean isNameUnique(String name, Long exceptId) throws RequestFailedException;

    /**
     * Получение списка сокращенного представления продукта
     * @param query запрос
     * @return
     */
    List<ProductShortView> getProductViewList( ProductQuery query ) throws RequestFailedException;
}
