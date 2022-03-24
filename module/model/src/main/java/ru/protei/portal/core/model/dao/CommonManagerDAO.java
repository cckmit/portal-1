package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.CommonManager;

public interface CommonManagerDAO extends PortalBaseDAO<CommonManager> {
    void removeByProduct(Long productId);
    CommonManager getByProduct(Long productId);
}
