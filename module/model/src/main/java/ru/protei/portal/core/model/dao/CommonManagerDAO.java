package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.CommonManager;

import java.util.List;

public interface CommonManagerDAO extends PortalBaseDAO<CommonManager> {
    void removeByProduct(Long productId);
    CommonManager getByProduct(Long productId);

    void removeByCompany(Long companyId);
    List<Long> getIdsByCompany(Long companyId);
}
