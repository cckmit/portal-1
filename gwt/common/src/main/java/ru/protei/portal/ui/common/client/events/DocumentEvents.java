package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Name;
import ru.brainworm.factory.context.client.annotation.Omit;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.Document;

public class DocumentEvents {

    @Url(value = "docs", primary = true)
    public static class Show {
        @Omit
        public Boolean clearScroll = false;
        public Show () {}
        public Show (Boolean clearScroll) {
            this.clearScroll = clearScroll;
        }
    }

    public static class ShowPreview {

        public ShowPreview (HasWidgets parent, Long documentId) {
            this.parent = parent;
            this.documentId = documentId;
        }

        public Long documentId;
        public HasWidgets parent;
    }

    @Url(value = "doc-preview", primary = true)
    public static class ShowPreviewFullScreen {

        public ShowPreviewFullScreen() {}
        public ShowPreviewFullScreen(Long documentId) {
            this.documentId = documentId;
        }

        @Name("id")
        public Long documentId;
    }

    @Url("doc-edit")
    public static class Edit {

        public Long id;

        public Edit() {}

        public Edit(Long id) {
            this.id = id;
        }

        public static Edit byId(Long id) {
            return new Edit(id);
        }
    }

    @Url("doc-edit-from-equipment")
    public static class EditFromEquipment {

        public Long id;

        public EditFromEquipment() {}

        public EditFromEquipment(Long id) {
            this.id = id;
        }

        public static Edit byId(Long id) {
            return new Edit(id);
        }
    }

    @Url("doc-create")
    public static class Create {
        public Create() {
        }
    }

    public static class ChangeModel {
    }

    public static class CreateFromWizard {
        @Omit
        public HasWidgets parent;
        @Omit
        public Document document;

        public CreateFromWizard(){}

        public CreateFromWizard(HasWidgets parent, Document document) {
            this.parent = parent;
            this.document = document;
        }
    }

    public static class Save {
        public Save() {
        }
    }

    @Url(value = "doc-create-with-equipment")
    public static class CreateWithEquipment {
        public CreateWithEquipment() {}

        public CreateWithEquipment(Long equipmentId, Long projectId, String projectName) {
            this.projectId = projectId;
            this.projectName = projectName;
            this.equipmentId = equipmentId;
        }

        public Long equipmentId;
        public Long projectId;
        public String projectName;
    }
}
