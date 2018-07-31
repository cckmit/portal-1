package ru.protei.portal.core.model.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    private List<Long> productIds;

    private List<Long> managerIds;

    private boolean withoutManager;

    private En_CaseType type;

    private List<Integer> stateIds;

    private List<Integer> importanceIds;
    /**
     * if true then both states otherwise only non-private state
     */
    private boolean allowViewPrivate = true;

    private boolean viewOnlyPrivate = false;

    private Date from;

    private Date to;

    private boolean searchStringAtComments = false;

    private String searchCasenoString;

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
        setProductIds(query.getProductIds());
        setType(query.getType());
        setStateIds(query.getStateIds());
        setImportanceIds(query.getImportanceIds());
        setFrom(query.getFrom());
        setTo(query.getTo());
        setManagerIds(query.getManagerIds());
        setWithoutManager(query.isWithoutManager());
        setAllowViewPrivate(query.isAllowViewPrivate());
        setViewOnlyPrivate(query.isViewOnlyPrivate());
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

    public List<Long> getProductIds() {
        return productIds;
    }

    public void setProductIds( List<Long> productIds ) { this.productIds = productIds; }

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

    public Date getFrom() { return from; }

    public void setFrom( Date from ) { this.from = from; }

    public Date getTo() { return to; }

    public void setTo( Date to ) { this.to = to; }

    public List<Long> getManagerIds() { return managerIds; }

    public void setManagerIds( List<Long> managerIds ) { this.managerIds = managerIds; }

    public boolean isWithoutManager() {
        return withoutManager;
    }

    public void setWithoutManager(boolean withoutManager) {
        this.withoutManager = withoutManager;
    }

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

    public boolean isViewOnlyPrivate() {
        return viewOnlyPrivate;
    }

    public void setViewOnlyPrivate(boolean viewOnlyPrivate) {
        this.viewOnlyPrivate = viewOnlyPrivate;
    }

    @Override
    public String toString () {
        return "CaseQuery{" +
                "companyIds=" + companyIds +
                ", initiatorIds=" + initiatorIds +
                ", productIds=" + productIds +
                ", managerIds=" + managerIds +
                ", withoutManager=" + withoutManager +
                ", type=" + type +
                ", stateIds=" + stateIds +
                ", importanceIds=" + importanceIds +
                ", from=" + from +
                ", to=" + to +
                ", showPrivate=" + allowViewPrivate +
                ", searchStringAtComments=" + searchStringAtComments +
                ", searchCasenoString=" + searchCasenoString +
                ", viewOnlyPrivate=" + viewOnlyPrivate +
                '}';
    }
}
