package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.CompanyGroup;

/**
 * Created by michael on 01.04.16.
 */
public interface CompanyGroupDAO extends PortalBaseDAO<CompanyGroup> {

    boolean checkExistsGroupByName (String name, Long id);
}
