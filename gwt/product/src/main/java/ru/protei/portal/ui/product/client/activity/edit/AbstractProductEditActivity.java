package ru.protei.portal.ui.product.client.activity.edit;

/**
 * Абстракция активности карточки создания/редактирования продукта
 */
public interface AbstractProductEditActivity {

    void onSaveClicked ();

    void onCancelClicked ();

    void onNameChanged ();

    void onStateChanged ();
}
