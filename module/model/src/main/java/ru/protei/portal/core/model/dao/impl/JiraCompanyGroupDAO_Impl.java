package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.JiraCompanyGroupDAO;
import ru.protei.portal.core.model.ent.JiraCompanyGroup;

public class JiraCompanyGroupDAO_Impl extends PortalBaseJdbcDAO<JiraCompanyGroup> implements JiraCompanyGroupDAO {
    @Override
    public JiraCompanyGroup getByName(String name) {
        return getByCondition(JiraCompanyGroup.Columns.JIRA_COMPANY_NAME+ "=?", name);
    }

}
