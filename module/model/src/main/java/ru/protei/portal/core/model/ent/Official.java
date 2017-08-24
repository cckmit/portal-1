package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.Date;

/**
 * Матрица принятия решений
 */
public class Official extends CaseShortView {

    private String productName;
    private String info;
    private Date createTime;
    private String numberEmployees;
    private EntityOption region;

    public String getNumberEmployees() {
        return numberEmployees;
    }

    public void setEmployeesNumber(String number) {
        numberEmployees = number;
    }

    public EntityOption getRegion() {
        return region;
    }

    public void setRegion(EntityOption region) {
        this.region = region;
    }
}
