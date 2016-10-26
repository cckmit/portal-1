package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.CompanyGroupItem;

import java.util.List;

/**
 * Created by michael on 01.04.16.
 */
public interface CompanyGroupItemDAO extends PortalBaseDAO<CompanyGroupItem> {

    List<CompanyGroupItem> getCompanyToGroupLinks(Long companyId, Long groupId);
}
