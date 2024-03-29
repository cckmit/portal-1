package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.JiraEndpointDAO;
import ru.protei.portal.core.model.ent.JiraEndpoint;

public class JiraEnpointDAO_Impl extends PortalBaseJdbcDAO<JiraEndpoint> implements JiraEndpointDAO {

    @Override
    public JiraEndpoint getByProjectId(Long companyId, Long id) {
        return getByCondition("company_id=? and project_id=?", companyId, id);
    }
}
