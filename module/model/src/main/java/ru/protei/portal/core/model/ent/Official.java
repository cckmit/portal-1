package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.view.CaseShortView;

import java.util.Date;

/**
 * Created by serebryakov on 21/08/17.
 */
public class Official extends CaseShortView {

    private String productName;
    private String info;
    private Date createTime;
    private String numberEmployees;

    public String getNumberEmployees() {
        return numberEmployees;
    }

    public void setEmployeesNumber(String number) {
        numberEmployees = number;
    }
}
