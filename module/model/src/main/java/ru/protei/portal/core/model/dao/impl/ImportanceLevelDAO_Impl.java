package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.ImportanceLevelDAO;
import ru.protei.portal.core.model.ent.ImportanceLevel;
import ru.protei.portal.core.model.util.sqlcondition.Condition;
import ru.protei.winter.jdbc.JdbcBaseDAO;
import ru.protei.winter.jdbc.JdbcQueryParameters;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.List;

import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.condition;

public class ImportanceLevelDAO_Impl extends JdbcBaseDAO<Integer, ImportanceLevel> implements ImportanceLevelDAO {
    @Override
    public List<ImportanceLevel> getImportanceLevelsByCompanyId(Long companyId) {
        Condition queryCondition = condition().and("cii.company_id").equal(companyId);

        JdbcQueryParameters jdbcQueryParameters = new JdbcQueryParameters()
                .withCondition(queryCondition.getSqlCondition(), queryCondition.getSqlParameters())
                .withJoins("LEFT OUTER JOIN company_importance_item cii on importance_level.id = cii.importance_level_id")
                .withSort(new JdbcSort(JdbcSort.Direction.ASC, "cii.order_number"));

        return getList(jdbcQueryParameters);
    }
}
