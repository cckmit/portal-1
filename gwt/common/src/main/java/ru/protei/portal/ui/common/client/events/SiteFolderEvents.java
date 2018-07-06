package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Url;

/**
 * События Site folder
 */
public class SiteFolderEvents {

    /**
     * Показать все платформы
     */
    @Url(value = "sitefolder", primary = true)
    public static class Show {
        public Show() {}
    }

    /**
     * События платформ
     */
    public static class Platform {

        @Url(value = "sfplatform")
        public static class Edit {
            public Long platformId;
            public Edit() {
                this(null);
            }
            public Edit (Long platformId) {
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
    }

    /**
     * События серверов
     */
    public static class Server {}

    /**
     * События приложений
     */
    public static class App {}
}
