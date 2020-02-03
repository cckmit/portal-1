package ru.protei.portal.core.model.ent;

import java.io.Serializable;
import java.util.List;

public class SelectorsParamsRequest implements Serializable {
    private List<Long> companyIds;

    private List<Long> personIds;

    private List<Long> productIds;

    public List<Long> getCompanyIds() {
        return companyIds;
    }

    public void setCompanyIds(List<Long> companyIds) {
        this.companyIds = companyIds;
    }

    public List<Long> getPersonIds() {
        return personIds;
    }

    public void setPersonIds(List<Long> personIds) {
        this.personIds = personIds;
    }

    public List<Long> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<Long> productIds) {
        this.productIds = productIds;
    }
}
