package ru.protei.portal.ui.delivery.client.activity.edit;

import ru.protei.portal.core.model.dict.En_CommentOrHistoryType;
import ru.protei.portal.core.model.ent.Kit;

import java.util.List;
import java.util.Set;

/**
 * Абстракция активности карточки редактирования Поставки
 */
public interface AbstractDeliveryEditActivity {
    void onBackClicked();

    void onOpenEditViewClicked();

    void onNameAndDescriptionEditClicked();
    void onSelectedTabsChanged(List<En_CommentOrHistoryType> selectedTabs);

    void onSearchKitChanged();

    void onKitEditClicked(Long kitId, String kitName);

    void onRemoveKitsButtonClicked(Set<Kit> value);
}
