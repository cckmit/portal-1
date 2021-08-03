package ru.protei.portal.ui.common.client.events;


import com.google.gwt.user.client.ui.HasWidgets;
import ru.protei.portal.core.model.ent.Module;

public class ModuleEvents {

    public static class Show {
        public Show (HasWidgets parent, Long id) {
            this.parent = parent;
            this.id = id;
        }

        public Long id;
        public HasWidgets parent;
    }

    public static class EditModuleMeta {
        public EditModuleMeta(HasWidgets parent, Module module) {
            this.parent = parent;
            this.module = module;
        }

        public Module module;
        public HasWidgets parent;
    }
}
