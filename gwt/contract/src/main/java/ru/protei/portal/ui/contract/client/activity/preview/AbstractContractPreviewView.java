package ru.protei.portal.ui.contract.client.activity.preview;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractContractPreviewView extends IsWidget {
    void setActivity(AbstractContractPreviewActivity activity);

    HasWidgets getCommentsContainer();
}
