package ru.protei.portal.ui.official.client.activity.preview;

/**
 * Абстракция активности должностного лица в карточке
 */
public interface AbstractOfficialItemActivity {

    void onEditClicked(AbstractOfficialItemView itemView);

    void onRemoveClicked(AbstractOfficialItemView itemView);
}
