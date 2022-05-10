package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.CommonManagerToNotifyList;

import java.util.List;

public interface CommonManagerToNotifyListDAO extends PortalBaseDAO<CommonManagerToNotifyList> {

    CommonManagerToNotifyList getByManagerId(Long managerId);

    List<CommonManagerToNotifyList> getByManagersIds(List<Long> managersIds);

    boolean updateNotifyList(CommonManagerToNotifyList commonManagerToNotifyList);
}
