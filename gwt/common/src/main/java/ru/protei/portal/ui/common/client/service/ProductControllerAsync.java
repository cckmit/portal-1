package ru.protei.portal.ui.common.client.service;


import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.query.ProductDirectionQuery;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

/**
 * Асинхронный сервис управления продуктами
 */
public interface ProductControllerAsync {

    void getProductList(ProductQuery query, AsyncCallback<SearchResult<DevUnit>> async);

    void getProduct( Long productId, AsyncCallback<DevUnit> async );

    void saveProduct(DevUnit product, AsyncCallback<Boolean> async);

    void updateState(Long productId, En_DevUnitState state, AsyncCallback<Boolean> async);

    /**
     * Получение списка сокращенного представления продукта
     * @param query запрос
     * @param callback
     */
    void getProductViewList( ProductQuery query, AsyncCallback< List<ProductShortView> > callback );

    /**
     * Получение списка продуктовых направлений
     * @param query
     * @param callback
     */
    void getProductDirectionList( ProductDirectionQuery query, AsyncCallback<List<ProductDirectionInfo>> callback );

    void isNameUnique(String name, En_DevUnitType type, Long exceptId, AsyncCallback<Boolean> async);
}
