package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.RedmineEndpointDAO;
import ru.protei.portal.core.model.ent.RedmineEndpoint;

import java.util.List;

public class RedmineEndpointDAO_Impl extends PortalBaseJdbcDAO<RedmineEndpoint> implements RedmineEndpointDAO {
    @Override
    public boolean updateCreatedOn( RedmineEndpoint endpoint) {
        return partialMerge(endpoint, "last_created");
    }

    @Override
    public boolean updateUpdatedOn(RedmineEndpoint endpoint) {
        return partialMerge(endpoint, "last_updated");
    }

    @Override
    public List<RedmineEndpoint> getByCompanyId(Long companyId) {
        return getListByCondition("COMPANY_ID=?", companyId);
    }

    @Override
    public RedmineEndpoint getByCompanyIdAndProjectId(Long companyId, String projectId) {
        return getByCondition("COMPANY_ID=? AND project_id=?", companyId, projectId);
    }
}
