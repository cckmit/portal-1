package ru.protei.portal.ui.delivery.client.activity.kit.edit;

import ru.protei.portal.core.model.dict.En_CommentOrHistoryType;

import java.util.List;

public interface AbstractDeliveryKitEditActivity {
    void onSelectedTabsChanged(List<En_CommentOrHistoryType> selectedTabs);
}
