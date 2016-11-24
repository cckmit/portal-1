package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.helper.HelperFunc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by michael on 12.10.16.
 */
public class CompanyQuery extends BaseQuery {

    /**
     * request for companies that a members of group with id=groupId
     */
    public Long groupId;

    /**
     * list of company category (a partner, dealer, customer, etc)
     */
    public List<Long> categoryIds;

    public CompanyQuery() {
        super("", En_SortField.comp_name, En_SortDir.ASC);
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
}

