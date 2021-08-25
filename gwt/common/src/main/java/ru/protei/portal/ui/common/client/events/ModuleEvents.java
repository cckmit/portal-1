package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.Module;

public class ModuleEvents {

    public static class ShowPreview {
        public ShowPreview(HasWidgets parent, Long id) {
            this.parent = parent;
            this.id = id;
        }

        public Long id;
        public HasWidgets parent;
    }

    @Url( value = "module")
    public static class Edit {
        public Edit() {
        }

        public Edit(Long id) {
            this.id = id;
        }

        public Long id;
    }

    public static class EditModuleMeta {
        public EditModuleMeta(HasWidgets parent, Module module, boolean isReadOnly) {
            this.parent = parent;
            this.module = module;
            this.isReadOnly = isReadOnly;
        }

        public Module module;
        public HasWidgets parent;
        public boolean isReadOnly;
    }

    public static class ChangeModule {
        public ChangeModule(Long id) {
            this.id = id;
        }

        public Long id;
    }

    public static class Create {
        public Create(HasWidgets parent, Long kitId, Long deliveryId) {
            this.parent = parent;
            this.kitId = kitId;
            this.deliveryId = deliveryId;
        }

        public HasWidgets parent;
        public Long kitId;
        public Long deliveryId;
    }
}
