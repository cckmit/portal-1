package ru.protei.portal.core.model.query;


import java.util.List;

/**
 * Фильтр по ролям
 */
public class UserRoleQuery extends BaseQuery {

    /**
     * Выборка ролей по конкретным идентификаторам
     */
    private List<Long> roleIds;

    public List< Long > getRoleIds() {
        return roleIds;
    }

    public void setRoleIds( List< Long > roleIds ) {
        this.roleIds = roleIds;
    }
}
