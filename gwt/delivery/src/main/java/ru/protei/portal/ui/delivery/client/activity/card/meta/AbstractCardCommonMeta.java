package ru.protei.portal.ui.delivery.client.activity.card.meta;

public interface AbstractCardCommonMeta {
    void onTestDateChanged();

    default void onArticleChanged(){}

    String getValidationError();
}
