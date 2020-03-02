package ru.protei.portal.core.model.query;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Mike on 02.11.2016.
 */
public class CaseQuery extends BaseQuery {

    @JsonIgnore
    private Long id;

    @JsonIgnore
    private List<Long> caseNumbers;

    private List<Long> companyIds;

    private List<Long> initiatorIds;

    private Set<Long> productIds;

    private List<Long> locationIds;

    private Set<Long> districtIds;

    private List<Long> managerIds;

    private En_CaseType type;

    private List<Integer> stateIds;

    private List<Integer> importanceIds;
    /**
     * if true then both states otherwise only non-private state
     */
    private boolean allowViewPrivate = true;

    private Boolean viewPrivate = null;

    @JsonAlias({"from", "createdFrom" })
    private Date createdFrom;

    @JsonAlias({"to", "createdTo" })
    private Date createdTo;

    private Date modifiedFrom;

    private Date modifiedTo;

    private boolean searchStringAtComments = false;

    private String searchCasenoString;

    private Long memberId;

    private List<Long> commentAuthorIds;

    private List<Long> caseTagsIds;

    private boolean customerSearch = false;

    private boolean findRecordByCaseComments;

    private Integer local;

    private Boolean contractIndependentProject;

    private Boolean platformIndependentProject;

    private Long productDirectionId;

    private List<Long> creatorIds;

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
        setCompanyIds(query.getCompanyIds());
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
        setManagerIds(query.getManagerIds());
        setAllowViewPrivate(query.isAllowViewPrivate());
        setViewPrivate(query.isViewPrivate());
        setSearchStringAtComments(query.isSearchStringAtComments());
        setSearchCasenoString(query.getSearchCasenoString());
        setMemberId(query.getMemberId());
        setCommentAuthorIds(query.getCommentAuthorIds());
        setCaseTagsIds(query.getCaseTagsIds());
        setFindRecordByCaseComments(query.isFindRecordByCaseComments());
        setCustomerSearch(query.isCustomerSearch());
        setLocal(query.getLocal());
        setContractIndependentProject(query.getContractIndependentProject());
        setPlatformIndependentProject(query.getPlatformIndependentProject());
        setProductDirectionId(query.getProductDirectionId());
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

    public List<Integer> getStateIds() {
        return stateIds;
    }

    public void setStateIds(List<Integer> stateIds) { this.stateIds = stateIds; }

    @JsonIgnore
    public void setStates(List<En_CaseState> states) {
        List<Integer> stateIds = null;
        if (states != null && !states.isEmpty()){
            stateIds = states.stream().map(En_CaseState::getId).collect(Collectors.toList());
        }
        this.setStateIds(stateIds);
    }

    public List<Integer> getImportanceIds() { return importanceIds; }

    public void setImportanceIds(List<Integer> importanceIds) { this.importanceIds = importanceIds; }

    public Date getCreatedFrom() { return createdFrom; }

    public void setCreatedFrom( Date createdFrom ) { this.createdFrom = createdFrom; }

    public Date getModifiedFrom() { return modifiedFrom; }

    public void setModifiedFrom( Date modifiedFrom ) { this.modifiedFrom = modifiedFrom; }

    public Date getCreatedTo() { return createdTo; }

    public void setCreatedTo( Date createdTo ) { this.createdTo = createdTo; }

    public Date getModifiedTo() { return modifiedTo; }

    public void setModifiedTo( Date modifiedTo ) { this.modifiedTo = modifiedTo; }

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

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
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

    public boolean isFindRecordByCaseComments() {
        return findRecordByCaseComments;
    }

    public void setFindRecordByCaseComments(boolean findRecordByCaseComments) {
        this.findRecordByCaseComments = findRecordByCaseComments;
    }

    public boolean isCustomerSearch() {
        return customerSearch;
    }

    public void setCustomerSearch(boolean customerSearch) {
        this.customerSearch = customerSearch;
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

    public Integer getLocal() {
        return local;
    }

    public void setLocal(Integer local) {
        this.local = local;
    }

    public Long getProductDirectionId() {
        return productDirectionId;
    }

    public void setProductDirectionId(Long productDirectionId) {
        this.productDirectionId = productDirectionId;
    }

    public List<Long> getCreatorIds() {
        return creatorIds;
    }

    public void setCreatorIds(List<Long> creatorIds) {
        this.creatorIds = creatorIds;
    }

    @Override
    public boolean isParamsPresent() {
        return super.isParamsPresent() ||
                id != null ||
                CollectionUtils.isNotEmpty(caseNumbers) ||
                CollectionUtils.isNotEmpty(companyIds) ||
                CollectionUtils.isNotEmpty(initiatorIds) ||
                CollectionUtils.isNotEmpty(productIds) ||
                CollectionUtils.isNotEmpty(locationIds) ||
                CollectionUtils.isNotEmpty(districtIds) ||
                CollectionUtils.isNotEmpty(managerIds) ||
                CollectionUtils.isNotEmpty(stateIds) ||
                CollectionUtils.isNotEmpty(importanceIds) ||
                CollectionUtils.isNotEmpty(creatorIds) ||
                createdFrom != null ||
                createdTo != null ||
                modifiedFrom != null ||
                modifiedTo != null ||
                StringUtils.isNotBlank(searchCasenoString) ||
                memberId != null ||
                CollectionUtils.isNotEmpty(commentAuthorIds) ||
                CollectionUtils.isNotEmpty(caseTagsIds) ||
                local != null ||
                contractIndependentProject != null ||
                platformIndependentProject != null ||
                productDirectionId != null;
    }

    @Override
    public String toString () {
        return "CaseQuery{" +
                "companyIds=" + companyIds +
                ", initiatorIds=" + initiatorIds +
                ", productIds=" + productIds +
                ", locationIds=" + locationIds +
                ", districtIds=" + districtIds +
                ", managerIds=" + managerIds +
                ", type=" + type +
                ", stateIds=" + stateIds +
                ", importanceIds=" + importanceIds +
                ", createdFrom=" + createdFrom +
                ", createdTo=" + createdTo +
                ", modifiedFrom=" + modifiedFrom +
                ", modifiedTo=" + modifiedTo +
                ", showPrivate=" + allowViewPrivate +
                ", searchStringAtComments=" + searchStringAtComments +
                ", searchCasenoString=" + searchCasenoString +
                ", viewPrivate=" + viewPrivate +
                ", memberIds=" + memberId +
                ", commentAuthorIds=" + commentAuthorIds +
                ", caseTagsIds=" + caseTagsIds +
                ", findRecordByCaseComments=" + findRecordByCaseComments +
                ", customerSearch=" + customerSearch +
                ", local=" + local +
                ", contractIndependentProject=" + contractIndependentProject +
                ", platformIndependentProject=" + platformIndependentProject +
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
                findRecordByCaseComments == caseQuery.findRecordByCaseComments &&
                Objects.equals(id, caseQuery.id) &&
                Objects.equals(caseNumbers, caseQuery.caseNumbers) &&
                Objects.equals(companyIds, caseQuery.companyIds) &&
                Objects.equals(initiatorIds, caseQuery.initiatorIds) &&
                Objects.equals(productIds, caseQuery.productIds) &&
                Objects.equals(locationIds, caseQuery.locationIds) &&
                Objects.equals(districtIds, caseQuery.districtIds) &&
                Objects.equals(managerIds, caseQuery.managerIds) &&
                type == caseQuery.type &&
                Objects.equals(stateIds, caseQuery.stateIds) &&
                Objects.equals(importanceIds, caseQuery.importanceIds) &&
                Objects.equals(viewPrivate, caseQuery.viewPrivate) &&
                Objects.equals(createdFrom, caseQuery.createdFrom) &&
                Objects.equals(createdTo, caseQuery.createdTo) &&
                Objects.equals(modifiedFrom, caseQuery.modifiedFrom) &&
                Objects.equals(modifiedTo, caseQuery.modifiedTo) &&
                Objects.equals(searchCasenoString, caseQuery.searchCasenoString) &&
                Objects.equals(memberId, caseQuery.memberId) &&
                Objects.equals(commentAuthorIds, caseQuery.commentAuthorIds) &&
                Objects.equals(caseTagsIds, caseQuery.caseTagsIds) &&
                Objects.equals(local, caseQuery.local) &&
                Objects.equals(contractIndependentProject, caseQuery.contractIndependentProject) &&
                Objects.equals(platformIndependentProject, caseQuery.platformIndependentProject) &&
                Objects.equals(productDirectionId, caseQuery.productDirectionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, caseNumbers, companyIds, initiatorIds, productIds, locationIds, districtIds, managerIds, type, stateIds, importanceIds, allowViewPrivate, viewPrivate, createdFrom, createdTo, modifiedFrom, modifiedTo, searchStringAtComments, searchCasenoString, memberId, commentAuthorIds, caseTagsIds, customerSearch, findRecordByCaseComments, local, contractIndependentProject, platformIndependentProject, productDirectionId);
    }
}
