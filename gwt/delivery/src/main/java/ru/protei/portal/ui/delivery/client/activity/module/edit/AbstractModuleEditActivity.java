package ru.protei.portal.ui.delivery.client.activity.module.edit;

import ru.protei.portal.core.model.dict.En_CommentOrHistoryType;

import java.util.List;

public interface AbstractModuleEditActivity {
    void onNameAndDescriptionEditClicked();

    void onOpenEditViewClicked();

    void onBackClicked();

    void onSelectedTabsChanged(List<En_CommentOrHistoryType> selectedTabs);
}
