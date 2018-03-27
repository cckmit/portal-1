package ru.protei.portal.core.model.struct;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.protei.portal.core.model.dict.En_SortDir;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Один параметр фильтра обращений
 */
@JsonAutoDetect
public class IssueFilterParam implements Serializable {

    @JsonProperty("desc")
    private String description;

    @JsonProperty("createdFrom")
    private Date createdFrom;

    @JsonProperty("createdTo")
    private Date createdTo;

    @JsonProperty("sort")
    private En_SortDir sort;

    @JsonProperty("companies")
    private List<Long> companies;

    @JsonProperty("products")
    private List<Long> products;

    @JsonProperty("managers")
    private List<Long> managers;

    @JsonProperty("importances")
    private List<Integer> importances;

    @JsonProperty("states")
    private List<Integer> states;

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public Date getCreatedFrom() {
        return createdFrom;
    }

    public void setCreatedFrom( Date createdFrom ) {
        this.createdFrom = createdFrom;
    }

    public Date getCreatedTo() {
        return createdTo;
    }

    public void setCreatedTo( Date createdTo ) {
        this.createdTo = createdTo;
    }

    public En_SortDir getSort() {
        return sort;
    }

    public void setSort( En_SortDir sort ) {
        this.sort = sort;
    }

    public List< Long > getCompanies() {
        return companies;
    }

    public void setCompanies( List< Long > companies ) {
        this.companies = companies;
    }

    public List< Long > getProducts() {
        return products;
    }

    public void setProducts( List< Long > products ) {
        this.products = products;
    }

    public List< Long > getManagers() {
        return managers;
    }

    public void setManagers( List< Long > managers ) {
        this.managers = managers;
    }

    public List< Integer > getImportances() {
        return importances;
    }

    public void setImportances( List< Integer > importances ) {
        this.importances = importances;
    }

    public List< Integer > getStates() {
        return states;
    }

    public void setStates( List< Integer > states ) {
        this.states = states;
    }
}
