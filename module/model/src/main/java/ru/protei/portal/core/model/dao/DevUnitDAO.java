package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.query.ProductDirectionQuery;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by michael on 23.05.16.
 */
public interface DevUnitDAO extends PortalBaseDAO<DevUnit> {

    DevUnit checkExistsByName(En_DevUnitType type, String name);
    DevUnit getByLegacyId (En_DevUnitType type, Long legacyId);
    boolean updateState(DevUnit newState);
    List<DevUnit> getParents(Long productId);
    List<DevUnit> getChildren(Set<Long> productId);
    List<DevUnit> getProductDirections(Long productId);

    List<DevUnit> getProjectDirections(Long projectId);
    List<DevUnit> getProjectProducts(Long projectId);
    List<DevUnit> getProjectProducts(List<Long> projectIds);

    @SqlConditionBuilder
    SqlCondition createProductSqlCondition(ProductQuery query);

    @SqlConditionBuilder
    SqlCondition createProductDirectionSqlCondition( ProductDirectionQuery query );


    Map<Long, Long> getProductOldToNewMap ();
}
