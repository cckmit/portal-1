package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Name;
import ru.brainworm.factory.context.client.annotation.Omit;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.Company;

/**
 * События платформ
 */
public class SiteFolderPlatformEvents {

    @Url(value = "sfplatforms", primary = true)
    public static class Show {
        @Omit
        public Boolean clearSelection = false;
        public Show() {}
        public Show(Boolean clearSelection) {
            this.clearSelection = clearSelection;
        }
    }

    @Url(value = "sfplatform")
    public static class Edit {
        @Name("platform")
        public Long platformId;
        @Omit
        public Company company;
        public Edit() {
            this(null);
        }
        public Edit(Long platformId) {
            this.platformId = platformId;
        }
        public static Edit withCompany(Company company) {
            Edit edit = new Edit();
            edit.company = company;
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

    @Url(value = "sfplatform_preview", primary = true)
    public static class ShowFullScreen {
        public ShowFullScreen() {}

        public ShowFullScreen(Long platformId) {
            this.platformId = platformId;
        }

        @Name("id")
        public Long platformId;
    }

    public static class Changed {
        public ru.protei.portal.core.model.ent.Platform platform;
        public Changed(ru.protei.portal.core.model.ent.Platform platform) {
            this.platform = platform;
        }
    }

    public static class ChangeModel {}

    public static class ShowConciseTable {

        public ShowConciseTable(HasWidgets parent, Long companyId) {
            this.parent = parent;
            this.companyId = companyId;
        }

        public HasWidgets parent;
        public Long companyId;
    }
}
