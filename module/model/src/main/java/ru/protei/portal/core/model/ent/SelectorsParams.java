package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.ProductShortView;

import java.io.Serializable;
import java.util.List;

public class SelectorsParams implements Serializable {
    private List<EntityOption> companyEntityOptions;

    private List<PersonShortView> personShortViews;

    private List<ProductShortView> productShortViews;

    private List<CaseTag> caseTags;

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

    public List<ProductShortView> getProductShortViews() {
        return productShortViews;
    }

    public void setProductShortViews(List<ProductShortView> productShortViews) {
        this.productShortViews = productShortViews;
    }
}
