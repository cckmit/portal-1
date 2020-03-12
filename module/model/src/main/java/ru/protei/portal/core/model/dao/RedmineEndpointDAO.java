package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.RedmineEndpoint;

import java.util.List;

public interface RedmineEndpointDAO extends PortalBaseDAO<RedmineEndpoint> {
    boolean updateCreatedOn( RedmineEndpoint endpoint);

    boolean updateUpdatedOn(RedmineEndpoint endpoint);

    List<RedmineEndpoint> getByCompanyId(Long companyId);

    RedmineEndpoint getByCompanyIdAndProjectId(Long companyId, String projectId);
}
