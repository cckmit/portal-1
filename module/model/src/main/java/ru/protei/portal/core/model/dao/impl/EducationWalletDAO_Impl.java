package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.EducationWalletDAO;
import ru.protei.portal.core.model.ent.EducationWallet;

import java.util.List;

import static ru.protei.portal.core.model.helper.HelperFunc.makeInArg;

public class EducationWalletDAO_Impl extends PortalBaseJdbcDAO<EducationWallet> implements EducationWalletDAO {

    @Override
    public List<EducationWallet> getByWorkers(List<Long> workerIdList) {
        return getListByCondition("dep_id IN (SELECT dep_id FROM worker_entry WHERE id IN " + makeInArg(workerIdList, String::valueOf) + ")");
    }

    @Override
    public EducationWallet getByWorker(Long workerId) {
        return getByCondition("dep_id = (SELECT dep_id FROM worker_entry WHERE id = ?)", workerId);
    }

    @Override
    public EducationWallet getByDepartment(Long depId) {
        return getByCondition("dep_id = ?", depId);
    }
}
