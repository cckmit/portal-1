package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.JiraEndpoint;

public interface JiraEndpointDAO extends PortalBaseDAO<JiraEndpoint> {

    JiraEndpoint getByProjectId(Long id);
}
