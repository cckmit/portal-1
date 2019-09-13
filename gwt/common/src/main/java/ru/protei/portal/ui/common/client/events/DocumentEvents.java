package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.struct.ProjectInfo;

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

        public Edit() {}

        public Edit(Long id) {
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

    public static class Form {

        public static class Show {
            public HasWidgets parent;
            public Document document;
            public String tag;
            public Show(HasWidgets parent, Document document, String tag) {
                this.parent = parent;
                this.document = document;
                this.tag = tag;
            }
        }

        public static class SetProject {
            public String tag;
            public ProjectInfo project;
            public SetProject(ProjectInfo project, String tag) {
                this.project = project;
                this.tag = tag;
            }
        }

        public static class Save {
            public String tag;
            public Save(String tag) {
                this.tag = tag;
            }
        }

        public static class Saved {
            public String tag;
            public Saved(String tag) {
                this.tag = tag;
            }
        }
    }
}
