package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mike on 02.11.2016.
 */
public class ContactQuery extends BaseQuery {

    private Long companyId;
    private Boolean fired;

    public ContactQuery() {
        fired = false;
    }

    public ContactQuery(EntityOption company, Boolean fired, String searchString, En_SortField sortField, En_SortDir sortDir) {
        this (company == null ? null : company.getId(), fired, searchString, sortField, sortDir);
    }

    public ContactQuery(Long companyId, Boolean fired, String searchString, En_SortField sortField, En_SortDir sortDir) {
        super(searchString, sortField, sortDir);
        this.companyId = companyId;
        this.fired = fired;
        this.limit = 1000;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Boolean getFired() {
        return fired;
    }

    public void setFired(Boolean fired) {
        this.fired = fired;
    }

    @Override
    public SqlCondition sqlCondition() {
        return new SqlCondition().build((condition, args) -> {
            condition.append("Person.company_id not in (select companyId from company_group_home)");

            if (companyId != null) {
                condition.append(" and Person.company_id = ?");
                args.add(companyId);
            }

            if (fired != null) {
                condition.append(" and Person.isfired=?");
                args.add(fired ? 1 : 0);
            }

            if (HelperFunc.isLikeRequired(searchString)) {
                condition.append(" and (Person.displayName like ? or Person.contactInfo like ?)");
                String likeArg = HelperFunc.makeLikeArg(searchString, true);

                args.add(likeArg);
                args.add(likeArg);
            }
        });
    }
}
