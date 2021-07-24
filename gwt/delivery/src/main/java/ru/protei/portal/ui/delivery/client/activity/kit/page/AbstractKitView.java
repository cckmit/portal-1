package ru.protei.portal.ui.delivery.client.activity.kit.page;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Kit;
import ru.protei.portal.ui.delivery.client.activity.kit.handler.KitActionsHandler;

import java.util.List;
import java.util.Set;

public interface AbstractKitView extends IsWidget {
    void setHandler(KitActionsHandler handler);

    void setActivity(AbstractKitActivity activity);

    void fillKits(List<Kit> kitSet);

    HasWidgets getModulesContainer();

    HasWidgets getModuleEditContainer();

    void makeKitSelected(Long kitId);

    void setKitsActionsEnabled(boolean hasEditPrivileges);

    Set<Kit> getKitsSelected();
}
