package ru.protei.portal.ui.common.client.activity.workerposition.edit;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.view.EntityOption;

public interface AbstractWorkerPositionEditView extends IsWidget {

    void setActivity(AbstractWorkerPositionEditActivity activity);

    HasValue<String> name();

    HasValue<EntityOption> company();

    HasEnabled nameEnabled();

    HasEnabled companyEnabled();
}
