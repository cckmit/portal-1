package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CommonManagerToNotifyListDAO;
import ru.protei.portal.core.model.ent.CommonManagerToNotifyList;

import java.util.List;

import static ru.protei.portal.core.model.helper.HelperFunc.makeInArg;

public class CommonManagerToNotifyListDAO_Impl extends PortalBaseJdbcDAO<CommonManagerToNotifyList> implements CommonManagerToNotifyListDAO {

    @Override
    public CommonManagerToNotifyList getByManagerId(Long managerId) {
        return getByCondition("manager_id=?", managerId);
    }

    @Override
    public List<CommonManagerToNotifyList> getByManagersIds(List<Long> managersIds) {
        return getListByCondition("manager_id IN " + makeInArg(managersIds));
    }

    @Override
    public boolean updateNotifyList(CommonManagerToNotifyList commonManagerToNotifyList) {
        return mergeByCondition(commonManagerToNotifyList, "manager_id = ?", commonManagerToNotifyList.getManagerId());
    }
}
