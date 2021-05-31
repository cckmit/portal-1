package ru.protei.portal.ui.delivery.client.activity.edit;

import ru.protei.portal.core.model.dict.En_CommentOrHistoryType;

import java.util.List;

/**
 * Абстракция активности карточки редактирования Поставки
 */
public interface AbstractDeliveryEditActivity {
    void onBackClicked();

    void onOpenEditViewClicked();

    void onNameAndDescriptionEditClicked();
    void onSelectedTabsChanged(List<En_CommentOrHistoryType> selectedTabs);
}
