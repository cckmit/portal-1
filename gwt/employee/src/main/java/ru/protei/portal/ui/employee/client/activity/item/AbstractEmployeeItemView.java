package ru.protei.portal.ui.employee.client.activity.item;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_AbsenceReason;

/**
 * Представление сотрудника
 */
public interface AbstractEmployeeItemView extends IsWidget {

    void setActivity( AbstractEmployeeItemActivity activity );

    void setName( String name, String link );

    void setBirthday( String value );

    void setPhone( String value );

    void setEmail( String value );

    void setPhoto ( String photo );

    void setDepartmentParent( String value );

    void setDepartment( String value );

    void setPosition( String value );

    void setCompany( String value );

    void setIP( String value );

    void setFireDate ( String value );

    void setAbsenceReason( En_AbsenceReason absence );
}
