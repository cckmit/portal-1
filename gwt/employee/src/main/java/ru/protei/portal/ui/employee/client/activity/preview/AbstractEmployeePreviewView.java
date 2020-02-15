package ru.protei.portal.ui.employee.client.activity.preview;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Представление превью сотрудника
 */
public interface AbstractEmployeePreviewView extends IsWidget {
    void setActivity(AbstractEmployeePreviewActivity activity );
    void setID( String value );
    void setIP( String ip );
    void setPhotoUrl(String url);
    HasWidgets getPositionsContainer();
    HasWidgets getInfoContainer();

    void showFullScreen(boolean isFullScreen);
}
