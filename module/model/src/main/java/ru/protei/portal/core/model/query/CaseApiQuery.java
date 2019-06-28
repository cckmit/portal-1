package ru.protei.portal.core.model.query;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Параметры фильтрации case, полученные через API
 */
public class CaseApiQuery extends BaseQuery {

    private Long managerId;

    private List<Long> managerIds;

    private List<Integer> stateIds;

    @JsonIgnore
    private boolean orWithoutManager;

    @JsonIgnore
    private En_CaseType type;
    /**
     * if true then both states otherwise only non-private state
     */
    private boolean allowViewPrivate = true;

    private Boolean viewPrivate = null;

    @JsonAlias({"from", "createdFrom" })
    private String createdFrom;

    @JsonAlias({"to", "createdTo" })
    private String createdTo;

    public CaseApiQuery() {}

/*    public CaseApiQuery(Long managerId, int limit, int offset ) {
        useSort(En_SortField.issue_number, En_SortDir.ASC);
        this.limit = limit;
        this.offset = offset;
        this.allowViewPrivate = true;
    }*/

    public En_CaseType getType() {
        return type;
    }
    public void setType( En_CaseType type ) {
        this.type = type;
    }

    public List<Integer> getStateIds() { return stateIds; }
    public void setStateIds(List<Integer> stateIds) { this.stateIds = stateIds; }

    @JsonIgnore
    public void setStates(List<En_CaseState> states) {
        List<Integer> stateIds = null;
        if (states != null && !states.isEmpty()){
            stateIds = states.stream().map(En_CaseState::getId).collect(Collectors.toList());
        }
        this.setStateIds(stateIds);
    }

/*
    public Date getCreatedFrom() { return createdFrom; }
    public void setCreatedFrom( Date createdFrom ) { this.createdFrom = createdFrom; }

    public Date getCreatedTo() { return createdTo; }
    public void setCreatedTo( Date createdTo ) { this.createdTo = createdTo; }
*/

    public String getCreatedFrom() { return createdFrom; }
    public void setCreatedFrom(String createdFrom) { this.createdFrom = createdFrom; }

    public String getCreatedTo() { return createdTo; }
    public void setCreatedTo(String createdTo) { this.createdTo = createdTo; }

    public Long getManagerId() { return managerId; }
    public void setManagerId(Long managerId) { this.managerId = managerId; }

    public List<Long> getManagerIds() { return managerIds; }
    public void setManagerIds( List<Long> managerIds ) { this.managerIds = managerIds; }

    public boolean isOrWithoutManager() {
        return orWithoutManager;
    }
    public void setOrWithoutManager(boolean withoutManager) {
        this.orWithoutManager = withoutManager;
    }

    public boolean isAllowViewPrivate() {
        return allowViewPrivate;
    }
    public void setAllowViewPrivate(boolean isAllowViewPrivate) {
        this.allowViewPrivate = isAllowViewPrivate;
    }

    @Override
    public String toString() {
        return "CaseApiQuery{" +
                "managerId=" + managerId +
                ", managerIds=" + managerIds +
                ", stateIds=" + stateIds +
                ", orWithoutManager=" + orWithoutManager +
                ", type=" + type +
                ", allowViewPrivate=" + allowViewPrivate +
                ", viewPrivate=" + viewPrivate +
                ", createdFrom='" + createdFrom + '\'' +
                ", createdTo='" + createdTo + '\'' +
                '}';
    }
}
