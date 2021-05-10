package ru.protei.portal.ui.common.client.widget.table;

public interface HasGroupFunctions<T, G> {
    G makeGroup(T value);
    String makeGroupName(G group);
}
