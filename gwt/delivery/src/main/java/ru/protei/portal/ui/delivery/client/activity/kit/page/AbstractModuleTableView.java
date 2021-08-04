package ru.protei.portal.ui.delivery.client.activity.kit.page;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Module;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AbstractModuleTableView extends IsWidget {
    void setActivity(AbstractKitActivity activity);

    void fillTable(Map<Module, List<Module>> modules);

    void clearSelectedRows();

    void clearModules();

    Set<Module> getSelectedModules();

    void setDeleteEnabled(boolean isEnabled);

    boolean isDeleteEnabled();
}
