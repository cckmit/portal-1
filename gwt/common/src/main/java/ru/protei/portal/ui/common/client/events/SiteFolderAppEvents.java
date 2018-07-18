package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Name;
import ru.brainworm.factory.context.client.annotation.Url;

/**
 * События приложений
 */
public class SiteFolderAppEvents {

    @Url(value = "sfapps", primary = true)
    public static class Show {
        @Name("server")
        public Long serverId;
        public Show() {
            this(null);
        }
        public Show(Long serverId) {
            this.serverId = serverId;
        }
    }

    @Url(value = "sfapp")
    public static class Edit {
        @Name("app")
        public Long appId;
        @Name("server")
        public Long serverId;
        public Edit() {
            this(null);
        }
        public Edit(Long appId) {
            this.appId = appId;
        }
        public static Edit withServer(Long serverId) {
            Edit edit = new Edit();
            edit.serverId = serverId;
            return edit;
        }
    }

    public static class ShowPreview {
        public ru.protei.portal.core.model.ent.Application app;
        public HasWidgets parent;
        public ShowPreview(HasWidgets parent, ru.protei.portal.core.model.ent.Application app) {
            this.parent = parent;
            this.app = app;
        }
    }

    public static class ShowList {
        public HasWidgets parent;
        public Long serverId;
        public ShowList(HasWidgets parent, Long serverId) {
            this.parent = parent;
            this.serverId = serverId;
        }
    }

    public static class Changed {
        public ru.protei.portal.core.model.ent.Application app;
        public Changed(ru.protei.portal.core.model.ent.Application app) {
            this.app = app;
        }
    }

    public static class ChangeModel {}
}
