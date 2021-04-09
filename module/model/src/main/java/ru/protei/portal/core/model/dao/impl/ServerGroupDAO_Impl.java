package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.ServerGroupDAO;
import ru.protei.portal.core.model.ent.ServerGroup;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.List;

public class ServerGroupDAO_Impl extends PortalBaseJdbcDAO<ServerGroup> implements ServerGroupDAO {
    @Override
    public List<ServerGroup> getListByPlatformId(Long platformId, int limit, int offset) {
        JdbcQueryParameters jdbcQueryParameters = new JdbcQueryParameters()
                .withCondition("server_group.platform_id = ?", platformId)
                .withLimit(limit)
                .withOffset(offset);

        return getList(jdbcQueryParameters);
    }
}
