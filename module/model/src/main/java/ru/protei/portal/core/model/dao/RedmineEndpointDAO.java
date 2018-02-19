package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.RedmineEndpoint;

import java.util.Date;
import java.util.List;

public interface RedmineEndpointDAO extends PortalBaseDAO<RedmineEndpoint> {
    void updateCreatedOn(Long companyId, String projectId, Date date);

    void updateUpdatedOn(Long companyId, String projectId, Date date);

    List<RedmineEndpoint> getByCompanyId(Long companyId);

    RedmineEndpoint getByCompanyIdAndProjectId(Long companyId, String projectId);
}
