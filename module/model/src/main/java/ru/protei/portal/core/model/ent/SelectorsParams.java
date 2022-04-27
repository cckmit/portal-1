package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dto.ProductDirectionInfo;
import ru.protei.portal.core.model.view.*;

import java.io.Serializable;
import java.util.List;

public class SelectorsParams implements Serializable {
    private List<EntityOption> companyEntityOptions;

    private List<PersonShortView> personShortViews;

    private List<PlatformOption> platforms;

    private List<ProductShortView> productShortViews;

    private List<CaseTag> caseTags;

    private PlanOption planOption;

    private List<Contractor> contractors;

    private List<ProductDirectionInfo> productDirectionInfos;

    private List<EntityOption> regions;

    private List<CaseState> caseStates;

    public List<CaseTag> getCaseTags() {
        return caseTags;
    }

    public void setCaseTags( List<CaseTag> caseTags ) {
        this.caseTags = caseTags;
    }

    public List<EntityOption> getCompanyEntityOptions() {
        return companyEntityOptions;
    }

    public void setCompanyEntityOptions(List<EntityOption> companyEntityOptions) {
        this.companyEntityOptions = companyEntityOptions;
    }

    public List<PersonShortView> getPersonShortViews() {
        return personShortViews;
    }

    public void setPersonShortViews(List<PersonShortView> personShortViews) {
        this.personShortViews = personShortViews;
    }

    public List<PlatformOption> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<PlatformOption> platforms) {
        this.platforms = platforms;
    }

    public List<ProductShortView> getProductShortViews() {
        return productShortViews;
    }

    public void setProductShortViews(List<ProductShortView> productShortViews) {
        this.productShortViews = productShortViews;
    }

    public PlanOption getPlanOption() {
        return planOption;
    }

    public void setPlanOption(PlanOption planOption) {
        this.planOption = planOption;
    }

    public List<Contractor> getContractors() {
        return contractors;
    }

    public void setContractors(List<Contractor> contractors) {
        this.contractors = contractors;
    }

    public List<ProductDirectionInfo> getProductDirectionInfos() {
        return productDirectionInfos;
    }

    public void setProductDirectionInfos(List<ProductDirectionInfo> productDirectionInfos) {
        this.productDirectionInfos = productDirectionInfos;
    }

    public List<EntityOption> getRegions() {
        return regions;
    }

    public void setRegions(List<EntityOption> regions) {
        this.regions = regions;
    }

    public List<CaseState> getCaseStates() {
        return caseStates;
    }

    public void setCaseStates(List<CaseState> caseStates) {
        this.caseStates = caseStates;
    }
}
