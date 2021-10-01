package ru.protei.portal.ui.delivery.client.activity.cardbatch.edit;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractCardBatchEditView extends IsWidget {

    void setActivity(AbstractCardBatchEditActivity activity);

    HasEnabled saveEnabled();

    HTMLPanel getCommonInfoContainer();

    HTMLPanel getMetaContainer();
}
