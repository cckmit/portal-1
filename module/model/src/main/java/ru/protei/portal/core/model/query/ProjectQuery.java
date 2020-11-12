package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.dto.ProductDirectionInfo;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.struct.Interval;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.*;

/**
 * Запрос по проектам
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

    private Long memberId;

    private Set<Long> productIds;

    private En_CustomerType customerType;

    private Date createdFrom;

    private Date createdTo;

    private Boolean platformIndependentProject;

    private Set<Long> initiatorCompanyIds;

    private Long pauseDateGreaterThan;

    private Integer deleted;

    private List<Interval> technicalSupportExpiresInDays;

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

    public En_CaseType getType() {
        return type;
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

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
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

    public Set<Long> getInitiatorCompanyIds() {
        return initiatorCompanyIds;
    }

    public void setInitiatorCompanyIds(Set<Long> initiatorCompanyIds) {
        this.initiatorCompanyIds = initiatorCompanyIds;
    }

    public void setType(En_CaseType type) {
        this.type = type;
    }

    public Long getPauseDateGreaterThan() {
        return pauseDateGreaterThan;
    }

    public void setPauseDateGreaterThan(Long pauseDateGreaterThan) {
        this.pauseDateGreaterThan = pauseDateGreaterThan;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public List<Interval> getTechnicalSupportExpiresInDays() {
        return technicalSupportExpiresInDays;
    }

    public void setTechnicalSupportExpiresInDays(List<Interval> technicalSupportExpiresInDays) {
        this.technicalSupportExpiresInDays = technicalSupportExpiresInDays;
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
                platformIndependentProject != null ||
                pauseDateGreaterThan != null ||
                deleted != null ||
                CollectionUtils.isNotEmpty(technicalSupportExpiresInDays);
    }

    @Override
    public String toString() {
        return "ProjectQuery{" +
                "type=" + type +
                ", caseIds=" + caseIds +
                ", states=" + states +
                ", regions=" + regions +
                ", headManagers=" + headManagers +
                ", caseMembers=" + caseMembers +
                ", directions=" + directions +
                ", districtIds=" + districtIds +
                ", memberId=" + memberId +
                ", productIds=" + productIds +
                ", customerType=" + customerType +
                ", createdFrom=" + createdFrom +
                ", createdTo=" + createdTo +
                ", platformIndependentProject=" + platformIndependentProject +
                ", initiatorCompanyIds=" + initiatorCompanyIds +
                ", pauseDateGreaterThan=" + pauseDateGreaterThan +
                ", deleted=" + deleted +
                ", technicalSupportExpiresInDays=" + technicalSupportExpiresInDays +
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
                Objects.equals(memberId, that.memberId) &&
                Objects.equals(productIds, that.productIds) &&
                customerType == that.customerType &&
                Objects.equals(createdFrom, that.createdFrom) &&
                Objects.equals(createdTo, that.createdTo) &&
                Objects.equals(platformIndependentProject, that.platformIndependentProject) &&
                Objects.equals(initiatorCompanyIds, that.initiatorCompanyIds) &&
                Objects.equals(pauseDateGreaterThan, that.pauseDateGreaterThan) &&
                Objects.equals(deleted, that.deleted) &&
                Objects.equals(technicalSupportExpiresInDays, that.technicalSupportExpiresInDays);
    }

    @Override
    public int hashCode() {
        return Objects.hash(caseIds, states, regions, headManagers, caseMembers, directions,
                districtIds, memberId, productIds, customerType, createdFrom, createdTo,
                platformIndependentProject, initiatorCompanyIds, pauseDateGreaterThan, deleted,
                technicalSupportExpiresInDays);
    }
}
