package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.JiraEndpoint;

public interface JiraEndpointDAO extends PortalBaseDAO<JiraEndpoint> {
    Long getCompanyUser(Long id);

    JiraEndpoint getByProjectId(Long id);
}
