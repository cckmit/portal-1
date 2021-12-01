package ru.protei.portal.ui.delivery.client.activity.actionmenu;

import ru.protei.portal.core.model.ent.CaseState;

public interface AbstractKitMenuPopupActivity {
    void onCopyClick();

    void onChangeStateClick(CaseState state);

    void onEditClick();

    void onRemoveClick();
}
