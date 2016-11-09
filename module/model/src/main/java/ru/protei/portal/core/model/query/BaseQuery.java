package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

import java.io.Serializable;

/**
 * Created by michael on 12.10.16.
 */
public class BaseQuery implements Serializable{

    public String searchString;
    public En_SortField sortField;
    public En_SortDir sortDir;
    public int limit;
    public int offset = 0;

    public BaseQuery() {
    }

    public BaseQuery(String searchString, En_SortField sortField, En_SortDir sortDir) {
        this.searchString = searchString;
        this.sortField = sortField;
        this.sortDir = sortDir;
    }

    public BaseQuery useSearch(String searchString) {
        this.searchString = searchString;
        return this;
    }

    public BaseQuery useSort (En_SortField sortField, En_SortDir sortDir) {
        this.sortDir = sortDir;
        this.sortField = sortField;
        return this;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public En_SortField getSortField() {
        return sortField;
    }

    public void setSortField(En_SortField sortField) {
        this.sortField = sortField;
    }

    public En_SortDir getSortDir() {
        return sortDir;
    }

    public void setSortDir(En_SortDir sortDir) {
        this.sortDir = sortDir;
    }
}
