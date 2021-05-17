package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.CompanyHomeGroupItem;

/**
 * Created by michael on 06.07.16.
 */
public interface CompanyGroupHomeDAO extends PortalBaseDAO<CompanyHomeGroupItem> {

    boolean checkIfHome (Long id);
    CompanyHomeGroupItem getByExternalCode(String externalCode);
    boolean isHomeCompany(Long companyId);
    boolean isSingleHomeCompany(Long companyId);
}
