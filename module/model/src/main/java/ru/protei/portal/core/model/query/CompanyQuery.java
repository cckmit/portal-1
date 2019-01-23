package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

import java.util.List;

/**
 * Фильтр по компаниям
 */
public class CompanyQuery extends BaseQuery {

    /**
     * request for companies that a members of group with id=groupId
     */
    private Long groupId;

    /**
     * list of company category (a partner, dealer, customer, etc)
     */
    private List<Long> categoryIds;

    private boolean onlyHome;

    private boolean parentIdIsNull;

    public CompanyQuery() {
        super("", En_SortField.comp_name, En_SortDir.ASC);
    }

    public CompanyQuery(boolean onlyHome) {
        super("", En_SortField.comp_name, En_SortDir.ASC);
        this.onlyHome = onlyHome;
    }

    public CompanyQuery(String searchString, En_SortField sortField, En_SortDir sortDir) {
        super(searchString, sortField, sortDir);
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public List<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    };

    public boolean getOnlyHome() {
        return onlyHome;
    }

    public void setOnlyHome(boolean onlyHome) {
        this.onlyHome = onlyHome;
    }

    public void setParentIdIsNull( boolean parentIdIsNull ) {
        this.parentIdIsNull = parentIdIsNull;
    }

    public boolean isParentIdIsNull() {
        return parentIdIsNull;
    }
}

