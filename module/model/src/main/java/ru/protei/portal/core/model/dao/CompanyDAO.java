package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.Company;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.List;

/**
 * Created by michael on 01.04.16.
 */
public interface CompanyDAO extends PortalBaseDAO<Company> {

    public List<Company> getList (String searchExpression, Long groupId, JdbcSort sort);
}
