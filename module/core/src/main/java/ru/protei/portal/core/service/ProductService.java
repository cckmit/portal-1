package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.ProductShortView;

import java.util.List;

/**
 * Сервис управления продуктами
 */
public interface ProductService {

    CoreResponse<Long> count(ProductQuery query);
    CoreResponse<List<ProductShortView>> shortViewList(ProductQuery query);
    CoreResponse<List<DevUnit>> productList(ProductQuery query);
    CoreResponse<DevUnit> getProduct(Long id);
    CoreResponse<Long> createProduct(DevUnit product);
    CoreResponse<Boolean> updateProduct(DevUnit product);
    CoreResponse<Boolean> checkUniqueProductByName(String name, Long id);
}
