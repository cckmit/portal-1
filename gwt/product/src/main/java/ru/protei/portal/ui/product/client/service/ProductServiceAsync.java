package ru.protei.portal.ui.product.client.service;


import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.view.ProductView;

import java.util.List;

/**
 * Асинхронный сервис управления продуктами
 */
public interface ProductServiceAsync {

    void getProductList(String param, boolean showDepricated, String sortField, String sortDir, AsyncCallback<List<ProductView>> async);

    void getProductById(Long productId, AsyncCallback<ProductView> async);
}
