package ru.protei.portal.ui.common.client.activity.casetag;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractCaseTagCreateView extends IsWidget {

    void setActivity(AbstractCaseTagCreateActivity activity);

    HasValue<String> name();

    HasValue<String> color();
}
