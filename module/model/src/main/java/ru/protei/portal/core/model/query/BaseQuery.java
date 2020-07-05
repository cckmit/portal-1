package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.helper.StringUtils;

import java.io.Serializable;

/**
 * Created by michael on 12.10.16.
 */
public class BaseQuery implements Serializable, DataQuery {

    public String searchString;

    private String alternativeSearchString;

    public En_SortField sortField;

    public En_SortDir sortDir;

    public int limit;

    public int offset = 0;

    public BaseQuery() {}

    public BaseQuery(String searchString, En_SortField sortField, En_SortDir sortDir) {
        this.searchString = searchString;
        this.sortField = sortField;
        this.sortDir = sortDir;
    }

    public BaseQuery useSearch(String searchString) {
        this.searchString = searchString;
        return this;
    }

    @Override
    public BaseQuery useSort(En_SortField sortField, En_SortDir sortDir) {
        this.sortDir = sortDir;
        this.sortField = sortField;
        return this;
    }

    @Override
    public int getLimit() {
        return limit;
    }

    @Override
    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public void setAlternativeSearchString( String searchString) {
        this.alternativeSearchString = searchString;
    }

    public String getAlternativeSearchString() {
        return alternativeSearchString;
    }

    @Override
    public En_SortField getSortField() {
        return sortField;
    }

    @Override
    public void setSortField(En_SortField sortField) {
        this.sortField = sortField;
    }

    @Override
    public En_SortDir getSortDir() {
        return sortDir;
    }

    @Override
    public void setSortDir(En_SortDir sortDir) {
        this.sortDir = sortDir;
    }

    @Override
    public String toString() {
        return "BaseQuery{" +
                "searchString='" + searchString + '\'' +
                ", alternativeSearchString=" + alternativeSearchString +
                ", sortField=" + sortField +
                ", sortDir=" + sortDir +
                ", limit=" + limit +
                ", offset=" + offset +
                '}';
    }

    public boolean isParamsPresent() {
        return StringUtils.isNotBlank(searchString);
    }

}
