package ru.protei.portal.ui.product.client.activity.edit;

import java.util.function.Consumer;

/**
 * Абстракция активности карточки создания/редактирования продукта
 */
public interface AbstractProductEditActivity {

    void onSaveClicked ();

    void onCancelClicked ();

    void onNameChanged ();

    void onStateChanged ();

    void renderMarkdownText(String text, Consumer<String> consumer);

    void onDisplayPreviewChanged( String key, boolean isDisplay );
}
