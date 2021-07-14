package ru.protei.portal.ui.delivery.client.activity.kit.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Kit;

import java.util.List;

public interface AbstractKitView extends IsWidget {
    void setActivity(AbstractKitActivity activity);

    void fillKits(List<Kit> kitSet);

    HasWidgets getModuleTableContainer();

    HasWidgets getModuleEditContainer();
}
