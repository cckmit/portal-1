package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Name;
import ru.brainworm.factory.context.client.annotation.Url;

/**
 * События платформ
 */
public class SiteFolderPlatformEvents {

    @Url(value = "sitefolder", primary = true)
    public static class Show {
        public Show() {}
    }

    @Url(value = "sfplatform")
    public static class Edit {
        @Name("platform")
        public Long platformId;
        @Name("company")
        public Long companyId;
        public Edit() {
            this(null);
        }
        public Edit(Long platformId) {
            this.platformId = platformId;
        }
        public static Edit withCompany(Long companyId) {
            Edit edit = new Edit();
            edit.companyId = companyId;
            return edit;
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
