package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.DocumentType;

public class DocumentTypeEvents {

    @Url(value = "doctypes", primary = true)
    public static class Show {}

    public static class ShowPreview {

        public ShowPreview(HasWidgets parent, DocumentType type) {
            this.parent = parent;
            this.type = type;
        }

        public DocumentType type;
        public HasWidgets parent;
    }

    public static class Changed {
        public Changed(DocumentType doctype, boolean needRefreshList) {
            this.doctype = doctype;
            this.needRefreshList = needRefreshList;
        }

        public DocumentType doctype;
        public boolean needRefreshList = false;
    }

    public static class ClosePreview {}
}
