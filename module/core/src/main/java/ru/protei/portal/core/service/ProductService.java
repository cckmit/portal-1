package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.query.ProductQuery;

import java.util.List;

/**
 * Created by michael on 27.09.16.
 */
public interface ProductService {

    CoreResponse<Long> count(ProductQuery query);

    CoreResponse<List<DevUnit>> list(ProductQuery query);

    CoreResponse<DevUnit> getProductById(Long id);

    CoreResponse<Long> createProduct(DevUnit product);

    CoreResponse<Boolean> updateProduct(DevUnit product);

    CoreResponse<Boolean> checkUniqueProductByName(String name, Long id);
}
