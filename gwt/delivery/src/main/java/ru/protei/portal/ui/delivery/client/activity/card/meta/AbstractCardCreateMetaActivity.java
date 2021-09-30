package ru.protei.portal.ui.delivery.client.activity.card.meta;

/**
 * Абстракция активности карточки создания Меты Платы
 */
public interface AbstractCardCreateMetaActivity extends AbstractCardCommonMeta {
    void onTypeChange();

    void onCardBatchChange();
}
