package ru.protei.portal.ui.delivery.client.activity.pcborder.meta;

public interface AbstractPcbOrderMetaActivity {
    default void onStateChanged() {}

    default void onPromptnessChanged() {}

    void onOrderTypeChanged();

    default void onStencilTypeChanged() {}

    default void onContractorChanged() {}

    default void onReadyDateChanged() {}

    default void onReceiptDateChanged() {}

    default void onOrderDateChanged() {}
}
