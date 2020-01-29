package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.view.EntityOption;

import java.io.Serializable;
import java.util.List;

public class IssueFilterParams implements Serializable {
    private List<EntityOption> companiesEntityOptions;

    public List<EntityOption> getCompaniesEntityOptions() {
        return companiesEntityOptions;
    }

    public void setCompaniesEntityOptions(List<EntityOption> companiesEntityOptions) {
        this.companiesEntityOptions = companiesEntityOptions;
    }

    @Override
    public String toString() {
        return "IssueFilterParams{" +
                "companiesEntityOptions=" + companiesEntityOptions +
                '}';
    }
}
