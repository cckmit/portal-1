package ru.protei.portal.ui.employee.client.activity.item;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Представление должности
 */
public interface AbstractPositionItemView extends IsWidget {
    void setActivity( AbstractPositionItemActivity activity );
    void setCompany( String value );
    void setDepartment( String value );
    void setPosition( String value );
    void showMainInfo( boolean isMain );
}
