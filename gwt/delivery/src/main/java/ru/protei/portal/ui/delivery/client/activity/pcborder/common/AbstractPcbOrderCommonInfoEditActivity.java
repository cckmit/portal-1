package ru.protei.portal.ui.delivery.client.activity.pcborder.common;

public interface AbstractPcbOrderCommonInfoEditActivity {

    void onAmountChanged();

    default void onSaveCommonInfoClicked(){}

    default void onCancelSaveCommonInfoClicked(){}
}
