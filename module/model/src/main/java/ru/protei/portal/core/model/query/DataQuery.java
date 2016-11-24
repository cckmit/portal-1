package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

/**
 * Created by michael on 22.11.16.
 */
public interface DataQuery {

    DataQuery useSort (En_SortField sortField, En_SortDir sortDir);

    int getLimit();

    void setLimit(int limit);

    int getOffset();

    void setOffset(int offset);

    En_SortField getSortField();

    void setSortField(En_SortField sortField);

    En_SortDir getSortDir();

    void setSortDir(En_SortDir sortDir);
}
