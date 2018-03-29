package ru.protei.portal.core.model.query;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

import java.io.Serializable;

/**
 * Created by michael on 12.10.16.
 */
@JsonAutoDetect
public class BaseQuery implements Serializable, DataQuery {

    @JsonProperty("search_string")
    public String searchString;

    @JsonProperty("sort_field")
    public En_SortField sortField;

    @JsonProperty("sort_dir")
    public En_SortDir sortDir;

    @JsonIgnore
    public int limit;

    @JsonIgnore
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

    @Override
    public BaseQuery useSort(En_SortField sortField, En_SortDir sortDir) {
        this.sortDir = sortDir;
        this.sortField = sortField;
        return this;
    }

    @Override
    @JsonIgnore
    public int getLimit() {
        return limit;
    }

    @Override
    @JsonIgnore
    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    @JsonIgnore
    public int getOffset() {
        return offset;
    }

    @Override
    @JsonIgnore
    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    @Override
    @JsonValue
    public En_SortField getSortField() {
        return sortField;
    }

    @Override
    @JsonValue
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
                ", sortField=" + sortField +
                ", sortDir=" + sortDir +
                ", limit=" + limit +
                ", offset=" + offset +
                '}';
    }
}
