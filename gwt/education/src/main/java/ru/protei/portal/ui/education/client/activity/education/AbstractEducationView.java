package ru.protei.portal.ui.education.client.activity.education;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractEducationView extends IsWidget {

    void setActivity(AbstractEducationActivity activity);

    HasWidgets container();

    HasVisibility toggleButtonVisibility();
}
