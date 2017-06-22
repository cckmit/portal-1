package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.EntityOption;

/**
 * Created by turik on 08.06.17.
 */
public class PersonQuery extends BaseQuery {
    private Long companyId;

    public PersonQuery() {
        super( "", En_SortField.person_full_name, En_SortDir.ASC );
    }

    public PersonQuery( EntityOption company, String searchString, En_SortField sortField, En_SortDir sortDir ) {
        this ( company == null ? null : company.getId(), searchString, sortField, sortDir );
    }

    public PersonQuery( Long companyId, String searchString, En_SortField sortField, En_SortDir sortDir ) {
        super(searchString, sortField, sortDir);
        this.companyId = companyId;
        this.limit = 1000;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId( Long companyId ) {
        this.companyId = companyId;
    }

    @Override
    public String toString() {
        return "PersonQuery{" +
                "companyId=" + companyId +
                '}';
    }
}
