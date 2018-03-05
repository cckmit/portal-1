package ru.protei.portal.ui.common.client.events;

import ru.brainworm.factory.context.client.annotation.Url;

public class DocumentationEvents {

    @Url(value = "documentations", primary = true)
    public static class Show {
        public Show() {
        }
    }

    @Url("documentation")
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
}
