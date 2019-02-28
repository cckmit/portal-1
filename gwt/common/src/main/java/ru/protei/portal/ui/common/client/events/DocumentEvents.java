package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.Document;

public class DocumentEvents {

    @Url(value = "docs", primary = true)
    public static class Show {
        public Show() {
        }
    }

    public static class ShowPreview {

        public ShowPreview (HasWidgets parent, Document document) {
            this.parent = parent;
            this.document = document;
        }

        public Document document;
        public HasWidgets parent;
    }

    @Url("doc")
    public static class Edit {

        public Long id;

        public Edit() {
            this.id = null;
        }

        public Edit(Long id) {
            this.id = id;
        }

        public static Edit byId(Long id) {
            return new Edit(id);
        }
    }

    public static class ChangeModel {
    }
}
