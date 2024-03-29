package ru.protei.portal.ui.delivery.client.activity.delivery.kit.page;

import ru.protei.portal.core.model.ent.Module;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.delivery.client.view.delivery.module.table.ModuleTableView;

public interface AbstractKitActivity extends ClickColumn.Handler<Module>, AbstractChangeStateHandler {
    void onKitClicked(Long id);

    void onCheckModuleClicked(ModuleTableView moduleTableView);

    void onRemoveModuleClicked(AbstractModuleTableView module);

    void onAddModuleClicked();
}
