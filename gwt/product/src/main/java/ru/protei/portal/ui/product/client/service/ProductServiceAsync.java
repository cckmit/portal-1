package ru.protei.portal.ui.product.client.service;


import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.DevUnit;

import java.util.List;

/**
 * Асинхронный сервис управления продуктами
 */
public interface ProductServiceAsync {

    void getProductList(String param, Boolean state, En_SortField sortField, Boolean sortDir, AsyncCallback<List<DevUnit>> async);

    void getProductById(Long productId, AsyncCallback<DevUnit> async);

    void saveProduct (Long productId, String name, String info, Boolean state, AsyncCallback<DevUnit> async);
}
