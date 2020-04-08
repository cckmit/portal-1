package ru.protei.portal.core.model.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Запрос по регионам
 */
public class ProjectQuery extends BaseQuery {
    @JsonIgnore
    private List<Long> caseIds;

    private Set<En_RegionState> states;

    private Set<EntityOption> regions;

    private Set<PersonShortView> headManagers;

    private Set<PersonShortView> caseMembers;

    private Set<ProductDirectionInfo> directions;

    private Set<Long> districtIds;

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

    public List<Long> getCaseIds() {
        return caseIds;
    }

    public void setCaseId( Long caseId ) {
        this.caseIds = new ArrayList<>();
        this.caseIds.add(caseId);
    }

    public void setCaseIds(List<Long> caseIds) {
        this.caseIds = caseIds;
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

    public Set<ProductDirectionInfo> getDirections() {
        return directions;
    }

    public void setDirections(Set<ProductDirectionInfo> directions) {
        this.directions = directions;
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

    public Set<EntityOption> getRegions() {
        return regions;
    }

    public void setRegions(Set<EntityOption> regions) {
        this.regions = regions;
    }

    public Set<PersonShortView> getHeadManagers() {
        return headManagers;
    }

    public void setHeadManagers(Set<PersonShortView> headManagers) {
        this.headManagers = headManagers;
    }

    public Set<PersonShortView> getCaseMembers() {
        return caseMembers;
    }

    public void setCaseMembers(Set<PersonShortView> caseMembers) {
        this.caseMembers = caseMembers;
    }

    @Override
    public boolean isParamsPresent() {
        return super.isParamsPresent() ||
                CollectionUtils.isNotEmpty(caseIds) ||
                CollectionUtils.isNotEmpty(states) ||
                CollectionUtils.isNotEmpty(regions) ||
                CollectionUtils.isNotEmpty(headManagers) ||
                CollectionUtils.isNotEmpty(caseMembers) ||
                CollectionUtils.isNotEmpty(directions) ||
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
                ", regions=" + regions +
                ", headManagers=" + headManagers +
                ", caseMembers=" + caseMembers +
                ", caseIds=" + caseIds +
                ", districtIds=" + districtIds +
                ", directions=" + directions +
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
