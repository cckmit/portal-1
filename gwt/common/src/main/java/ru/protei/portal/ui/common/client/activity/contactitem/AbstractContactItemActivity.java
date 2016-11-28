package ru.protei.portal.ui.common.client.activity.contactitem;

/**
 * Абстракция активити элемента списка
 */
public interface AbstractContactItemActivity {
    void onChangeComment( AbstractContactItemView item );
    void onChangeValue( AbstractContactItemView item );
    void onChangeType( AbstractContactItemView item );
}
