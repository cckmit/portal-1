package ru.protei.portal.ui.common.client.activity.casehistory.item;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Представление айтема истории
 */
public interface AbstractCaseHistoryItemView extends IsWidget {
    void setActivity(AbstractCaseHistoryItemActivity activity);

    HasVisibility addedValueContainerVisibility();

    HasVisibility removedValueContainerVisibility();

    HasVisibility changeContainerVisibility();

    void setChangeInfoMessage(String changeInfoMessage);

    void setInitiator(String initiator);

    void setAddedValue(String addedValue, String title);

    void setRemovedValue(String removedValue, String title);

    void setOldValue(String oldValue, String title);

    void setNewValue(String newValue, String title);

    void setDate(String date);

    void setAddedValueColor(String backgroundColor);

    void setChangedValueColors(String oldValueColor, String newValueColor);
}
