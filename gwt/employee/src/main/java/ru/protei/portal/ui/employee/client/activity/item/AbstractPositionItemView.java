package ru.protei.portal.ui.employee.client.activity.item;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Представление должности
 */
public interface AbstractPositionItemView extends IsWidget {
    void setActivity( AbstractPositionItemActivity activity );

    HasVisibility departmentContainerVisibility();

    HasVisibility departmentHeadContainerVisibility();

    void setDepartment(String value );
    void setDepartmentParent(String value);
    void setPosition(String value );

    void setDepartmentHead(String departmentHead);
}
