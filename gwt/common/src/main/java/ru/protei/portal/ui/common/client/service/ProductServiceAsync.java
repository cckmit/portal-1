package ru.protei.portal.ui.common.client.service;


import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.view.ProductShortView;

import java.util.List;

/**
 * Асинхронный сервис управления продуктами
 */
public interface ProductServiceAsync {

    void getProductList(ProductQuery query, AsyncCallback<List<DevUnit>> async);

    void getProduct( Long productId, AsyncCallback<DevUnit> async );

    void saveProduct(DevUnit product, AsyncCallback<Boolean> async);

    void isNameUnique(String name, Long exceptId, AsyncCallback<Boolean> async);

    /**
     * Получение списка сокращенного представления продукта
     * @param query запрос
     * @param callback
     */
    void getProductViewList( ProductQuery query, AsyncCallback< List<ProductShortView> > callback );
}
