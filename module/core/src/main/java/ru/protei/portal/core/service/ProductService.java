package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.query.ProductDirectionQuery;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.view.ProductShortView;

import java.util.List;
import java.util.Set;

/**
 * Сервис управления продуктами
 */
public interface ProductService {

    CoreResponse<Long> count(ProductQuery query);
    CoreResponse<List<ProductShortView>> shortViewList( ProductQuery query );
    CoreResponse<List<DevUnit>> productList( ProductQuery query, Set< UserRole > roles );
    CoreResponse<DevUnit> getProduct( Long id, Set< UserRole > roles );
    CoreResponse<Long> createProduct(DevUnit product);
    CoreResponse<Boolean> updateProduct( DevUnit product, Set< UserRole > roles );
    CoreResponse<Boolean> checkUniqueProductByName(String name, Long id);
    CoreResponse<List<ProductDirectionInfo>> productDirectionList( ProductDirectionQuery query );
}
