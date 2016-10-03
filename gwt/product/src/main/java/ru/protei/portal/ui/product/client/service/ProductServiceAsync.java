package ru.protei.portal.ui.product.client.service;


import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.Product;

import java.util.List;

/**
 * Асинхронный сервис управления продуктами
 */
public interface ProductServiceAsync {

    void getProductList(String name, AsyncCallback<List<Product>> async);

    void getProductById(Long productId, AsyncCallback<Product> async);
}
