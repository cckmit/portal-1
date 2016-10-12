package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.query.CompanyQuery;

import java.util.List;

/**
 * Created by michael on 01.04.16.
 */
public interface CompanyDAO extends PortalBaseDAO<Company> {

    public List<Company> getListByQuery (CompanyQuery query);
}
