package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Name;
import ru.brainworm.factory.context.client.annotation.Url;

/**
 * События Site folder
 */
public class SiteFolderEvents {

    /**
     * События платформ
     */
    public static class Platform {

        @Url(value = "sitefolder", primary = true)
        public static class Show {
            public Show() {}
        }

        @Url(value = "sfplatform")
        public static class Edit {
            @Name("platform")
            public Long platformId;
            public Edit() {
                this(null);
            }
            public Edit(Long platformId) {
                this.platformId = platformId;
            }
        }

        public static class ShowPreview {
            public ru.protei.portal.core.model.ent.Platform platform;
            public HasWidgets parent;
            public ShowPreview(HasWidgets parent, ru.protei.portal.core.model.ent.Platform platform) {
                this.parent = parent;
                this.platform = platform;
            }
        }

        public static class Changed {
            public ru.protei.portal.core.model.ent.Platform platform;
            public Changed(ru.protei.portal.core.model.ent.Platform platform) {
                this.platform = platform;
            }
        }

        public static class ChangeModel {}
    }

    /**
     * События серверов
     */
    public static class Server {

        @Url(value = "sfservers", primary = true)
        public static class Show {
            @Name("platform")
            public Long platformId;
            public Show() {
                this(null);
            }
            public Show(Long platformId) {
                this.platformId = platformId;
            }
        }

        @Url(value = "sfserver")
        public static class Edit {
            @Name("server")
            public Long serverId;
            public Edit() {
                this(null);
            }
            public Edit(Long serverId) {
                this.serverId = serverId;
            }
        }

        public static class ShowPreview {
            public ru.protei.portal.core.model.ent.Server server;
            public HasWidgets parent;
            public ShowPreview(HasWidgets parent, ru.protei.portal.core.model.ent.Server server) {
                this.parent = parent;
                this.server = server;
            }
        }

        public static class ShowList {
            public HasWidgets parent;
            public Long platformId;
            public ShowList(HasWidgets parent, Long platformId) {
                this.parent = parent;
                this.platformId = platformId;
            }
        }

        public static class Changed {
            public ru.protei.portal.core.model.ent.Server server;
            public Changed(ru.protei.portal.core.model.ent.Server server) {
                this.server = server;
            }
        }

        public static class ChangeModel {}
    }

    /**
     * События приложений
     */
    public static class App {

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
            public Edit() {
                this(null);
            }
            public Edit(Long appId) {
                this.appId = appId;
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
}
