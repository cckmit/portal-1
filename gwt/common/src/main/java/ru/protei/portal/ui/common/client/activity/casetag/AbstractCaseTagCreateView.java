package ru.protei.portal.ui.common.client.activity.casetag;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.view.EntityOption;

public interface AbstractCaseTagCreateView extends IsWidget {

    void setActivity(AbstractCaseTagCreateActivity activity);

    HasValue<String> name();

    HasValue<String> color();

    HasValue<EntityOption> company();

    void setVisibleCompanyPanel(boolean visible);
}
