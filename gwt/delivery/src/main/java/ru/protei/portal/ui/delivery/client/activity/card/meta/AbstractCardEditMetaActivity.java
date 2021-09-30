package ru.protei.portal.ui.delivery.client.activity.card.meta;

/**
 * Абстракция активности карточки создания Меты Платы
 */
public interface AbstractCardEditMetaActivity extends AbstractCardCommonMeta {
    void onStateChanged();

    void onManagerChanged();
}
