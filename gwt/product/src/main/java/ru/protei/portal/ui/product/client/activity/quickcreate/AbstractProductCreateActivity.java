package ru.protei.portal.ui.product.client.activity.quickcreate;

/**
 * Активность создания продукта с минимальным набором параметров
 */
public interface AbstractProductCreateActivity {
    void onNameChanged();
    void onSaveClicked();
    void onResetClicked();
}
