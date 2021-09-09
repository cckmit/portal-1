package ru.protei.portal.ui.common.client.events;


import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Url;
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

    public static class EditMeta {
        public EditMeta(HasWidgets parent, Module module) {
            this.parent = parent;
            this.module = module;
        }

        public Module module;
        public HasWidgets parent;
    }

    public static class Changed {
        public Changed(Long id) {
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

    public static class CancelCreating {}
}
