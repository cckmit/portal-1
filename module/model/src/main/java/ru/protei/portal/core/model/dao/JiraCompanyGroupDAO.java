package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.JiraCompanyGroup;

public interface JiraCompanyGroupDAO extends PortalBaseDAO<JiraCompanyGroup> {
    JiraCompanyGroup getByName(String name);
}
