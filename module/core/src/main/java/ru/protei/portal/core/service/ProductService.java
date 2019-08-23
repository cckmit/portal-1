package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.query.ProductDirectionQuery;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

/**
 * Сервис управления продуктами
 */
public interface ProductService {

    @Privileged(En_Privilege.PRODUCT_VIEW)
    Result<SearchResult<DevUnit>> getProducts( AuthToken token, ProductQuery query);

    Result<List<ProductShortView>> shortViewList( AuthToken token, ProductQuery query );

    @Privileged( En_Privilege.PRODUCT_VIEW )
    Result<DevUnit> getProduct( AuthToken token, Long id );

    @Privileged( En_Privilege.PRODUCT_CREATE )
    @Auditable( En_AuditType.PRODUCT_CREATE )
    Result createProduct( AuthToken token, DevUnit product);

    @Privileged( En_Privilege.PRODUCT_EDIT )
    @Auditable( En_AuditType.PRODUCT_MODIFY )
    Result<Boolean> updateProduct( AuthToken token, DevUnit product );

    @Privileged( En_Privilege.PRODUCT_EDIT )
    @Auditable( En_AuditType.PRODUCT_MODIFY )
    Result<En_DevUnitState> updateState( AuthToken makeAuthToken, Long productId, En_DevUnitState state);

    @Privileged( En_Privilege.PRODUCT_VIEW )
    Result<Boolean> checkUniqueProductByName( AuthToken token, String name, Long id);

    Result<List<ProductDirectionInfo>> productDirectionList( AuthToken token, ProductDirectionQuery query );
}
