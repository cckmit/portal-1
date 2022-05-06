package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommonManagerToNotifyList that = (CommonManagerToNotifyList) o;
        return Objects.equals(managerId, that.managerId) &&
                Objects.equals(notifyListId, that.notifyListId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(managerId, notifyListId);
    }

    @Override
    public String toString() {
        return "CommonManagerToNotifyList{" +
                "managerId=" + managerId +
                ", notifyListId=" + notifyListId +
                '}';
    }
}
