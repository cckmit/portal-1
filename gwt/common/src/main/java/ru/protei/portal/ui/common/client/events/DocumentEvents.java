package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Name;
import ru.brainworm.factory.context.client.annotation.Omit;
import ru.brainworm.factory.context.client.annotation.Url;

public class DocumentEvents {

    @Url(value = "docs", primary = true)
    public static class Show {
        @Omit
        public Boolean preScroll = false;
        public Show () {}
        public Show (Boolean preScroll) {
            this.preScroll = preScroll;
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

    @Url(value = "doc_preview", primary = true)
    public static class ShowPreviewFullScreen {

        public ShowPreviewFullScreen() {}
        public ShowPreviewFullScreen(Long documentId) {
            this.documentId = documentId;
        }

        @Name("id")
        public Long documentId;
    }

    @Url("doc")
    public static class Edit {

        public Long id;

        @Omit
        public Runnable backEvent;

        public Edit() {}

        public Edit(Long id) {
            this.id = id;
        }

        public Edit withBackEvent(Runnable backEvent) {
            this.backEvent = backEvent;
            return this;
        }
    }

    @Url("doc_create")
    public static class Create {
        public Create() {
        }
    }

    public static class SetButtonsEnabled {

        public boolean isEnabled;

        public SetButtonsEnabled(boolean isEnabled) {
            this.isEnabled = isEnabled;
        }
    }

    public static class CreateFromWizard {

        public HasWidgets parent;

        public CreateFromWizard(HasWidgets parent) {
            this.parent = parent;
        }
    }

    public static class Save {
        public Save() {
        }
    }

    public static class SaveAndContinue {
        public SaveAndContinue() {
        }
    }

    @Url(value = "doc_create_with_equipment")
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

    public static class ProjectCreated {}
}
