package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.core.model.query.PlatformQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import java.util.List;

public interface PlatformDAO extends PortalBaseDAO<Platform> {

    void removeByProjectId(Long projectId);

    @SqlConditionBuilder
    SqlCondition createSqlCondition(PlatformQuery query);

    List<Platform> getByProjectId (Long id);
}
