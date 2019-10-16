package ru.protei.portal.ui.employee.client.activity.item;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.struct.ContactItem;

import java.util.List;
import java.util.stream.Stream;

/**
 * Представление сотрудника
 */
public interface AbstractEmployeeItemView extends IsWidget {

    void setActivity( AbstractEmployeeItemActivity activity );

    void setName ( String name );

    void setBirthday( String value );

    void setPhone( String value );

    void setEmail( String value );

    void setPhoto (String photo );

    void setDepartmentParent(String value);

    void setDepartment(String value );

    void setPosition( String value );

    void setIP(String value );

    void setFireDate (String value );

    HasWidgets getPreviewContainer();
}
