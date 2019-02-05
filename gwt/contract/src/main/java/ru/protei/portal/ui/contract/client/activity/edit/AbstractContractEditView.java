package ru.protei.portal.ui.contract.client.activity.edit;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractContractEditView extends IsWidget {

    void setActivity(AbstractContractEditActivity activity);

    HasEnabled saveEnabled();
}
