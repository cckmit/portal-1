package ru.protei.portal.ui.education.client.activity.admin.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractEducationAdminFilterView extends IsWidget {

    void setActivity(AbstractEducationAdminFilterActivity activity);

    void resetFilter();

    HasValue<Boolean> showOnlyNotApproved();

    HasValue<Boolean> showOutdated();
}
