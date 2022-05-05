package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;

@JdbcEntity(table = "common_manager_to_notify_list")
public class CommonManagerToNotifyList {

    @JdbcColumn(name = "manager_id")
    private Long managerId;

    @JdbcColumn(name = "notify_list_id")
    private Long notifyListId;

    public CommonManagerToNotifyList() {
    }

    public CommonManagerToNotifyList(Long managerId, Long notifyListId) {
        this.managerId = managerId;
        this.notifyListId = notifyListId;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public Long getNotifyListId() {
        return notifyListId;
    }

    public void setNotifyListId(Long notifyListId) {
        this.notifyListId = notifyListId;
    }
}
