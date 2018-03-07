package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.RedmineEndpointDAO;
import ru.protei.portal.core.model.ent.RedmineEndpoint;

import java.util.Date;
import java.util.List;

public class RedmineEndpointDAO_Impl extends PortalBaseJdbcDAO<RedmineEndpoint> implements RedmineEndpointDAO {
    @Override
    public void updateCreatedOn(Long companyId, String projectId, Date date) {
        RedmineEndpoint endpoint = getByCondition("COMPANY_ID = ? AND project_id = ?", companyId, projectId);
        endpoint.setLastCreatedOnDate(date);
        saveOrUpdate(endpoint);
    }

    @Override
    public void updateUpdatedOn(Long companyId, String projectId, Date date) {
        RedmineEndpoint endpoint = getByCondition("COMPANY_ID = ? AND project_id = ?", companyId, projectId);
        endpoint.setLastUpdatedOnDate(date);
        saveOrUpdate(endpoint);
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
