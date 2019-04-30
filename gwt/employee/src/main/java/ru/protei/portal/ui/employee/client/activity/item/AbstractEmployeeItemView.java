package ru.protei.portal.ui.employee.client.activity.item;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Представление сотрудника
 */
public interface AbstractEmployeeItemView extends IsWidget {

    void setActivity( AbstractEmployeeItemActivity activity );

    void setName ( String name );

    void setBirthday( String value );

    void setPhone( String value );

    void setEmail( String value );

    void setPhoto ( String photo );

    void setDepartment( String value );

    void setPosition( String value );

    HasWidgets getPreviewContainer();
}
