package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_AuthType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

import java.util.List;
import java.util.Set;

/**
 * Фильтр по учетным записям
 */
public class AccountQuery extends BaseQuery {

    private Set< En_AuthType > types;

    /**
     * Выборка пользователь с конкретными ролями
     */
    private List<Long> roleIds;
    private Long companyId;

    public AccountQuery() {}

    public AccountQuery(Set<En_AuthType> types, List<Long> roleIds, String searchString, En_SortField sortField,
                        En_SortDir sortDir, Long companyId) {
        super ( searchString, sortField, sortDir );
        this.types = types;
        this.roleIds = roleIds;
        this.companyId = companyId;
    }

    public Set<En_AuthType> getTypes() {
        return types;
    }

    public void setTypes( Set<En_AuthType> types ) {
        this.types = types;
    }

    public List< Long > getRoleIds() {
        return roleIds;
    }

    public void setRoleIds( List< Long > roleIds ) {
        this.roleIds = roleIds;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    @Override
    public String toString() {
        return "AccountQuery{" +
                "types=" + types +
                ", roleIds=" + roleIds +
                ", companyId=" + companyId +
                ", searchString='" + searchString + '\'' +
                ", sortField=" + sortField +
                ", sortDir=" + sortDir +
                ", limit=" + limit +
                ", offset=" + offset +
                '}';
    }
}
