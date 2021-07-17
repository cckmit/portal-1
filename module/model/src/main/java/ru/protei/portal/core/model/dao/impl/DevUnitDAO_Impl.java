package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.DevUnitDAO;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.ProductDirectionQuery;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.util.sqlcondition.Condition;
import ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder;
import ru.protei.winter.jdbc.JdbcHelper;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.*;

import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;
import static ru.protei.winter.core.utils.collections.CollectionUtils.isNotEmpty;

/**
 * Created by michael on 23.05.16.
 */
public class DevUnitDAO_Impl extends PortalBaseJdbcDAO<DevUnit> implements DevUnitDAO {

    @Override
    public DevUnit checkExistsByName(En_DevUnitType type, String name) {
        return getByCondition("UTYPE_ID=? and UNIT_NAME=?", type.getId(), name);
    }

    @Override
    public DevUnit getByLegacyId(En_DevUnitType type, Long legacyId) {
        return getByCondition("UTYPE_ID=? and OLD_ID=?", type.getId(), legacyId);
    }

    @Override
    public boolean updateState(DevUnit newState) {
        return partialMerge(newState, "UNIT_STATE");
    }

    @Override
    public List<DevUnit> getParents(Long productId) {
        return getListByCondition("dev_unit.ID IN (SELECT DUNIT_ID FROM dev_unit_children WHERE CHILD_ID = ?) AND UTYPE_ID != ?",
                productId,
                En_DevUnitType.DIRECTION.getId()
        );
    }

    @Override
    public List<DevUnit> getChildren(Set<Long> productIds) {
        return getListByCondition("dev_unit.ID IN (SELECT dev_unit_children.CHILD_ID FROM dev_unit_children WHERE dev_unit_children.DUNIT_ID IN " + HelperFunc.makeInArg(productIds, false) + ")");
    }

    @Override
    public List<DevUnit> getProductDirections(Long productId) {
        return getListByCondition("dev_unit.ID IN (SELECT DUNIT_ID FROM dev_unit_children WHERE CHILD_ID = ?) AND UTYPE_ID = ?",
                productId,
                En_DevUnitType.DIRECTION.getId()
        );
    }

    @Override
    public List<DevUnit> getProjectDirections(Long projectId) {
        return getListByCondition("dev_unit.ID IN (SELECT product_id FROM project_to_product WHERE project_id = ?) AND UTYPE_ID = ?",
                projectId,
                En_DevUnitType.DIRECTION.getId()
        );
    }

    @Override
    public List<DevUnit> getProjectProducts(Long projectId) {
        return getListByCondition("dev_unit.ID IN (SELECT product_id FROM project_to_product WHERE project_id = ?) AND UTYPE_ID != ?",
                projectId,
                En_DevUnitType.DIRECTION.getId()
        );
    }

    @Override
    public List<DevUnit> getProjectProducts(List<Long> projectIds) {
        if (CollectionUtils.isEmpty(projectIds)) return Collections.emptyList();
        return getListByCondition("dev_unit.ID IN (SELECT product_id FROM project_to_product WHERE project_id IN " + HelperFunc.makeInArg(projectIds) + ") AND UTYPE_ID != ?",
                En_DevUnitType.DIRECTION.getId()
        );
    }

    @Override
    public Map<Long, Long> getProductOldToNewMap() {
        Map<Long,Long> result = new HashMap<>();
        getListByCondition("UTYPE_ID=? and old_id is not null", En_DevUnitType.PRODUCT.getId())
                .forEach(unit -> result.put(unit.getOldId(), unit.getId()));
        return result;
    }

    @SqlConditionBuilder
    public SqlCondition createProductSqlCondition(ProductQuery query) {
        Condition condition = SqlQueryBuilder.condition()
                .and( SqlQueryBuilder.condition()
                        .or( "UNIT_NAME" ).like( query.getSearchString() )
                        .or( "ALIASES" ).like( query.getSearchString() )
                        .or( "UNIT_NAME" ).like( query.getAlternativeSearchString() )
                        .or( "ALIASES" ).like( query.getAlternativeSearchString() )
                )
                .and( "UNIT_STATE" ).equal( query.getState() == null ? null : query.getState().getId() )
                .and( "UTYPE_ID" ).in( collectIds( query.getTypes() ) );

        if (isEmpty( query.getTypes() )) {
            condition.and( "UTYPE_ID" ).not().equal( En_DevUnitType.DIRECTION.getId() );
        }

        if (isNotEmpty(query.getDirectionIds())) {
            condition.condition( " and dev_unit.ID IN (SELECT CHILD_ID FROM dev_unit_children WHERE DUNIT_ID IN "
                    + HelperFunc.makeInArg( query.getDirectionIds(), false )
                    + ")" );
        }

        if (isNotEmpty( query.getPlatformIds() )) {
            condition.condition(
                    " and dev_unit.ID IN " +
                            "(SELECT project_to_product.product_id FROM project_to_product WHERE project_to_product.project_id IN " +
                            "(SELECT platform.project_id FROM platform WHERE platform.id IN " + HelperFunc.makeInArg( query.getPlatformIds(), false ) + ")" +
                            ")"
            );
        }

        return new SqlCondition( condition.getSqlCondition(), condition.getSqlParameters() );
    }

    @SqlConditionBuilder
    public SqlCondition createProductDirectionSqlCondition( ProductDirectionQuery query ) {
        return new SqlCondition().build( (condition, args)->{
            condition.append( "UTYPE_ID=?" );
            args.add( En_DevUnitType.DIRECTION.getId() );
        } );
    }

}
