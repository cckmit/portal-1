package ru.protei.portal.core.model.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.helper.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Запрос по регионам
 */
public class ProjectQuery extends BaseQuery {
    @JsonIgnore
    private List<Long> caseNumbers;

    private Set<En_RegionState> states;

    private Set<Long> districtIds;

    private Long directionId;

    private Boolean onlyMineProjects;

    private Set<Long> productIds;

    private En_CustomerType customerType;

    private Date createdFrom;

    private Date createdTo;

    private Boolean contractIndependentProject;

    private Boolean platformIndependentProject;

    public ProjectQuery() {
        sortField = En_SortField.case_name;
        sortDir = En_SortDir.ASC;
    }

    public ProjectQuery( String searchString, En_SortField sortField, En_SortDir sortDir ) {
        super(searchString, sortField, sortDir);
    }

    public ProjectQuery( Set<En_RegionState> state, String searchString, En_SortField sortField, En_SortDir sortDir ) {
        super(searchString, sortField, sortDir);
        this.states = state;
    }

    public ProjectQuery(Date createdFrom, Date createdTo, Set<Long> productIds, String searchString, En_SortField sortField, En_SortDir sortDir) {
        super(searchString, sortField, sortDir);
        this.createdFrom = createdFrom;
        this.createdTo = createdTo;
        this.productIds = productIds;
    }

    public List<Long> getCaseNumbers() {
        return caseNumbers;
    }

    public void setCaseNo( Long caseNo ) {
        this.caseNumbers = new ArrayList<>();
        this.caseNumbers.add(caseNo);
    }

    public void setCaseNumbers( List<Long> caseNumbers ) {
        this.caseNumbers = caseNumbers;
    }

    public Set<En_RegionState> getStates() {
        return states;
    }

    public void setStates(Set<En_RegionState> state) {
        this.states = state;
    }

    public Set<Long> getDistrictIds() {
        return districtIds;
    }

    public void setDistrictIds( Set<Long> districtIds ) {
        this.districtIds = districtIds;
    }

    public Long getDirectionId() {
        return directionId;
    }

    public void setDirectionId( Long directionId ) {
        this.directionId = directionId;
    }

    public Boolean isOnlyMineProjects() {
        return onlyMineProjects;
    }

    public void setOnlyMineProjects(Boolean onlyMineProjects) {
        this.onlyMineProjects = onlyMineProjects;
    }

    public Set<Long> getProductIds() {
        return productIds;
    }

    public void setProductIds(Set<Long> productIds) {
        this.productIds = productIds;
    }

    public En_CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType( En_CustomerType customerType ) {
        this.customerType = customerType;
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

    public Boolean getContractIndependentProject() {
        return contractIndependentProject;
    }

    public void setContractIndependentProject(Boolean contractIndependentProject) {
        this.contractIndependentProject = contractIndependentProject;
    }

    public Boolean getPlatformIndependentProject() {
        return platformIndependentProject;
    }

    public void setPlatformIndependentProject(Boolean platformIndependentProject) {
        this.platformIndependentProject = platformIndependentProject;
    }

    @Override
    public boolean isParamsPresent() {
        return super.isParamsPresent() ||
                CollectionUtils.isNotEmpty(caseNumbers) ||
                CollectionUtils.isNotEmpty(states) ||
                directionId != null ||
                CollectionUtils.isNotEmpty(productIds) ||
                customerType != null ||
                createdFrom != null ||
                createdTo != null ||
                contractIndependentProject != null ||
                platformIndependentProject != null;
    }

    @Override
    public String toString() {
        return "ProjectQuery{" +
                "states=" + states +
                ", caseNumbers=" + caseNumbers +
                ", districtIds=" + districtIds +
                ", directionId=" + directionId +
                ", onlyMineProjects=" + onlyMineProjects +
                ", productIds=" + productIds +
                ", customerType=" + customerType +
                ", createdFrom=" + createdFrom +
                ", createdTo=" + createdTo +
                ", searchString='" + searchString + '\'' +
                ", sortField=" + sortField +
                ", sortDir=" + sortDir +
                ", limit=" + limit +
                ", offset=" + offset +
                ", contractIndependentProject=" + contractIndependentProject +
                ", platformIndependentProject=" + platformIndependentProject +
                '}';
    }
}
