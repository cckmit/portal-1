package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.EducationWalletDAO;
import ru.protei.portal.core.model.ent.EducationWallet;

import java.util.List;

import static ru.protei.portal.core.model.helper.HelperFunc.makeInArg;

public class EducationWalletDAO_Impl extends PortalBaseJdbcDAO<EducationWallet> implements EducationWalletDAO {

    @Override
    public List<EducationWallet> getByDepartments(List<Long> depIdList) {
        return getListByCondition("dep_id IN " + makeInArg(depIdList, String::valueOf));
    }

    @Override
    public EducationWallet getByDepartment(Long depId) {
        return getByCondition("dep_id = ?", depId);
    }
}
