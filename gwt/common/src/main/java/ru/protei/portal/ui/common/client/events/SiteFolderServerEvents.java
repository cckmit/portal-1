package ru.protei.portal.ui.common.client.events;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import ru.brainworm.factory.context.client.annotation.Name;
import ru.brainworm.factory.context.client.annotation.Omit;
import ru.brainworm.factory.context.client.annotation.Url;
import ru.protei.portal.core.model.ent.Platform;

/**
 * События серверов
 */
public class SiteFolderServerEvents {

    @Url(value = "sfservers", primary = true)
    public static class ShowSummaryTable {
        @Name("platform")
        public Long platformId;
        @Omit
        public Boolean preScroll;
        public ShowSummaryTable() {
            this(null, false);
        }
        public ShowSummaryTable(Long platformId, Boolean preScroll) {
            this.platformId = platformId;
            this.preScroll = preScroll;
        }
    }

    public static class ShowTable {
        public ShowTable(Panel parent, Platform platform, Runnable onServersLoaded) {
            this(parent, platform, onServersLoaded, false);
        }

        public ShowTable(Panel parent, Platform platform, Runnable onServersLoaded, boolean isPlatformPreview) {
            this.parent = parent;
            this.platform = platform;
            this.isPlatformPreview = isPlatformPreview;
            this.onServersLoaded = onServersLoaded;
        }

        public Panel parent;
        public Platform platform;
        public boolean isPlatformPreview;
        public Runnable onServersLoaded;
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
        public Runnable backEvent;
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
        public Edit withBackEvent(Runnable backEvent) {
            this.backEvent = backEvent;
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

    public static class Changed {
        public ru.protei.portal.core.model.ent.Server server;
        public Changed(ru.protei.portal.core.model.ent.Server server) {
            this.server = server;
        }
    }

    public static class ChangeModel {}
}
