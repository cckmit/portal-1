package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import ru.brainworm.factory.context.client.annotation.Name;
import ru.brainworm.factory.context.client.annotation.Omit;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.Platform;

/**
 * События серверов
 */
public class SiteFolderServerEvents {

    @Url(value = "sfservers", primary = true)
    public static class Show {
        @Name("platform")
        public Long platformId;
        @Omit
        public Boolean preScroll;
        public Show() {
            this(null, false);
        }
        public Show (Long platformId, Boolean preScroll) {
            this.platformId = platformId;
            this.preScroll = preScroll;
        }
    }

    @Url(value = "sfserver")
    public static class Edit {
        @Name("server")
        public Long serverId;
        @Name("clone")
        public Long serverIdToBeCloned;
        @Omit
        public Platform platform;
        @Omit
        public Object source;
        public Edit() {
            this(null);
        }
        public Edit(Long serverId) {
            this.serverId = serverId;
        }
        public static Edit withPlatform(Platform platform) {
            Edit edit = new Edit();
            edit.platform = platform;
            return edit;
        }
        public static Edit withClone(Long serverIdToBeCloned) {
            Edit edit = new Edit();
            edit.serverIdToBeCloned = serverIdToBeCloned;
            return edit;
        }
        public Edit withSource(Object source) {
            this.source = source;
            return this;
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

    public static class ShowDetailedList {
        public HasWidgets parent;
        public Long platformId;
        public ShowDetailedList(HasWidgets parent, Long platformId) {
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
