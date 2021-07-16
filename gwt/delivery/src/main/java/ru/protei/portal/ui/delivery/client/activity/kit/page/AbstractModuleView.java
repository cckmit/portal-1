package ru.protei.portal.ui.delivery.client.activity.kit.page;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Module;

import java.util.List;

public interface AbstractModuleView extends IsWidget {
    void setActivity(AbstractKitActivity activity);

    void putModules(List<Module> modules);

    void clearModules();
}
