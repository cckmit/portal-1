package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class IssueFilterParams implements Serializable {
    private String name;

    private CaseQuery caseQuery;

    private String searchString;

    private En_SortField sortField;

    private En_SortDir sortDir;

    private boolean searchStringAtComments = false;

    private Boolean viewPrivate = null;

    private Date createdFrom;

    private Date createdTo;

    private Date modifiedFrom;

    private Date modifiedTo;

    private List<Integer> importanceIds;

    private List<Integer> stateIds;

    private List<EntityOption> companyEntityOptions;

    private List<PersonShortView> managerPersonShortView;

    private List<PersonShortView> initiatorPersonShortView;

    private List<ProductShortView> productShortView;

    private List<PersonShortView> commentPersonShortView;

    private List<Long> caseTagsIds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CaseQuery getCaseQuery() {
        return caseQuery;
    }

    public void setCaseQuery(CaseQuery caseQuery) {
        this.caseQuery = caseQuery;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public En_SortField getSortField() {
        return sortField;
    }

    public void setSortField(En_SortField sortField) {
        this.sortField = sortField;
    }

    public En_SortDir getSortDir() {
        return sortDir;
    }

    public void setSortDir(En_SortDir sortDir) {
        this.sortDir = sortDir;
    }

    public boolean isSearchStringAtComments() {
        return searchStringAtComments;
    }

    public void setSearchStringAtComments(boolean searchStringAtComments) {
        this.searchStringAtComments = searchStringAtComments;
    }

    public Boolean isViewPrivate() {
        return viewPrivate;
    }

    public void setViewPrivate(Boolean viewPrivate) {
        this.viewPrivate = viewPrivate;
    }

    public Date getCreatedFrom() {
        return createdFrom;
    }

    public void setCreatedFrom(Date createdFrom) {
        this.createdFrom = createdFrom;
    }

    public Date getCreatedTo() {
        return createdTo;
    }

    public void setCreatedTo(Date createdTo) {
        this.createdTo = createdTo;
    }

    public Date getModifiedFrom() {
        return modifiedFrom;
    }

    public void setModifiedFrom(Date modifiedFrom) {
        this.modifiedFrom = modifiedFrom;
    }

    public Date getModifiedTo() {
        return modifiedTo;
    }

    public void setModifiedTo(Date modifiedTo) {
        this.modifiedTo = modifiedTo;
    }

    public List<Integer> getImportanceIds() {
        return importanceIds;
    }

    public void setImportanceIds(List<Integer> importanceIds) {
        this.importanceIds = importanceIds;
    }

    public List<Integer> getStateIds() {
        return stateIds;
    }

    public void setStateIds(List<Integer> stateIds) {
        this.stateIds = stateIds;
    }

    public List<EntityOption> getCompanyEntityOptions() {
        return companyEntityOptions;
    }

    public void setCompanyEntityOptions(List<EntityOption> companyEntityOptions) {
        this.companyEntityOptions = companyEntityOptions;
    }

    public List<PersonShortView> getManagerPersonShortView() {
        return managerPersonShortView;
    }

    public void setManagerPersonShortView(List<PersonShortView> managerPersonShortView) {
        this.managerPersonShortView = managerPersonShortView;
    }

    public List<PersonShortView> getInitiatorPersonShortView() {
        return initiatorPersonShortView;
    }

    public void setInitiatorPersonShortView(List<PersonShortView> initiatorPersonShortView) {
        this.initiatorPersonShortView = initiatorPersonShortView;
    }

    public List<ProductShortView> getProductShortView() {
        return productShortView;
    }

    public void setProductShortView(List<ProductShortView> productShortView) {
        this.productShortView = productShortView;
    }

    public List<PersonShortView> getCommentPersonShortView() {
        return commentPersonShortView;
    }

    public void setCommentPersonShortView(List<PersonShortView> commentPersonShortView) {
        this.commentPersonShortView = commentPersonShortView;
    }

    public List<Long> getCaseTagsIds() {
        return caseTagsIds;
    }

    public void setCaseTagsIds(List<Long> caseTagsIds) {
        this.caseTagsIds = caseTagsIds;
    }

    @Override
    public String toString() {
        return "IssueFilterParams{" +
                "name='" + name + '\'' +
                ", caseQuery=" + caseQuery +
                ", searchString='" + searchString + '\'' +
                ", sortField=" + sortField +
                ", sortDir=" + sortDir +
                ", searchStringAtComments=" + searchStringAtComments +
                ", viewPrivate=" + viewPrivate +
                ", createdFrom=" + createdFrom +
                ", createdTo=" + createdTo +
                ", modifiedFrom=" + modifiedFrom +
                ", modifiedTo=" + modifiedTo +
                ", importanceIds=" + importanceIds +
                ", stateIds=" + stateIds +
                ", companyEntityOptions=" + companyEntityOptions +
                ", managerPersonShortView=" + managerPersonShortView +
                ", initiatorPersonShortView=" + initiatorPersonShortView +
                ", productShortView=" + productShortView +
                ", commentPersonShortView=" + commentPersonShortView +
                ", caseTagsIds=" + caseTagsIds +
                '}';
    }
}
