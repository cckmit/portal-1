package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.dto.ProductDirectionInfo;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.struct.DateRange;
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

    private Set<Long> initiatorCompanyIds;

    private DateRange commentCreationRange;

    private Long pauseDateGreaterThan;

    private Integer deleted;

    private Set<Long> subcontractorIds;

    private List<Interval> technicalSupportExpiresInDays;

    private boolean isActive;

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

    public DateRange getCommentCreationRange() {
        return commentCreationRange;
    }

    public void setCommentCreationRange(DateRange commentCreationRange) {
        this.commentCreationRange = commentCreationRange;
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

    public Set<Long> getSubcontractorIds() {
        return subcontractorIds;
    }

    public void setSubcontractorIds(Set<Long> subcontractorIds) {
        this.subcontractorIds = subcontractorIds;
    }

    public boolean getActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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
                CollectionUtils.isNotEmpty(subcontractorIds) ||
                commentCreationRange != null ||
                customerType != null ||
                createdFrom != null ||
                createdTo != null ||
                pauseDateGreaterThan != null ||
                deleted != null ||
                CollectionUtils.isNotEmpty(technicalSupportExpiresInDays) ||
                isActive;
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
                ", initiatorCompanyIds=" + initiatorCompanyIds +
                ", commentCreationRange=" + commentCreationRange +
                ", pauseDateGreaterThan=" + pauseDateGreaterThan +
                ", deleted=" + deleted +
                ", subcontractorIds=" + subcontractorIds +
                ", technicalSupportExpiresInDays=" + technicalSupportExpiresInDays +
                ", isActive=" + isActive +
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
                Objects.equals(initiatorCompanyIds, that.initiatorCompanyIds) &&
                Objects.equals(commentCreationRange, that.commentCreationRange) &&
                Objects.equals(pauseDateGreaterThan, that.pauseDateGreaterThan) &&
                Objects.equals(deleted, that.deleted) &&
                Objects.equals(subcontractorIds, that.subcontractorIds) &&
                Objects.equals(technicalSupportExpiresInDays, that.technicalSupportExpiresInDays) &&
                Objects.equals(isActive, that.isActive);
    }

    @Override
    public int hashCode() {
        return Objects.hash(caseIds, states, regions, headManagers, caseMembers, directions,
                districtIds, memberId, productIds, customerType, createdFrom, createdTo,
                initiatorCompanyIds, commentCreationRange, pauseDateGreaterThan, deleted,
                subcontractorIds, technicalSupportExpiresInDays, isActive);
    }
}
