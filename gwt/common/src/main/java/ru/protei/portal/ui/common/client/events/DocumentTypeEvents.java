package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.DocumentType;

public class DocumentTypeEvents {

    @Url(value = "doctypes", primary = true)
    public static class Show {
    }

    public static class ShowPreview {

        public ShowPreview(HasWidgets parent, Long id) {
            this.parent = parent;
            this.id = id;
        }

        public Long id;
        public HasWidgets parent;
    }

    @Url("doctype")
    public static class Edit {

        public Long id;

        public Edit() {
            this.id = null;
        }

        public Edit(Long id) {
            this.id = id;
        }

        public static DocumentEvents.Edit byId(Long id) {
            return new DocumentEvents.Edit(id);
        }
    }

    public static class ChangeModel {
    }

    public static class Changed {
        public Changed() {
        }

        public Changed(DocumentType doctype) {
            this.doctype = doctype;
        }

        public DocumentType doctype;
    }

    public static class ShowFullScreen {
        public Long id;

        public ShowFullScreen(Long id) {
            this.id = id;
        }
    }
}
