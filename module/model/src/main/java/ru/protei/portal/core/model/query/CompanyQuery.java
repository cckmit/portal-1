package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

import java.util.List;

/**
 * Фильтр по компаниям
 */
public class CompanyQuery extends BaseQuery {

    /**
     * list of company category (a partner, dealer, customer, etc)
     */
    private List<Integer> categoryIds;

    private boolean onlyHome;

    private Boolean synchronizeWith1C;

    private boolean isOnlyParentCompanies;

    private boolean sortHomeCompaniesAtBegin;

    private boolean onlyVisibleFields;

    private List<Long> companyIds;

    private Boolean isShowDeprecated;

    private Boolean isReverseOrder;

    private Boolean isShowHidden;

    public CompanyQuery() {
        super("", En_SortField.comp_name, En_SortDir.ASC);
    }


    public CompanyQuery onlyVisibleFields() {
        this.onlyVisibleFields = true;
        return this;
    }
    public CompanyQuery(boolean onlyHome) {
        super("", En_SortField.comp_name, En_SortDir.ASC);
        this.onlyHome = onlyHome;
    }

    public CompanyQuery(boolean onlyHome, boolean isShowHidden) {
        super("", En_SortField.comp_name, En_SortDir.ASC);
        this.onlyHome = onlyHome;
        this.isShowHidden = isShowHidden;
    }

    public CompanyQuery(String searchString, En_SortField sortField, En_SortDir sortDir, boolean isShowDeprecated) {
        super(searchString, sortField, sortDir);
        this.isShowDeprecated = isShowDeprecated;
    }

    public Boolean getShowHidden() {
        return isShowHidden;
    }

    public void setShowHidden(Boolean showHidden) {
        isShowHidden = showHidden;
    }

    public List<Integer> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Integer> categoryIds) {
        this.categoryIds = categoryIds;
    };

    public boolean getOnlyHome() {
        return onlyHome;
    }

    public Boolean getSynchronizeWith1C() {
        return synchronizeWith1C;
    }

    public void setSynchronizeWith1C(Boolean synchronizeWith1C) {
        this.synchronizeWith1C = synchronizeWith1C;
    }

    public void setOnlyHome(boolean onlyHome) {
        this.onlyHome = onlyHome;
    }

    public void setOnlyParentCompanies( boolean parentIdIsNull ) {
        this.isOnlyParentCompanies = parentIdIsNull;
    }

    public boolean isOnlyParentCompanies() {
        return isOnlyParentCompanies;
    }

    public boolean isSortHomeCompaniesAtBegin() {
        return sortHomeCompaniesAtBegin;
    }

    public void setSortHomeCompaniesAtBegin( boolean sortHomeCompaniesAtBegin ) {
        this.sortHomeCompaniesAtBegin = sortHomeCompaniesAtBegin;
    }

    public boolean isOnlyVisibleFields() {
        return onlyVisibleFields;
    }

    public Boolean isReverseOrder() {
        return isReverseOrder;
    }

    public void setOnlyVisibleFields(boolean onlyVisibleFields) {
        this.onlyVisibleFields = onlyVisibleFields;
    }

    public void setCompanyIds(List<Long> companyIds) {
        this.companyIds = companyIds;
    }

    public List<Long> getCompanyIds() {
        return companyIds;
    }

    public Boolean getShowDeprecated() {
        return isShowDeprecated;
    }

    public void setShowDeprecated(Boolean deprecated) {
        isShowDeprecated = deprecated;
    }

    public CompanyQuery reverseOrder( boolean reverseOrder ) {
        this.isReverseOrder = reverseOrder;
        return this;
    }

    public CompanyQuery synchronizeWith1C( Boolean synchronizeWith1C ) {
        this.synchronizeWith1C = synchronizeWith1C;
        return this;
    }


    @Override
    public String toString() {
        return "CompanyQuery{" +
                ", onlyHome=" + onlyHome +
                ", isOnlyParentCompanies=" + isOnlyParentCompanies +
                ", categoryIds=" + categoryIds +
                ", companyIds=" + companyIds +
                ", reverseOrder=" + isReverseOrder +
                ", synchronizeWith1C=" + synchronizeWith1C +
                '}';
    }
}

