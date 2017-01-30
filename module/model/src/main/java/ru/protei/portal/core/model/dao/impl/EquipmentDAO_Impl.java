package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.EquipmentDAO;
import ru.protei.portal.core.model.dao.impl.PortalBaseJdbcDAO;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.query.EquipmentQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DAO оборудования
 */
public class EquipmentDAO_Impl extends PortalBaseJdbcDAO<Equipment> implements EquipmentDAO {

    @Override
    public List<Equipment> getListByQuery(EquipmentQuery query) {
        return listByQuery(query);
    }

    // TODO: make query filter
    @SqlConditionBuilder
    public SqlCondition createSqlCondition(EquipmentQuery query) {
        return new SqlCondition().build((condition, args) -> {
        });
    }

}
