package ru.protei.portal.ui.delivery.client.activity.cardbatch.meta;

public interface AbstractCardBatchMetaActivity {

    default void onStateChange(){}

    void onDeadlineChanged();

    default void onPriorityChange(){}
}
