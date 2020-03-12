package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.EducationWallet;

import java.util.List;

public interface EducationWalletDAO extends PortalBaseDAO<EducationWallet> {
    List<EducationWallet> getAll();
}
