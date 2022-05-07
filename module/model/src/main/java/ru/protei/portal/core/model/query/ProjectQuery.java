package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.struct.DateRange;
import ru.protei.portal.core.model.struct.Interval;

import java.util.*;

import static ru.protei.portal.core.model.helper.CollectionUtils.emptyIfNull;
import static ru.protei.portal.core.model.helper.CollectionUtils.toList;

/**
 * Запрос по проектам
 */
public class ProjectQuery extends BaseQuery implements HasFilterQueryIds {
    private List<Long> caseIds;

    private Set<Long> stateIds;

    private Set<Long> headManagerIds;

    private Set<Long> caseMemberIds;

    private Set<Long> directionIds;

    private Set<Long> regionIds;

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

    private Long idSearch;

    private Boolean hasContract;

    public ProjectQuery() {
        sortField = En_SortField.case_name;
        sortDir = En_SortDir.ASC;
    }

    public ProjectQuery(String searchString, Long id, En_SortField sortField, En_SortDir sortDir ) {
        super(searchString, sortField, sortDir);
        this.idSearch = id;
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

    public Set<Long> getStateIds() {
        return stateIds;
    }

    public void setStateIds(Set<Long> state) {
        this.stateIds = state;
    }

    public Set<Long> getDistrictIds() {
        return districtIds;
    }

    public void setDistrictIds( Set<Long> districtIds ) {
        this.districtIds = districtIds;
    }

    public Set<Long> getDirectionIds() {
        return directionIds;
    }

    public void setDirectionIds(Set<Long> directionIds) {
        this.directionIds = directionIds;
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

    public Set<Long> getPlatformIds() {
        return new HashSet<Long>();
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

    public Set<Long> getRegionIds() {
        return regionIds;
    }

    public void setRegionIds(Set<Long> regionIds) {
        this.regionIds = regionIds;
    }

    public Set<Long> getHeadManagerIds() {
        return headManagerIds;
    }

    public void setHeadManagerIds(Set<Long> headManagerIds) {
        this.headManagerIds = headManagerIds;
    }

    public Set<Long> getCaseMemberIds() {
        return caseMemberIds;
    }

    public void setCaseMemberIds(Set<Long> caseMemberIds) {
        this.caseMemberIds = caseMemberIds;
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

    public Long getIdSearch() {
        return idSearch;
    }

    public void setIdSearch(Long idSearch) {
        this.idSearch = idSearch;
    }

    public Boolean getHasContract() {
        return hasContract;
    }

    public void setHasContract(Boolean hasContract) {
        this.hasContract = hasContract;
    }

    @Override
    public List<Long> getAllCompanyIds() {
        List<Long> allCompanyIds = new ArrayList<>();
        allCompanyIds.addAll(emptyIfNull(initiatorCompanyIds));
        allCompanyIds.addAll(emptyIfNull(subcontractorIds));

        return allCompanyIds;
    }

    @Override
    public List<Long> getAllPersonIds() {
        List<Long> allPersonIds = new ArrayList<>();
        allPersonIds.addAll(emptyIfNull(caseMemberIds));
        allPersonIds.addAll(emptyIfNull(headManagerIds));

        if (memberId != null) {
            allPersonIds.add(memberId);
        }

        return allPersonIds;
    }

    @Override
    public List<Long> getAllProductIds() {
        return new ArrayList<>(emptyIfNull(productIds));
    }

    @Override
    public List<Long> getAllDirectionIds() {
        return new ArrayList<>(emptyIfNull(directionIds));
    }

    @Override
    public List<Long> getAllTagIds() {
        return new ArrayList<>();
    }

    @Override
    public List<Long> getAllRegionIds() {
        return new ArrayList<>(emptyIfNull(regionIds));
    }

    @Override
    public List<Long> getAllPlatformIds() {
        return new ArrayList<>();
    }

    @Override
    public Long getPlanId() {
        return null;
    }

    @Override
    public boolean isParamsPresent() {
        return super.isParamsPresent() ||
                CollectionUtils.isNotEmpty(caseIds) ||
                CollectionUtils.isNotEmpty(stateIds) ||
                CollectionUtils.isNotEmpty(regionIds) ||
                CollectionUtils.isNotEmpty(headManagerIds) ||
                CollectionUtils.isNotEmpty(caseMemberIds) ||
                CollectionUtils.isNotEmpty(directionIds) ||
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
                isActive ||
                hasContract != null;
    }

    @Override
    public String toString() {
        return "ProjectQuery{" +
                ", caseIds=" + caseIds +
                ", stateIds=" + stateIds +
                ", regionIds=" + regionIds +
                ", headManagerIds=" + headManagerIds +
                ", caseMemberIds=" + caseMemberIds +
                ", directionIds=" + directionIds +
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
                ", hasContract=" + hasContract +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectQuery)) return false;
        ProjectQuery that = (ProjectQuery) o;
        return Objects.equals(caseIds, that.caseIds) &&
                Objects.equals(stateIds, that.stateIds) &&
                Objects.equals(regionIds, that.regionIds) &&
                Objects.equals(headManagerIds, that.headManagerIds) &&
                Objects.equals(caseMemberIds, that.caseMemberIds) &&
                Objects.equals(directionIds, that.directionIds) &&
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
                Objects.equals(isActive, that.isActive) &&
                Objects.equals(hasContract, that.hasContract);
    }

    @Override
    public int hashCode() {
        return Objects.hash(caseIds, stateIds, regionIds, headManagerIds, caseMemberIds, directionIds,
                districtIds, memberId, productIds, customerType, createdFrom, createdTo,
                initiatorCompanyIds, commentCreationRange, pauseDateGreaterThan, deleted,
                subcontractorIds, technicalSupportExpiresInDays, isActive, hasContract);
    }
}
