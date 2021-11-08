package ru.protei.portal.ui.delivery.client.activity.pcborder.common;

public interface AbstractPcbOrderCommonInfoEditActivity {

    default void onCardTypeChanged(Long cardTypeId){}

//    void onAmountChanged();

    default void onSaveCommonInfoClicked(){}

    default void onCancelSaveCommonInfoClicked(){}

    void onAmountChanged();

}
