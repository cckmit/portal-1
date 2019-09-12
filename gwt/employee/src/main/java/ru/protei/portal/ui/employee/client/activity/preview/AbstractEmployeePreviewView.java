package ru.protei.portal.ui.employee.client.activity.preview;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * Представление превью сотрудника
 */
public interface AbstractEmployeePreviewView extends IsWidget {

    void setActivity(AbstractEmployeePreviewActivity activity );
    void setID( String value );
    HasWidgets getPositionsContainer();

    Widget asWidget(boolean isForTableView);

    void setName(String name);
}
