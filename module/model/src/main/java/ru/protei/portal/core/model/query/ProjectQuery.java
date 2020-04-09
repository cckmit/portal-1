package ru.protei.portal.core.model.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

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

    public CaseQuery toCaseQuery(Long myPersonId) {
        CaseQuery caseQuery = new CaseQuery();
        caseQuery.setType(En_CaseType.PROJECT);

        caseQuery.setCaseIds(this.getCaseIds());

        if (CollectionUtils.isNotEmpty(this.getStates())) {
            caseQuery.setStateIds(this.getStates().stream()
                    .map((state) -> new Long(state.getId()).intValue())
                    .collect(toList())
            );
        }

        if (CollectionUtils.isNotEmpty(this.getRegions())) {
            caseQuery.setRegionIds(this.getRegions().stream()
                    .map(region -> region == null ? null : region.getId())
                    .collect(toList())
            );
        }

        if (CollectionUtils.isNotEmpty(this.getHeadManagers())) {
            caseQuery.setHeadManagerIds(this.getHeadManagers().stream()
                    .map(headManager -> headManager == null ? null : headManager.getId())
                    .collect(toList())
            );
        }

        if (CollectionUtils.isNotEmpty(this.getCaseMembers())) {
            caseQuery.setCaseMemberIds(this.getCaseMembers().stream()
                    .map(member -> member == null ? null : member.getId())
                    .collect(toList())
            );
        }

        if (CollectionUtils.isNotEmpty(this.getDirections())) {
            caseQuery.setProductDirectionIds(this.getDirections().stream()
                    .map(directionInfo -> directionInfo == null ? null : directionInfo.id)
                    .collect(toList())
            );
        }

        if (CollectionUtils.isNotEmpty(this.getProductIds())) {
            caseQuery.setProductIds(this.getProductIds());
        }

        if (this.isOnlyMineProjects() != null && this.isOnlyMineProjects()) {
            caseQuery.setMemberId(myPersonId);
        }

        if (this.getCustomerType() != null) {
            caseQuery.setLocal(this.getCustomerType().getId());
        }

        caseQuery.setCreatedFrom(this.getCreatedFrom());
        caseQuery.setCreatedTo(this.getCreatedTo());
        caseQuery.setSearchString(this.getSearchString());
        caseQuery.setSortDir(this.getSortDir());
        caseQuery.setSortField(this.getSortField());
        caseQuery.setContractIndependentProject(this.getContractIndependentProject());
        caseQuery.setPlatformIndependentProject(this.getPlatformIndependentProject());
        caseQuery.setDistrictIds(this.getDistrictIds());

        return caseQuery;
    }
}
