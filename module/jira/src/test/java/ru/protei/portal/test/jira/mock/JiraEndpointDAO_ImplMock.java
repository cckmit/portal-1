package ru.protei.portal.test.jira.mock;

import ru.protei.portal.core.model.dao.impl.JiraEnpointDAO_Impl;
import ru.protei.portal.core.model.ent.JiraEndpoint;

public class JiraEndpointDAO_ImplMock extends JiraEnpointDAO_Impl {

    @Override
    public JiraEndpoint getByProjectId(Long companyId, Long id) {
        JiraEndpoint endpoint = new JiraEndpoint();
        endpoint.setId(1L);
        endpoint.setCompanyId(companyId);
        endpoint.setProjectId(id.toString());
        endpoint.setPriorityMapId(1);
        endpoint.setStatusMapId(1);
        return endpoint;
    }
}
