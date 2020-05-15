package ru.protei.portal.core.model.query;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.protei.portal.core.model.dict.En_CaseType;

import java.util.List;

/**
 * Параметры фильтрации case, полученные через API
 */
public class CaseApiQuery extends BaseQuery {

    private List<Long> managerIds;

    private List<Long> companyIds;

    private List<Long> stateIds;

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

    public En_CaseType getType() {
        return type;
    }
    public void setType( En_CaseType type ) {
        this.type = type;
    }

    public List<Long> getStateIds() { return stateIds; }
    public void setStateIds(List<Long> stateIds) { this.stateIds = stateIds; }

    public String getCreatedFrom() { return createdFrom; }
    public void setCreatedFrom(String createdFrom) { this.createdFrom = createdFrom; }

    public String getCreatedTo() { return createdTo; }
    public void setCreatedTo(String createdTo) { this.createdTo = createdTo; }

    public List<Long> getManagerIds() { return managerIds; }
    public void setManagerIds( List<Long> managerIds ) { this.managerIds = managerIds; }

    public List<Long> getCompanyIds() { return companyIds;}
    public void setCompanyIds(List<Long> companyIds) { this.companyIds = companyIds; }

    public boolean isAllowViewPrivate() {
        return allowViewPrivate;
    }
    public void setAllowViewPrivate(boolean isAllowViewPrivate) {
        this.allowViewPrivate = isAllowViewPrivate;
    }

    @Override
    public String toString() {
        return "CaseApiQuery{" +
                "managerIds=" + managerIds +
                ", states=" + stateIds +
                ", type=" + type +
                ", allowViewPrivate=" + allowViewPrivate +
                ", viewPrivate=" + viewPrivate +
                ", createdFrom='" + createdFrom + '\'' +
                ", createdTo='" + createdTo + '\'' +
                ", companyIds=" + companyIds +
                '}';
    }
}
