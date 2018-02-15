package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.RedmineEndpoint;

import java.sql.Date;
import java.util.List;

public interface RedmineEndpointDAO extends PortalBaseDAO<RedmineEndpoint> {
    void updateCreatedOn(Long companyId, String projectName, Date date);

    void updateUpdatedOn(Long companyId, String projectName, Date date);

    List<RedmineEndpoint> getByCompanyId(Long companyId);

    RedmineEndpoint getByCompanyIdAndProjectName(Long companyId, String projectName);
}
