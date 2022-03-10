package ru.protei.portal.ui.delivery.client.activity.delivery.kit.page;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Kit;
import ru.protei.portal.ui.delivery.client.activity.delivery.kit.handler.KitActionsHandler;

import java.util.List;
import java.util.Set;

public interface AbstractKitView extends IsWidget {
    void setHandler(KitActionsHandler handler);

    void setActivity(AbstractKitActivity activity);

    void fillKits(List<Kit> kitSet);

    void updateKit(Kit kit);

    HasWidgets getModulesContainer();

    HasWidgets getModuleEditContainer();

    void makeKitSelected(Long kitId);

    void setKitsActionsEnabled(boolean hasEditPrivileges);

    Set<Kit> getKitsSelected();

    HasVisibility modulesContainerVisibility();

    void setModuleNotSelectedMessageVisible(boolean isVisible);

    void setKitNotSelectedMessageVisible(boolean isVisible);
}
