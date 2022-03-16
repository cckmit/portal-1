package ru.protei.portal.core.model.query;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.ImportanceLevel;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.DateRange;
import ru.protei.portal.core.model.struct.Pair;

import java.util.*;

import static ru.protei.portal.core.model.helper.CollectionUtils.emptyIfNull;
import static ru.protei.portal.core.model.helper.CollectionUtils.toList;

/**
 * Created by Mike on 02.11.2016.
 */
public class CaseQuery extends BaseQuery implements HasFilterQueryIds {

    @JsonIgnore
    private Long id;

    @JsonIgnore
    private List<Long> caseNumbers;

    private List<Long> caseIds;

    private List<Long> companyIds;

    private List<Long> managerCompanyIds;

    private List<Long> initiatorIds;

    private List<Long> platformIds;

    private Set<Long> productIds;

    private List<Long> locationIds;

    private Set<Long> districtIds;

    private List<Long> managerIds;

    private En_CaseType type;

    private List<Long> stateIds;

    private List<Integer> importanceIds;

    private Pair<Long, Boolean> personIdToIsFavorite;

    /**
     * if true then both states otherwise only non-private state
     */
    private boolean allowViewPrivate = true;

    private Boolean viewPrivate = null;

    @Deprecated
    @JsonAlias({"from", "createdFrom" })
    private Date createdFrom;

    @Deprecated
    @JsonAlias({"to", "createdTo" })
    private Date createdTo;

    @Deprecated
    private Date modifiedFrom;

    @Deprecated
    private Date modifiedTo;

    private DateRange createdRange;

    private DateRange modifiedRange;

    private boolean searchStringAtComments = false;

    private String searchCasenoString;

    private List<Long> commentAuthorIds;

    private List<Long> caseTagsIds;

    private List<String> caseTagsNames;

    private boolean customerSearch = false;

    private Integer local;

    private List<Long> creatorIds;

    private Boolean isCheckImportanceHistory;

    private Long planId;

    private List<Integer> timeElapsedTypeIds;

    private List<Integer> workTriggersIds;

    private Boolean overdueDeadlines;

    private Boolean autoCLose;

    public CaseQuery() {}

    public CaseQuery(Long id) {
        setId(id);
    }

    public CaseQuery( En_CaseType type, String searchString, En_SortField sortField, En_SortDir sortDir ) {
        super(searchString, sortField, sortDir);
        this.type = type;
        this.limit = 1000;
        this.allowViewPrivate = true;
    }

    public CaseQuery(CaseQuery query) {
        setSearchString(query.getSearchString());
        setSortField(query.getSortField());
        setSortDir(query.getSortDir());

        setId(query.getId());
        setCaseNumbers(query.getCaseNumbers());
        setCaseIds(query.getCaseIds());
        setCompanyIds(query.getCompanyIds());
        setManagerCompanyIds(query.getManagerCompanyIds());
        setInitiatorIds(query.getInitiatorIds());
        setProductIds(query.getProductIds());
        setLocationIds(query.getLocationIds());
        setDistrictIds(query.getDistrictIds());
        setType(query.getType());
        setStateIds(query.getStateIds());
        setImportanceIds(query.getImportanceIds());
        setCreatedFrom(query.getCreatedFrom());
        setCreatedTo(query.getCreatedTo());
        setModifiedFrom(query.getModifiedFrom());
        setModifiedTo(query.getModifiedTo());
        setCreatedRange(query.getCreatedRange());
        setModifiedRange(query.getModifiedRange());
        setManagerIds(query.getManagerIds());
        setAllowViewPrivate(query.isAllowViewPrivate());
        setViewPrivate(query.isViewPrivate());
        setSearchStringAtComments(query.isSearchStringAtComments());
        setSearchCasenoString(query.getSearchCasenoString());
        setCommentAuthorIds(query.getCommentAuthorIds());
        setCaseTagsIds(query.getCaseTagsIds());
        setCaseTagsNames(query.getCaseTagsNames());
        setCustomerSearch(query.isCustomerSearch());
        setLocal(query.getLocal());
        setCreatorIds(query.getCreatorIds());
        setPlanId(query.getPlanId());
        setPersonIdToIsFavorite(query.getPersonIdToIsFavorite());
        setTimeElapsedTypeIds(query.getTimeElapsedTypeIds());
        setWorkTriggersIds(query.getWorkTriggersIds());
        setOverdueDeadlines(query.getOverdueDeadlines());
        setAutoCLose(query.getAutoCLose());
        setPlatformIds(query.getPlatformIds());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<Long> getCaseIds() {
        return caseIds;
    }

    public void setCaseIds(List<Long> caseIds) {
        this.caseIds = caseIds;
    }

    public List<Long> getCompanyIds() {
        return companyIds;
    }

    public void setCompanyIds( List<Long> companyIds) {
        this.companyIds = companyIds;
    }

    public List<Long> getInitiatorIds() {
        return initiatorIds;
    }

    public void setInitiatorIds(List<Long> initiatorIds) {
        this.initiatorIds = initiatorIds;
    }

    public List<Long> getPlatformIds() {
        return platformIds;
    }

    public void setPlatformIds(List<Long> platformIds) {
        this.platformIds = platformIds;
    }

    public Set<Long> getProductIds() {
        return productIds;
    }

    public void setProductIds( Set<Long> productIds ) { this.productIds = productIds; }

    public List<Long> getLocationIds() { return locationIds; }

    public void setLocationIds(List<Long> locationIds) { this.locationIds = locationIds; }

    public Set<Long> getDistrictIds() { return districtIds; }

    public void setDistrictIds(Set<Long> districtsIds) { this.districtIds = districtsIds; }

    public En_CaseType getType() {
        return type;
    }

    public void setType( En_CaseType type ) {
        this.type = type;
    }

    public List<Long> getStateIds() {
        return stateIds;
    }

    public void setStateIds(List<Long> stateIds) { this.stateIds = stateIds; }

    public List<Integer> getImportanceIds() { return importanceIds; }

    public void setImportanceIds(List<Integer> importanceIds) { this.importanceIds = importanceIds; }

    public void setImportanceLevels(Iterable<ImportanceLevel> importances) {
        this.importanceIds = importances == null ? null : toList( importances, importanceLevel -> importanceLevel.getId().intValue() );
    }

    public Date getCreatedFrom() { return createdFrom; }

    public void setCreatedFrom( Date createdFrom ) { this.createdFrom = createdFrom; }

    public Date getModifiedFrom() { return modifiedFrom; }

    public void setModifiedFrom( Date modifiedFrom ) { this.modifiedFrom = modifiedFrom; }

    public Date getCreatedTo() { return createdTo; }

    public void setCreatedTo( Date createdTo ) { this.createdTo = createdTo; }

    public Date getModifiedTo() { return modifiedTo; }

    public void setModifiedTo( Date modifiedTo ) { this.modifiedTo = modifiedTo; }

    public DateRange getCreatedRange() { return createdRange; }

    public void setCreatedRange(DateRange createdRange) { this.createdRange = createdRange; }

    public DateRange getModifiedRange() { return modifiedRange; }

    public void setModifiedRange(DateRange modifiedRange) { this.modifiedRange = modifiedRange; }

    public List<Long> getManagerIds() { return managerIds; }

    public void setManagerIds( List<Long> managerIds ) { this.managerIds = managerIds; }

    public boolean isAllowViewPrivate() {
        return allowViewPrivate;
    }

    public void setAllowViewPrivate(boolean isAllowViewPrivate) {
        this.allowViewPrivate = isAllowViewPrivate;
    }

    public boolean isSearchStringAtComments() {
        return searchStringAtComments;
    }

    public void setSearchStringAtComments(boolean searchStringAtComments) {
        this.searchStringAtComments = searchStringAtComments;
    }

    public String getSearchCasenoString() {
        return searchCasenoString;
    }

    public void setSearchCasenoString(String searchCasenoString) {
        this.searchCasenoString = searchCasenoString;
    }

    public Boolean isViewPrivate() {
        return viewPrivate;
    }

    public void setViewPrivate(Boolean viewOnlyPrivate) {
        this.viewPrivate = viewOnlyPrivate;
    }

    public List<Long> getCommentAuthorIds() {
        return commentAuthorIds;
    }

    public void setCommentAuthorIds(List<Long> commentAuthorIds) {
        this.commentAuthorIds = commentAuthorIds;
    }

    public List<Long> getCaseTagsIds() {
        return caseTagsIds;
    }

    public void setCaseTagsIds(List<Long> caseTagsIds) {
        this.caseTagsIds = caseTagsIds;
    }

    public List<String> getCaseTagsNames() {
        return caseTagsNames;
    }

    public void setCaseTagsNames(List<String> caseTagsNames) {
        this.caseTagsNames = caseTagsNames;
    }

    public boolean isCustomerSearch() {
        return customerSearch;
    }

    public void setCustomerSearch(boolean customerSearch) {
        this.customerSearch = customerSearch;
    }

    public Integer getLocal() {
        return local;
    }

    public void setLocal(Integer local) {
        this.local = local;
    }

    public List<Long> getCreatorIds() {
        return creatorIds;
    }

    public void setCreatorIds(List<Long> creatorIds) {
        this.creatorIds = creatorIds;
    }

    public void setCheckImportanceHistory( Boolean isCheckImportanceHistory ) {
        this.isCheckImportanceHistory = isCheckImportanceHistory;
    }

    public Boolean isCheckImportanceHistory() {
        return isCheckImportanceHistory;
    }

    public List<Long> getManagerCompanyIds() {
        return managerCompanyIds;
    }

    public void setManagerCompanyIds(List<Long> managerCompanyIds) {
        this.managerCompanyIds = managerCompanyIds;
    }

    @Override
    public List<Long> getAllCompanyIds() {
        List<Long> companyIds = new ArrayList<>();
        companyIds.addAll(emptyIfNull(getCompanyIds()));
        companyIds.addAll(emptyIfNull(getManagerCompanyIds()));

        return companyIds;
    }

    @Override
    public List<Long> getAllPlatformIds() {
        return new ArrayList<>(emptyIfNull(getPlatformIds()));
    }

    @Override
    public List<Long> getAllPersonIds() {
        List<Long> personsIds = new ArrayList<>();
        personsIds.addAll(emptyIfNull(getManagerIds()));
        personsIds.addAll(emptyIfNull(getInitiatorIds()));
        personsIds.addAll(emptyIfNull(getCommentAuthorIds()));
        personsIds.addAll(emptyIfNull(getCreatorIds()));

        return personsIds;
    }

    @Override
    public List<Long> getAllProductIds() {
        return new ArrayList<>(emptyIfNull(getProductIds()));
    }

    @Override
    public List<Long> getAllDirectionIds() {
        return new ArrayList<>();
    }

    @Override
    public List<Long> getAllTagIds() {
        return new ArrayList<>(emptyIfNull(getCaseTagsIds()));
    }

    @Override
    public List<Long> getAllRegionIds() {
        return new ArrayList<>();
    }

    @Override
    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public Pair<Long, Boolean> getPersonIdToIsFavorite() {
        return personIdToIsFavorite;
    }

    public void setPersonIdToIsFavorite(Pair<Long, Boolean> personIdToIsFavorite) {
        this.personIdToIsFavorite = personIdToIsFavorite;
    }

    public List<Integer> getTimeElapsedTypeIds() {
        return timeElapsedTypeIds;
    }

    public void setTimeElapsedTypeIds(List<Integer> timeElapsedTypeIds) {
        this.timeElapsedTypeIds = timeElapsedTypeIds;
    }

    public List<Integer> getWorkTriggersIds() {
        return workTriggersIds;
    }

    public void setWorkTriggersIds(List<Integer> workTriggersIds) {
        this.workTriggersIds = workTriggersIds;
    }

    public Boolean getOverdueDeadlines() {
        return overdueDeadlines;
    }

    public void setOverdueDeadlines(Boolean overdueDeadlines) {
        this.overdueDeadlines = overdueDeadlines;
    }

    public Boolean getAutoCLose() {
        return autoCLose;
    }

    public void setAutoCLose(Boolean autoCLose) {
        this.autoCLose = autoCLose;
    }

    public boolean isParamsPresent() {
        return super.isParamsPresent() ||
                id != null ||
                CollectionUtils.isNotEmpty(caseNumbers) ||
                CollectionUtils.isNotEmpty(caseIds) ||
                CollectionUtils.isNotEmpty(companyIds) ||
                CollectionUtils.isNotEmpty(managerCompanyIds) ||
                CollectionUtils.isNotEmpty(initiatorIds) ||
                CollectionUtils.isNotEmpty(platformIds) ||
                CollectionUtils.isNotEmpty(productIds) ||
                CollectionUtils.isNotEmpty(locationIds) ||
                CollectionUtils.isNotEmpty(districtIds) ||
                CollectionUtils.isNotEmpty(managerIds) ||
                CollectionUtils.isNotEmpty(stateIds) ||
                CollectionUtils.isNotEmpty(importanceIds) ||
                CollectionUtils.isNotEmpty(creatorIds) ||
                createdRange != null ||
                modifiedRange != null ||
                StringUtils.isNotBlank(searchCasenoString) ||
                CollectionUtils.isNotEmpty(commentAuthorIds) ||
                CollectionUtils.isNotEmpty(caseTagsIds) ||
                CollectionUtils.isNotEmpty(caseTagsNames) ||
                local != null ||
                isCheckImportanceHistory != null ||
                planId != null ||
                personIdToIsFavorite != null ||
                CollectionUtils.isNotEmpty(timeElapsedTypeIds) ||
                CollectionUtils.isNotEmpty(workTriggersIds);
    }

    public boolean isAnySelectedParamPresent() {
        return isUnLimitSelectedParamsPresent() ||
                CollectionUtils.isNotEmpty(stateIds) ||
                CollectionUtils.isNotEmpty(importanceIds) ||
                CollectionUtils.isNotEmpty(managerCompanyIds);
    }

    public boolean isUnLimitSelectedParamsPresent() {
        return super.isParamsPresent() ||
                CollectionUtils.isNotEmpty(caseNumbers) ||
                CollectionUtils.isNotEmpty(caseIds) ||
                CollectionUtils.isNotEmpty(companyIds) ||
                CollectionUtils.isNotEmpty(initiatorIds) ||
                CollectionUtils.isNotEmpty(platformIds) ||
                CollectionUtils.isNotEmpty(productIds) ||
                CollectionUtils.isNotEmpty(locationIds) ||
                CollectionUtils.isNotEmpty(districtIds) ||
                CollectionUtils.isNotEmpty(managerIds) ||
                CollectionUtils.isNotEmpty(creatorIds) ||
                createdRange != null ||
                modifiedRange != null ||
                StringUtils.isNotBlank(searchCasenoString) ||
                CollectionUtils.isNotEmpty(commentAuthorIds) ||
                CollectionUtils.isNotEmpty(caseTagsIds) ||
                CollectionUtils.isNotEmpty(caseTagsNames) ||
                planId != null ||
                personIdToIsFavorite != null ||
                CollectionUtils.isNotEmpty(timeElapsedTypeIds) ||
                CollectionUtils.isNotEmpty(workTriggersIds);
    }

    @Override
    public String toString() {
        return "CaseQuery{" +
                "id=" + id +
                ", caseNumbers=" + caseNumbers +
                ", caseIds=" + caseIds +
                ", companyIds=" + companyIds +
                ", managerCompanyIds=" + managerCompanyIds +
                ", initiatorIds=" + initiatorIds +
                ", platformIds=" + platformIds +
                ", productIds=" + productIds +
                ", locationIds=" + locationIds +
                ", districtIds=" + districtIds +
                ", managerIds=" + managerIds +
                ", type=" + type +
                ", stateIds=" + stateIds +
                ", importanceIds=" + importanceIds +
                ", personIdToIsFavorite=" + personIdToIsFavorite +
                ", allowViewPrivate=" + allowViewPrivate +
                ", viewPrivate=" + viewPrivate +
                ", createdFrom=" + createdFrom +
                ", createdTo=" + createdTo +
                ", modifiedFrom=" + modifiedFrom +
                ", modifiedTo=" + modifiedTo +
                ", createdRange=" + createdRange +
                ", modifiedRange=" + modifiedRange +
                ", searchStringAtComments=" + searchStringAtComments +
                ", searchCasenoString='" + searchCasenoString + '\'' +
                ", commentAuthorIds=" + commentAuthorIds +
                ", caseTagsIds=" + caseTagsIds +
                ", caseTagsNames=" + caseTagsNames +
                ", customerSearch=" + customerSearch +
                ", local=" + local +
                ", creatorIds=" + creatorIds +
                ", isCheckImportanceHistory=" + isCheckImportanceHistory +
                ", planId=" + planId +
                ", timeElapsedTypeIds=" + timeElapsedTypeIds +
                ", workTriggersIds=" + workTriggersIds +
                ", overdueDeadlines=" + overdueDeadlines +
                ", autoClose=" + autoCLose +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaseQuery caseQuery = (CaseQuery) o;
        return allowViewPrivate == caseQuery.allowViewPrivate &&
                searchStringAtComments == caseQuery.searchStringAtComments &&
                customerSearch == caseQuery.customerSearch &&
                Objects.equals(id, caseQuery.id) &&
                Objects.equals(caseNumbers, caseQuery.caseNumbers) &&
                Objects.equals(caseIds, caseQuery.caseIds) &&
                Objects.equals(companyIds, caseQuery.companyIds) &&
                Objects.equals(managerCompanyIds, caseQuery.managerCompanyIds) &&
                Objects.equals(initiatorIds, caseQuery.initiatorIds) &&
                Objects.equals(platformIds, caseQuery.platformIds) &&
                Objects.equals(productIds, caseQuery.productIds) &&
                Objects.equals(locationIds, caseQuery.locationIds) &&
                Objects.equals(districtIds, caseQuery.districtIds) &&
                Objects.equals(managerIds, caseQuery.managerIds) &&
                type == caseQuery.type &&
                Objects.equals(stateIds, caseQuery.stateIds) &&
                Objects.equals(importanceIds, caseQuery.importanceIds) &&
                Objects.equals(viewPrivate, caseQuery.viewPrivate) &&
                Objects.equals(createdRange, caseQuery.createdRange) &&
                Objects.equals(modifiedRange, caseQuery.modifiedRange) &&
                Objects.equals(searchCasenoString, caseQuery.searchCasenoString) &&
                Objects.equals(commentAuthorIds, caseQuery.commentAuthorIds) &&
                Objects.equals(caseTagsIds, caseQuery.caseTagsIds) &&
                Objects.equals(caseTagsNames, caseQuery.caseTagsNames) &&
                Objects.equals(local, caseQuery.local) &&
                Objects.equals(creatorIds, caseQuery.creatorIds) &&
                Objects.equals(planId, caseQuery.planId) &&
                Objects.equals(personIdToIsFavorite, caseQuery.personIdToIsFavorite) &&
                Objects.equals(timeElapsedTypeIds, caseQuery.timeElapsedTypeIds) &&
                Objects.equals(workTriggersIds, caseQuery.workTriggersIds) &&
                Objects.equals(overdueDeadlines, caseQuery.overdueDeadlines) &&
                Objects.equals(autoCLose, caseQuery.autoCLose);

    }

    @Override
    public int hashCode() {
        return Objects.hash(id, caseNumbers, caseIds, companyIds, managerCompanyIds, initiatorIds, platformIds, productIds, locationIds, districtIds, managerIds,
                type, stateIds, importanceIds, allowViewPrivate, viewPrivate, createdRange, modifiedRange,
                searchStringAtComments, searchCasenoString, commentAuthorIds, caseTagsIds, caseTagsNames,
                customerSearch, local, creatorIds, planId, personIdToIsFavorite,
                timeElapsedTypeIds, workTriggersIds, overdueDeadlines, autoCLose);
    }
}
