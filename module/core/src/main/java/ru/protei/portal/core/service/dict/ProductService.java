package ru.protei.portal.core.service.dict;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.api.struct.HttpListResult;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.query.ProductQuery;

/**
 * Created by michael on 27.09.16.
 */
public interface ProductService {

    HttpListResult<DevUnit> list(ProductQuery query);

    CoreResponse<DevUnit> getProductById(Long id);

    CoreResponse<Long> createProduct(DevUnit product);

    CoreResponse<Boolean> updateProduct(DevUnit product);

}
