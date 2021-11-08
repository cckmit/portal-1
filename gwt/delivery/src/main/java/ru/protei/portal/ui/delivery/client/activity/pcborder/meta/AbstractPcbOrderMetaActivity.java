package ru.protei.portal.ui.delivery.client.activity.pcborder.meta;

public interface AbstractPcbOrderMetaActivity {

    default void onStateChange(){}

//    void onDeadlineChanged();

    default void onPriorityChange(){}
}
