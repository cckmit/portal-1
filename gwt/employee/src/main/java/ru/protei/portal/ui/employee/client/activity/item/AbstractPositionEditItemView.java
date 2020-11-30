package ru.protei.portal.ui.employee.client.activity.item;

import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractPositionEditItemView extends IsWidget {
    void setActivity(AbstractPositionEditItemActivity activity);

    void setDepartment(String department);

    void setPosition(String position);

    void setCompany(String position);

    void setContractAgreement(boolean isContractAgreement);

    void setRemovePositionEnable(boolean isEnable);
}
