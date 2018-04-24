package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.DocumentType;

public class DocumentTypeEvents {

    @Url(value = "doctypes", primary = true)
    public static class Show {
    }

    public static class ShowPreview {

        public ShowPreview (HasWidgets parent, DocumentType doctype) {
            this.parent = parent;
            this.doctype = doctype;
        }

        public DocumentType doctype;
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
}
