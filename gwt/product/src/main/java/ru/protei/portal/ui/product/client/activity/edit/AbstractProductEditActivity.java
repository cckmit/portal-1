package ru.protei.portal.ui.product.client.activity.edit;

import ru.protei.portal.core.model.dict.En_DevUnitType;

import java.util.function.Consumer;

/**
 * Абстракция активности карточки создания/редактирования продукта
 */
public interface AbstractProductEditActivity {

    void onSaveClicked ();

    void onCancelClicked ();

    void checkName();

    void renderMarkdownText(String text, Consumer<String> consumer);

    void onDisplayPreviewChanged( String key, boolean isDisplay );

    void onTypeChanged(En_DevUnitType type);
}
