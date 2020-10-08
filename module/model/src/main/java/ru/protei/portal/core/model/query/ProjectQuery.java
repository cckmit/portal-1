package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.dto.ProductDirectionInfo;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.struct.DateRange;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * Запрос по регионам
 */
public class ProjectQuery extends BaseQuery {
    private En_CaseType type = En_CaseType.PROJECT;

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

    private Long pauseDate;

    private Boolean platformIndependentProject;

    private Set<EntityOption> initiatorCompanyIds;

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

    public Long getPauseDate() {
        return pauseDate;
    }

    public void setPauseDate( Long pauseDate ) {
        this.pauseDate = pauseDate;
    }

    public Set<EntityOption> getInitiatorCompanyIds() {
        return initiatorCompanyIds;
    }

    public void setInitiatorCompanyIds(Set<EntityOption> initiatorCompanyIds) {
        this.initiatorCompanyIds = initiatorCompanyIds;
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
                CollectionUtils.isNotEmpty(initiatorCompanyIds) ||
                customerType != null ||
                createdFrom != null ||
                createdTo != null ||
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
                ", platformIndependentProject=" + platformIndependentProject +
                ", initiatorCompanyIds=" + initiatorCompanyIds +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectQuery)) return false;
        ProjectQuery that = (ProjectQuery) o;
        return Objects.equals(caseIds, that.caseIds) &&
                Objects.equals(states, that.states) &&
                Objects.equals(regions, that.regions) &&
                Objects.equals(headManagers, that.headManagers) &&
                Objects.equals(caseMembers, that.caseMembers) &&
                Objects.equals(directions, that.directions) &&
                Objects.equals(districtIds, that.districtIds) &&
                Objects.equals(onlyMineProjects, that.onlyMineProjects) &&
                Objects.equals(productIds, that.productIds) &&
                customerType == that.customerType &&
                Objects.equals(createdFrom, that.createdFrom) &&
                Objects.equals(createdTo, that.createdTo) &&
                Objects.equals(platformIndependentProject, that.platformIndependentProject) &&
                Objects.equals(initiatorCompanyIds, that.initiatorCompanyIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(caseIds, states, regions, headManagers, caseMembers, directions,
                districtIds, onlyMineProjects, productIds, customerType, createdFrom, createdTo,
                platformIndependentProject, initiatorCompanyIds);
    }

    public CaseQuery toCaseQuery(Long myPersonId) {
        CaseQuery caseQuery = new CaseQuery();
        caseQuery.setType(En_CaseType.PROJECT);

        caseQuery.setCaseIds(this.getCaseIds());

        if (CollectionUtils.isNotEmpty(this.getStates())) {
            caseQuery.setStateIds(this.getStates().stream()
                    .map(state -> state.getId())
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

        caseQuery.setCreatedRange(new DateRange(En_DateIntervalType.FIXED, this.getCreatedFrom(), this.getCreatedTo()));
        caseQuery.setSearchString(this.getSearchString());
        caseQuery.setSortDir(this.getSortDir());
        caseQuery.setSortField(this.getSortField());
        caseQuery.setPlatformIndependentProject(this.getPlatformIndependentProject());
        caseQuery.setDistrictIds(this.getDistrictIds());

        if (CollectionUtils.isNotEmpty(this.getInitiatorCompanyIds())) {
            caseQuery.setCompanyIds(this.getInitiatorCompanyIds().stream()
                    .map(entityOption -> entityOption == null ? null : entityOption.getId())
                    .collect(toList())
            );
        }

        return caseQuery;
    }
}
