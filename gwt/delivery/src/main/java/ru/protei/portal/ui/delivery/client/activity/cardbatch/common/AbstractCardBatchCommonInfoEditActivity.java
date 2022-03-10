package ru.protei.portal.ui.delivery.client.activity.cardbatch.common;

public interface AbstractCardBatchCommonInfoEditActivity {

    void onAmountChanged();

    default void onSaveCommonInfoClicked(){}

    default void onCancelSaveCommonInfoClicked(){}
}
