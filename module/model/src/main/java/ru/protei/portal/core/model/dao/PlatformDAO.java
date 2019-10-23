package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.core.model.query.PlatformQuery;
import ru.protei.portal.core.model.query.SqlCondition;

public interface PlatformDAO extends PortalBaseDAO<Platform> {

    @SqlConditionBuilder
    SqlCondition createSqlCondition(PlatformQuery query);

    Platform getByProjectId (Long id);
}
