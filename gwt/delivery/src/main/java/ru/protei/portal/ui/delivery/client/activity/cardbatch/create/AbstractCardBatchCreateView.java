package ru.protei.portal.ui.delivery.client.activity.cardbatch.create;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.view.EntityOption;

public interface AbstractCardBatchCreateView extends IsWidget {

    void setActivity(AbstractCardBatchCreateActivity activity);

    HasEnabled saveEnabled();

    HTMLPanel getCommonInfoContainer();

    HTMLPanel getMetaContainer();
}
