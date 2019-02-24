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
    private List<Long> categoryIds;

    private boolean onlyHome;

    private boolean isOnlyParentCompanies;

    private boolean sortHomeCompaniesAtBegin;

    private List<Long> companyIds;

    public CompanyQuery() {
        super("", En_SortField.comp_name, En_SortDir.ASC);
    }

    public CompanyQuery(boolean onlyHome) {
        super("", En_SortField.comp_name, En_SortDir.ASC);
        this.onlyHome = onlyHome;
    }

    public CompanyQuery(String searchString, En_SortField sortField, En_SortDir sortDir) {
        super(searchString, sortField, sortDir);
    }

    public List<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    };

    public boolean getOnlyHome() {
        return onlyHome;
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

    public void setCompanyIds( List<Long> companyIds) {
        this.companyIds = companyIds;
    }

    public List<Long> getCompanyIds() {
        return companyIds;
    }

    @Override
    public String toString() {
        return "CompanyQuery{" +
                ", onlyHome=" + onlyHome +
                ", isOnlyParentCompanies=" + isOnlyParentCompanies +
                ", categoryIds=" + categoryIds +
                ", companyIds=" + companyIds +
                '}';
    }

}

