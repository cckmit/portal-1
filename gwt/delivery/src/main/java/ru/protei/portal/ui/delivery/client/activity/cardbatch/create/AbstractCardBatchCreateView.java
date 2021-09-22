package ru.protei.portal.ui.delivery.client.activity.cardbatch.create;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractCardBatchCreateView extends IsWidget {

    void setActivity(AbstractCardBatchCreateActivity activity);

    HasEnabled saveEnabled();

    HasValue<String> number();

    HasValue<String> params();
}
