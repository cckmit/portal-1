package ru.protei.portal.ui.employee.client.activity.item;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Представление сотрудника
 */
public interface AbstractEmployeeItemView extends IsWidget {

    void setActivity( AbstractEmployeeItemActivity activity );

    void setName ( String name );

    void setPhoto ( String photo );

    HasWidgets getPreviewContainer();
}
