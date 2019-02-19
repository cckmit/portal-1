package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.JiraEndpointDAO;
import ru.protei.portal.core.model.ent.JiraEndpoint;

public class JiraEnpointDAO_Impl extends PortalBaseJdbcDAO<JiraEndpoint> implements JiraEndpointDAO {
    @Override
    public Long getCompanyUser(Long id) {
        return null;
    }

    @Override
    public JiraEndpoint getByProjectId(Long id) {
        return null;
    }
}
