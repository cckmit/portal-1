package ru.protei.portal.ui.delivery.client.activity.cardbatch.common;

public interface AbstractCardBatchCommonInfoEditActivity {

    default void onCardTypeChanged(Long cardTypeId){}

    void onAmountChanged();

    default void onSaveCommonInfoClicked(){}

    default void onCancelSaveCommonInfoClicked(){}
}
