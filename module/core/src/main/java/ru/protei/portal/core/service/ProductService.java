package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dto.DevUnitInfo;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.query.ProductDirectionQuery;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.dto.ProductDirectionInfo;
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

    Result<List<ProductShortView>> productsShortViewListWithChildren(AuthToken token, ProductQuery query);

    Result<List<ProductShortView>> shortViewListByIds(AuthToken token, List<Long> ids );

    @Privileged( En_Privilege.PRODUCT_VIEW )
    Result<DevUnit> getProduct( AuthToken token, Long id );

    @Privileged( En_Privilege.PRODUCT_CREATE )
    @Auditable( En_AuditType.PRODUCT_CREATE )
    Result<DevUnit> createProduct( AuthToken token, DevUnit product);

    @Privileged(En_Privilege.PRODUCT_CREATE)
    @Auditable(En_AuditType.PRODUCT_CREATE)
    Result<DevUnitInfo> createProductByInfo(AuthToken token, DevUnitInfo product);

    @Privileged( En_Privilege.PRODUCT_EDIT )
    @Auditable( En_AuditType.PRODUCT_MODIFY )
    Result<DevUnit> updateProduct( AuthToken token, DevUnit product );

    @Privileged( En_Privilege.PRODUCT_EDIT )
    @Auditable( En_AuditType.PRODUCT_MODIFY )
    Result<En_DevUnitState> updateState( AuthToken makeAuthToken, Long productId, En_DevUnitState state);

    @Privileged( En_Privilege.PRODUCT_VIEW )
    Result<Boolean> checkUniqueProductByName(AuthToken token, String name, En_DevUnitType type, Long id);

    Result<List<ProductDirectionInfo>> productDirectionList( AuthToken token, ProductDirectionQuery query );

    Result<List<ProductDirectionInfo>> productDirectionList( AuthToken token, List<Long> productDirectionIds );

    @Privileged( En_Privilege.PRODUCT_VIEW )
    Result<DevUnitInfo> getProductInfo( AuthToken authToken, Long productId );

    @Privileged( En_Privilege.PRODUCT_EDIT )
    @Auditable( En_AuditType.PRODUCT_MODIFY )
    Result<Long> updateProductFromInfo( AuthToken authToken, DevUnitInfo product );

    @Privileged( En_Privilege.PRODUCT_VIEW )
    Result<List<DevUnitInfo>> getProductsBySelfCompanyProjects(AuthToken authToken);
}
