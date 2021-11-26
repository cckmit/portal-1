package ru.protei.portal.core.model.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.struct.Pair;

import java.util.List;

/**
 * Created by michael on 22.11.16.
 */
public interface DataQuery {

    @JsonIgnore
    DataQuery useSort (En_SortField sortField, En_SortDir sortDir);

    List<Pair<En_SortField, En_SortDir>> getSortParameters();

    int getLimit();

    void setLimit(int limit);

    int getOffset();

    void setOffset(int offset);

    En_SortField getSortField();

    void setSortField(En_SortField sortField);

    En_SortDir getSortDir();

    void setSortDir(En_SortDir sortDir);
}
