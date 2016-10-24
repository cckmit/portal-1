package ru.protei.portal.ui.product.client.service;


import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.DevUnit;

import java.util.List;

/**
 * Асинхронный сервис управления продуктами
 */
public interface ProductServiceAsync {

    void getProductList(String param, En_DevUnitState state, En_SortField sortField, Boolean sortDir, AsyncCallback<List<DevUnit>> async);

    void getProductById(Long productId, AsyncCallback<DevUnit> async);

    void saveProduct(DevUnit product, AsyncCallback<Boolean> async);
}
